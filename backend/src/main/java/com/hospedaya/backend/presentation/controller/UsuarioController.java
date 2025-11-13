package com.hospedaya.backend.presentation.controller;

import com.hospedaya.backend.application.dto.login.LoginRequest;
import com.hospedaya.backend.application.service.base.UsuarioService;
import com.hospedaya.backend.domain.entity.Usuario;
import com.hospedaya.backend.infraestructure.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Operaciones sobre usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private UsuarioService usuarioService;

    @Operation(summary = "Listar usuarios")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuarios obtenidos correctamente"),
            @ApiResponse(responseCode = "400", description = "Petición inválida"),
            @ApiResponse(responseCode = "404", description = "Usuarios no encontrados")
    })
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return ResponseEntity.ok(usuarios);
    }

    @Operation(
            summary = "Crear un usuario",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo usuario",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{ \"nombre\": \"David Gómez\", \"email\": \"david@example.com\", \"password\": \"123456\" }"
                            )
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Error al crear usuario")
    })
    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario usuario) {
        Usuario guardado = usuarioService.crearUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        try {
            Usuario actualizado = usuarioService.actualizarUsuario(id, usuario);
            return ResponseEntity.ok(actualizado);
        } catch (com.hospedaya.backend.exception.ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/foto")
    public ResponseEntity<?> subirFotoPerfil(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            Usuario usuario = usuarioService.findById(id);
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Archivo vacío");
            }
            // Crear directorio si no existe
            Path uploadDir = Paths.get("uploads/perfiles");
            Files.createDirectories(uploadDir);
            // Nombre del archivo
            String filename = "usuario-" + id + "-" + System.currentTimeMillis() + "-" + file.getOriginalFilename();
            Path destination = uploadDir.resolve(filename);
            Files.write(destination, file.getBytes());
            // Guardar URL pública
            String publicUrl = "/uploads/perfiles/" + filename;
            usuario.setFotoPerfilUrl(publicUrl);
            usuarioService.actualizarUsuario(id, usuario);
            return ResponseEntity.ok(publicUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.ok("Usuario eliminado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado con ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el usuario: " + e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getPerfil(org.springframework.security.core.Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }
        // Buscar por email sin sensibilidad a mayúsculas/minúsculas para evitar fallos por casing
        return usuarioRepository.findByEmailIgnoreCase(authentication.getName())
                .<ResponseEntity<?>>map(u -> {
                    // Devolver un DTO plano para evitar ciclos de serialización y datos sensibles
                    java.util.Map<String, Object> dto = new java.util.HashMap<>();
                    dto.put("id", u.getId());
                    dto.put("nombre", u.getNombre());
                    dto.put("email", u.getEmail());
                    dto.put("telefono", u.getTelefono());
                    dto.put("fotoPerfilUrl", u.getFotoPerfilUrl());
                    dto.put("rol", u.getRol() != null ? u.getRol().name() : null);
                    dto.put("fechaRegistro", u.getFechaRegistro());
                    dto.put("activo", u.getActivo());
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado"));
    }
}
