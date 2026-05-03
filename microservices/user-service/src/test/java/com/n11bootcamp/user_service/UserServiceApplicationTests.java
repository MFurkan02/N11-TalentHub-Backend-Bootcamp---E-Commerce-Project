package com.n11bootcamp.user_service;

import com.n11bootcamp.user_service.request.SignupRequest;
import com.n11bootcamp.user_service.entity.User;
import com.n11bootcamp.user_service.repository.UserRepository;
import com.n11bootcamp.user_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito'yu etkinleştirir
class UserServiceTest {

    @Mock
    private UserRepository userRepository; // Veritabanını simüle ederiz

    @InjectMocks
    private UserService userService; // Mockları bu servisin içine enjekte ederiz

    private SignupRequest signupRequest;

    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequest();
        signupRequest.setUsername("furkan");
        signupRequest.setEmail("furkan@mail.com");
        signupRequest.setPassword("123456");
    }

    @Test
    void registerUser_Success_ShouldReturnCreated() {
        // GIVEN: Kullanıcı adı sistemde daha önce yokmuş gibi davran
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(new User());

        ResponseEntity<?> response = userService.registerUser(signupRequest);

        assertTrue(response.getStatusCode().is2xxSuccessful());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_AlreadyExists_ShouldReturnError() {
        when(userRepository.existsByUsername("furkan")).thenReturn(true);

        ResponseEntity<?> response = userService.registerUser(signupRequest);

        assertTrue(response.getStatusCode().is4xxClientError());

        verify(userRepository, never()).save(any(User.class));
    }
}