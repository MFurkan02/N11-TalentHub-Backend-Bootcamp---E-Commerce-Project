package com.n11bootcamp.user_service.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import com.n11bootcamp.user_service.entity.ShoppingCart;
import com.n11bootcamp.user_service.entity.User;
import com.n11bootcamp.user_service.repository.UserRepository;
import com.n11bootcamp.user_service.request.LoginRequest;
import com.n11bootcamp.user_service.request.SignupRequest;
import com.n11bootcamp.user_service.request.UpdateUserRequest;
import com.n11bootcamp.user_service.response.JwtResponse;
import com.n11bootcamp.user_service.response.MessageResponse;
import jakarta.validation.Valid;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

    // ?? JWT Authentication i�in gerekli config de�erleri application.yml�den okunuyor
    @Value("${jwt.issuer_uri}")   String jwtIssuerUri;
    @Value("${jwt.client_id}")    String jwtClientId;
    @Value("${jwt.client_secret}")String jwtClientSecret;
    @Value("${jwt.grant_type}")   String jwtGrantType;
    @Value("${jwt.scope}")        String jwtScope;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;
    // ?? Di�er microservice�lerle haberle�mek i�in (ShoppingCartService vs.)


    private String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * ?? Kullan�c� giri�ini do�rular, Identity Provider�dan JWT al�r.
     */
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {

        // 1. Kullanıcıyı DB'den bul
        User user;
        try {
            user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("User not found!"));
        }

        // 2. Şifre kontrolü
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("User credentials are not valid"));
        }

        // 3. Refresh token üret
        String refreshToken = generateRefreshToken();
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(7));

        // 🔥 KRİTİK: DB'YE KAYDET (senin eksikti)
        userRepository.save(user);

        // 4. JWT token request
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(jwtIssuerUri.trim());

        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("grant_type", jwtGrantType.trim()));
        params.add(new BasicNameValuePair("client_id", jwtClientId.trim()));
        params.add(new BasicNameValuePair("client_secret", jwtClientSecret.trim()));
        params.add(new BasicNameValuePair("username", loginRequest.getUsername().trim()));
        params.add(new BasicNameValuePair("password", loginRequest.getPassword().trim()));

        String accessToken;

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse response = httpClient.execute(httpPost);

            String responseBody = EntityUtils.toString(response.getEntity());

            System.out.println("JWT Response: " + responseBody);

            accessToken = extractAccessToken(responseBody);

            // 🔥 TOKEN CHECK
            if (accessToken == null || accessToken.isEmpty()) {
                return ResponseEntity.status(500)
                        .body(new MessageResponse("Access token generation failed"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(new MessageResponse("Authentication service error"));
        }

        try {
            ShoppingCart cart = restTemplate.getForObject(
                    "http://SHOPPING-CART-SERVICE/api/shopping-cart/by-name/" + user.getUsername(),
                    ShoppingCart.class
            );

            if (cart == null) {
                restTemplate.postForObject(
                        "http://SHOPPING-CART-SERVICE/api/shopping-cart?name=" + user.getUsername(),
                        null,
                        ShoppingCart.class
                );
            }

        } catch (Exception e) {
            // cart service down olsa bile login bozulmasın
            System.out.println("Cart service unavailable: " + e.getMessage());
        }

        // 5. Response
        return ResponseEntity.ok(
                new JwtResponse(
                        accessToken,
                        refreshToken,
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole()
                )
        );
    }

    /**
     * ?? JSON response�tan "access_token" de�erini ��kar�r.
     */
    private static String extractAccessToken(String jsonResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            return rootNode.path("access_token").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * ?? Yeni kullan�c� kayd�
     */
    public ResponseEntity<?> registerUser(@Valid SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Email is already in use!"));
        }

        PasswordEncoder encoder = new BCryptPasswordEncoder();

        // ?? Yeni kullan�c�y� olu�tur ve kaydet
        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                "Customer"
        );

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    /**
     * ? Kullan�c�y� sil.
     * - �nce ShoppingCartService �a�r�l�r (kullan�c�n�n sepeti varsa silinir).
     * - Sonra User DB�den silinir.
     */
    public ResponseEntity<?> deleteUser(Long userId) {
        try {
            // Kullan�c� var m� kontrol et
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found!"));

            try {
                // Kullan�c�n�n shopping cart��n� bul
                ShoppingCart shoppingCart = restTemplate.getForObject(
                        "http://SHOPPING-CART-SERVICE/api/shopping-cart/by-name/" + user.getUsername(),
                        ShoppingCart.class);

                // Shopping cart varsa sil
                restTemplate.delete("http://SHOPPING-CART-SERVICE/api/shopping-cart/" + shoppingCart.getId());
            } catch (Exception e) {
                // Sepet bulunamazsa sorun de�il, user silmeye devam
            }

            // Kullan�c�y� sil
            userRepository.delete(user);

            return ResponseEntity.ok(new MessageResponse("User account deleted successfully!"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new MessageResponse("Internal Server Error"));
        }
    }

    /**
     * ?? Kullan�c� bilgilerini g�ncelle
     */
    public ResponseEntity<?> updateUser(Long userId, UpdateUserRequest updateUserRequest) {
        try {
            // Kullan�c�y� bul
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found!"));

            // �ifre g�ncelle
            if (updateUserRequest.getPassword() != null && !updateUserRequest.getPassword().isEmpty()) {
                PasswordEncoder encoder = new BCryptPasswordEncoder();
                user.setPassword(encoder.encode(updateUserRequest.getPassword()));
            }

            // Email g�ncelle
            if (updateUserRequest.getEmail() != null && !updateUserRequest.getEmail().isEmpty()) {
                if (userRepository.existsByEmail(updateUserRequest.getEmail())) {
                    return ResponseEntity.badRequest().body(new MessageResponse("Email is already in use!"));
                }
                user.setEmail(updateUserRequest.getEmail());
            }

            // Kaydet
            userRepository.save(user);

            return ResponseEntity.ok(new MessageResponse("User account updated successfully!"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new MessageResponse("Internal Server Error"));
        }
    }

    public JwtResponse refreshToken(String refreshToken) {

        // 1. User'ı bul
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        // 2. Expiry check
        if (user.getRefreshTokenExpiry() == null ||
                user.getRefreshTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        // 3. 🔥 YENİ ACCESS TOKEN (external auth server)
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(jwtIssuerUri.trim());

        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("grant_type", "refresh_token"));
        params.add(new BasicNameValuePair("client_id", jwtClientId.trim()));
        params.add(new BasicNameValuePair("client_secret", jwtClientSecret.trim()));
        params.add(new BasicNameValuePair("refresh_token", refreshToken));

        String newAccessToken;

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse response = httpClient.execute(httpPost);

            String responseBody = EntityUtils.toString(response.getEntity());

            newAccessToken = extractAccessToken(responseBody);

            if (newAccessToken == null || newAccessToken.isEmpty()) {
                throw new RuntimeException("Token refresh failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Refresh token request failed");
        }

        // 4. Response
        return new JwtResponse(
                newAccessToken,
                refreshToken,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }
}

