package com.hospedaya.backend.presentation.controller;

import com.hospedaya.backend.application.dto.login.LoginRequest;
import com.hospedaya.backend.application.dto.usuario.UsuarioResponseDTO;
import com.hospedaya.backend.application.dto.usuario.UsuarioUpdateDTO;
import com.hospedaya.backend.application.mapper.UsuarioMapper;
import com.hospedaya.backend.application.service.base.UsuarioService;
import com.hospedaya.backend.domain.entity.Usuario;
import com.hospedaya.backend.exception.ResourceNotFoundException;
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
    @Autowired
    private UsuarioMapper usuarioMapper;
    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Operation(summary = "Listar usuarios")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuarios obtenidos correctamente"),
            @ApiResponse(responseCode = "400", description = "Petición inválida"),
            @ApiResponse(responseCode = "404", description = "Usuarios no encontrados")
    })
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        // Importante: devolvemos DTOs planos para evitar ciclos de serialización
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        List<UsuarioResponseDTO> response = usuarios.stream()
                .map(usuarioMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener un usuario por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario obtenido correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "ID inválido")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuarioPorId(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.findById(id);
            UsuarioResponseDTO responseDTO = usuarioMapper.toResponse(usuario);
            return ResponseEntity.ok(responseDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
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
    public ResponseEntity<?> actualizarUsuario(
            @PathVariable Long id,
            @RequestBody @jakarta.validation.Valid UsuarioUpdateDTO dto) {
        try {
            // Construimos un Usuario parcial con los campos presentes en el DTO
            Usuario cambios = new Usuario();
            cambios.setNombre(dto.getNombre());
            cambios.setEmail(dto.getEmail());
            cambios.setPassword(dto.getPassword());
            cambios.setTelefono(dto.getTelefono());
            cambios.setFotoPerfilUrl(dto.getFotoPerfilUrl());

            Usuario actualizado = usuarioService.actualizarUsuario(id, cambios);

            // Respuesta plana, similar a /usuarios/me
            java.util.Map<String, Object> body = new java.util.HashMap<>();
            body.put("id", actualizado.getId());
            body.put("nombre", actualizado.getNombre());
            body.put("email", actualizado.getEmail());
            body.put("telefono", actualizado.getTelefono());
            body.put("fotoPerfilUrl", actualizado.getFotoPerfilUrl());
            body.put("rol", actualizado.getRol() != null ? actualizado.getRol().name() : null);
            body.put("fechaRegistro", actualizado.getFechaRegistro());
            body.put("activo", actualizado.getActivo());

            return ResponseEntity.ok(body);
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

    /**
     * Elimina la cuenta del usuario autenticado validando su contraseña.
     * Pensado para ser llamado desde la configuración de perfil ("Mi cuenta").
     */
    @DeleteMapping("/me")
    public ResponseEntity<?> eliminarMiCuenta(
            org.springframework.security.core.Authentication authentication,
            @RequestBody(required = false) java.util.Map<String, String> body
    ) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        String password = body != null ? body.get("password") : null;
        if (password == null || password.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("La contraseña es requerida para eliminar la cuenta");
        }

        String emailNorm = authentication.getName().trim().toLowerCase();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailIgnoreCase(emailNorm);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");
        }

        try {
            usuarioService.cancelarReservasYEliminarUsuario(usuario.getId());
            return ResponseEntity.ok("Cuenta eliminada correctamente");
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            // Caso típico: el usuario tiene reservas asociadas (pendientes o en curso)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: tienes reservas activas. Debes cancelar estas reservas o esperar a que pase el tiempo de la reserva en curso para borrar tu cuenta.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar la cuenta: " + e.getMessage());
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
