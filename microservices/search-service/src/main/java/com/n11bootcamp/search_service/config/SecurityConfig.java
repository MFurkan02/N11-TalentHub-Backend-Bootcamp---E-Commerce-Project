package com.n11bootcamp.search_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Mikroservislerde genellikle kapalı tutulur
                .cors(cors -> cors.disable()) // CORS zaten Gateway'de hallediliyor
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/search/**").permitAll() // Arama isteklerine izin ver
                        .anyRequest().permitAll() // Şimdilik geliştirme aşamasında her şeye izin ver
                );

        return http.build();
    }
}