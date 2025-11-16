package com.hospedaya.backend.presentation.controller;

import com.hospedaya.backend.application.dto.login.AuthResponse;
import com.hospedaya.backend.application.dto.login.LoginRequest;
import com.hospedaya.backend.application.service.integration.EmailService;
import com.hospedaya.backend.domain.entity.PasswordResetToken;
import com.hospedaya.backend.domain.entity.Usuario;
import com.hospedaya.backend.domain.enums.Rol;
import com.hospedaya.backend.infraestructure.repository.PasswordResetTokenRepository;
import com.hospedaya.backend.infraestructure.repository.UsuarioRepository;
import com.hospedaya.backend.infraestructure.security.JwtUtil;
import org.springframework.http.HttpStatus;
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

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 15;

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

        // Buscar usuario ignorando mayúsculas/minúsculas
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(emailNorm)
                .orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        // Si la cuenta está bloqueada y aún no ha pasado el tiempo, rechazar login
        if (Boolean.TRUE.equals(usuario.getActivo()) && usuario.getAccountLockedUntil() != null
                && usuario.getAccountLockedUntil().isAfter(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.LOCKED)
                    .body("Cuenta bloqueada por múltiples intentos fallidos. Intenta de nuevo más tarde.");
        }

        // Si ya pasó el tiempo de bloqueo, resetear estado
        if (usuario.getAccountLockedUntil() != null
                && usuario.getAccountLockedUntil().isBefore(LocalDateTime.now())) {
            usuario.setFailedLoginAttempts(0);
            usuario.setAccountLockedUntil(null);
            usuario.setLastFailedLoginAt(null);
            usuarioRepository.save(usuario);
        }

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(emailNorm, request.getPassword())
            );

            // Login correcto: resetear contador de intentos fallidos
            usuario.setFailedLoginAttempts(0);
            usuario.setAccountLockedUntil(null);
            usuario.setLastFailedLoginAt(null);
            usuarioRepository.save(usuario);

            Map<String, Object> claims = new HashMap<>();
            claims.put("typ", "access");
            String token = jwtUtil.generateToken(emailNorm, claims);
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (BadCredentialsException ex) {
            // Incrementar intentos fallidos y aplicar bloqueo si supera el umbral
            int intentos = usuario.getFailedLoginAttempts() != null ? usuario.getFailedLoginAttempts() : 0;
            intentos++;
            usuario.setFailedLoginAttempts(intentos);
            usuario.setLastFailedLoginAt(LocalDateTime.now());

            if (intentos >= MAX_FAILED_ATTEMPTS) {
                usuario.setAccountLockedUntil(LocalDateTime.now().plusMinutes(LOCK_MINUTES));
            }

            usuarioRepository.save(usuario);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
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
        if (usuario.getRol() == null) usuario.setRol(Rol.USUARIO);
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

        // Regenerar token JWT tras cambio de contraseña
        Map<String, Object> claims = new HashMap<>();
        claims.put("typ", "access");
        String tokenJwt = jwtUtil.generateToken(usuario.getEmail(), claims);

        return ResponseEntity.ok(new AuthResponse(tokenJwt));
    }
}
