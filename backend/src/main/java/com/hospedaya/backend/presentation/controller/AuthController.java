package com.hospedaya.backend.presentation.controller;

import com.hospedaya.backend.application.dto.login.AuthResponse;
import com.hospedaya.backend.application.dto.login.LoginRequest;
import com.hospedaya.backend.application.service.integration.EmailService;
import com.hospedaya.backend.domain.entity.PasswordResetToken;
import com.hospedaya.backend.domain.entity.Usuario;
import com.hospedaya.backend.infraestructure.repository.PasswordResetTokenRepository;
import com.hospedaya.backend.infraestructure.repository.UsuarioRepository;
import com.hospedaya.backend.infraestructure.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                          UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                          EmailService emailService, PasswordResetTokenRepository passwordResetTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String emailNorm = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : null;
        if (emailNorm == null || emailNorm.isEmpty()) {
            return ResponseEntity.badRequest().body("Email es requerido");
        }
        // Verificación insensible a mayúsculas/minúsculas
        if (!usuarioRepository.existsByEmailIgnoreCase(emailNorm)) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(emailNorm, request.getPassword())
            );
            Map<String, Object> claims = new HashMap<>();
            claims.put("typ", "access");
            String token = jwtUtil.generateToken(emailNorm, claims);
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario usuario) {
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("Email es requerido");
        }
        // Normalizar email
        String emailNorm = usuario.getEmail().trim().toLowerCase();
        usuario.setEmail(emailNorm);
        if (usuarioRepository.existsByEmailIgnoreCase(emailNorm)) {
            return ResponseEntity.status(409).body("El email ya está registrado");
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        if (usuario.getActivo() == null) usuario.setActivo(true);
        if (usuario.getFechaRegistro() == null) usuario.setFechaRegistro(java.time.LocalDate.now());
        Usuario saved = usuarioRepository.save(usuario);
        
        // Enviar correo de bienvenida
        emailService.enviarCorreoRegistro(saved);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("typ", "access");
        String token = jwtUtil.generateToken(saved.getEmail(), claims);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("Email es requerido");
        }
        String emailNorm = email.trim().toLowerCase();

        usuarioRepository.findByEmailIgnoreCase(emailNorm).ifPresent(usuario -> {
            // Crear token y guardar
            PasswordResetToken token = new PasswordResetToken();
            token.setToken(UUID.randomUUID().toString());
            token.setUsuario(usuario);
            token.setExpiracion(LocalDateTime.now().plusHours(1));
            passwordResetTokenRepository.save(token);

            String resetLink = "http://localhost:4200/reset-password?token=" + token.getToken();
            emailService.enviarCorreoRecuperacion(usuario, resetLink);
        });

        // Siempre devolver 200 para no filtrar existencia de emails
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String tokenStr = request.get("token");
        String newPassword = request.get("newPassword");
        if (tokenStr == null || tokenStr.isBlank() || newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body("Token y nueva contraseña son requeridos");
        }

        var tokenOpt = passwordResetTokenRepository.findByToken(tokenStr);
        if (tokenOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Token inválido");
        }
        var token = tokenOpt.get();
        if (Boolean.TRUE.equals(token.getUsado()) || token.getExpiracion().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Token expirado o usado");
        }

        var usuario = token.getUsuario();
        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);

        token.setUsado(true);
        passwordResetTokenRepository.save(token);

        return ResponseEntity.ok().build();
    }
}
