package com.n11bootcamp.product_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 1. API isteklerine izin ver
                        .requestMatchers("/api/product/**").permitAll()
                        // 2. Resim klasörüne giden isteklere TAM izin ver (Önemli!)
                        .requestMatchers("/images/**").permitAll()
                        // 3. Static resource'ları (resimleri) security dışına çıkar
                        .requestMatchers("/images/products/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}