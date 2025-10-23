package com.hospedaya.backend.presentation.controller;

import com.hospedaya.backend.application.dto.login.AuthResponse;
import com.hospedaya.backend.application.dto.login.LoginRequest;
import com.hospedaya.backend.domain.entity.Usuario;
import com.hospedaya.backend.infraestructure.repository.UsuarioRepository;
import com.hospedaya.backend.infraestructure.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                          UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Si el usuario (email) no existe, devolver 404 según el requerimiento
        if (!usuarioRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            Map<String, Object> claims = new HashMap<>();
            claims.put("typ", "access");
            String token = jwtUtil.generateToken(request.getEmail(), claims);
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            return ResponseEntity.status(409).body("El email ya está registrado");
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        if (usuario.getActivo() == null) usuario.setActivo(true);
        if (usuario.getFechaRegistro() == null) usuario.setFechaRegistro(java.time.LocalDate.now());
        Usuario saved = usuarioRepository.save(usuario);
        Map<String, Object> claims = new HashMap<>();
        claims.put("typ", "access");
        String token = jwtUtil.generateToken(saved.getEmail(), claims);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
