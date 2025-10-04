package com.hospedaya.backend.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desactiva CSRF para peticiones POST sin token
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Permite todo sin autenticación
                )
                .httpBasic(httpBasic -> httpBasic.disable()) // Desactiva autenticación básica
                .formLogin(form -> form.disable()); // Desactiva el formulario de login por defecto

        return http.build();
    }
}
