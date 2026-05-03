package com.n11bootcamp.user_service.controller;

import com.n11bootcamp.user_service.request.LoginRequest;
import com.n11bootcamp.user_service.request.RefreshRequest;
import com.n11bootcamp.user_service.request.SignupRequest;
import com.n11bootcamp.user_service.request.UpdateUserRequest;
import com.n11bootcamp.user_service.service.UserService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;




import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Authentication & Management", description = "Kullanıcı kayıt, giriş ve profil yönetimi işlemlerini içerir.")
public class UserController {

    @Autowired
    UserService userService;

    @Operation(summary = "Kullanıcı Girişi", description = "Kullanıcı adı ve şifre ile JWT token alır.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Giriş başarılı, token döndürüldü"),
            @ApiResponse(responseCode = "401", description = "Hatalı kullanıcı adı veya şifre")
    })
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return userService.authenticateUser(loginRequest);
    }

    @Operation(summary = "Yeni Kullanıcı Kaydı", description = "Sisteme yeni bir kullanıcı hesabı oluşturur.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Kullanıcı başarıyla oluşturuldu"),
            @ApiResponse(responseCode = "400", description = "Geçersiz giriş verileri veya zaten mevcut kullanıcı")
    })
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        return userService.registerUser(signUpRequest);
    }

    @Operation(summary = "Kullanıcı Silme", description = "ID üzerinden belirtilen kullanıcıyı sistemden kalıcı olarak siler.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kullanıcı silindi"),
            @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı")
    })
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        return userService.deleteUser(userId);
    }

    @Operation(summary = "Kullanıcı Güncelleme", description = "Kullanıcının e-posta veya şifre gibi bilgilerini günceller.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Güncelleme başarılı"),
            @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı")
    })
    @PutMapping("/update/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId,
                                        @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        return userService.updateUser(userId, updateUserRequest);
    }

    @Operation(summary = "Token Yenileme (Refresh Token)", description = "Süresi dolmuş Access Token'ı yeni bir tane ile değiştirmek için Refresh Token kullanır.")
    @ApiResponse(responseCode = "200", description = "Yeni token başarıyla üretildi")
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {
        return ResponseEntity.ok(userService.refreshToken(request.getRefreshToken()));
    }
}