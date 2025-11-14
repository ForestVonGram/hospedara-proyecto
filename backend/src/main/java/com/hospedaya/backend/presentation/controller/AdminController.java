package com.hospedaya.backend.presentation.controller;

import com.hospedaya.backend.application.service.base.UsuarioService;
import com.hospedaya.backend.domain.entity.Usuario;
import com.hospedaya.backend.domain.enums.Rol;
import com.hospedaya.backend.infraestructure.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@Tag(name = "Administración", description = "Funciones administrativas del sistema")
public class AdminController {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    public AdminController(UsuarioRepository usuarioRepository, UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/summary")
    @Operation(summary = "Resumen de métricas básicas de usuarios")
    public ResponseEntity<Map<String, Object>> resumen() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        long total = usuarios.size();
        long activos = usuarios.stream().filter(u -> Boolean.TRUE.equals(u.getActivo())).count();
        long inactivos = total - activos;
        Map<String, Long> porRol = new HashMap<>();
        for (Rol r : Rol.values()) {
            long count = usuarios.stream().filter(u -> r.equals(u.getRol())).count();
            porRol.put(r.name(), count);
        }
        Map<String, Object> resp = new HashMap<>();
        resp.put("totalUsuarios", total);
        resp.put("activos", activos);
        resp.put("inactivos", inactivos);
        resp.put("porRol", porRol);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/usuarios")
    @Operation(summary = "Listar todos los usuarios")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    @PatchMapping("/usuarios/{id}/activar")
    @Operation(summary = "Activar un usuario")
    public ResponseEntity<Usuario> activar(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.activarUsuario(id));
    }

    @PatchMapping("/usuarios/{id}/desactivar")
    @Operation(summary = "Desactivar un usuario")
    public ResponseEntity<Usuario> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.desactivarUsuario(id));
    }

    @PatchMapping("/usuarios/{id}/rol")
    @Operation(summary = "Cambiar el rol de un usuario")
    public ResponseEntity<?> cambiarRol(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String rolStr = body != null ? body.get("rol") : null;
        if (rolStr == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Campo 'rol' es requerido");
        }
        try {
            Rol nuevo = Rol.valueOf(rolStr.trim().toUpperCase());
            Usuario actualizado = usuarioService.asignarRol(id, nuevo);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Rol inválido: " + rolStr);
        }
    }
}
