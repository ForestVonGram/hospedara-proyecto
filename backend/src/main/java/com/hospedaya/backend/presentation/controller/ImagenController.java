package com.hospedaya.backend.presentation.controller;

import com.hospedaya.backend.application.service.integration.CloudinaryService;
import com.hospedaya.backend.domain.entity.Usuario;
import com.hospedaya.backend.infraestructure.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para subida de imágenes
 */
@RestController
@RequestMapping("/imagenes")
public class ImagenController {

    private final CloudinaryService cloudinaryService;
    private final UsuarioRepository usuarioRepository;

    public ImagenController(CloudinaryService cloudinaryService, UsuarioRepository usuarioRepository) {
        this.cloudinaryService = cloudinaryService;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Subir foto de perfil de usuario
     */
    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        try {
            // Validar archivo
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("El archivo está vacío");
            }

            // Validar tipo de archivo
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("El archivo debe ser una imagen");
            }

            // Validar tamaño (máximo 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("La imagen no puede superar 5MB");
            }

            // Subir a Cloudinary
            String imageUrl = cloudinaryService.uploadImage(file, "avatars");

            // Actualizar usuario
            String email = authentication.getName();
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Eliminar imagen anterior si existe
            if (usuario.getFotoPerfilUrl() != null && usuario.getFotoPerfilUrl().contains("cloudinary")) {
                String oldPublicId = cloudinaryService.extractPublicId(usuario.getFotoPerfilUrl());
                if (oldPublicId != null) {
                    cloudinaryService.deleteImage(oldPublicId);
                }
            }

            usuario.setFotoPerfilUrl(imageUrl);
            usuarioRepository.save(usuario);

            Map<String, String> response = new HashMap<>();
            response.put("url", imageUrl);
            response.put("message", "Avatar actualizado exitosamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al subir imagen: " + e.getMessage());
        }
    }

    /**
     * Subir imágenes de alojamiento
     */
    @PostMapping("/alojamiento")
    public ResponseEntity<?> uploadAlojamientoImage(
            @RequestParam("file") MultipartFile file
    ) {
        try {
            // Validar archivo
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("El archivo está vacío");
            }

            // Validar tipo de archivo
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("El archivo debe ser una imagen");
            }

            // Validar tamaño (máximo 10MB para alojamientos)
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("La imagen no puede superar 10MB");
            }

            // Subir a Cloudinary
            String imageUrl = cloudinaryService.uploadImage(file, "alojamientos");

            Map<String, String> response = new HashMap<>();
            response.put("url", imageUrl);
            response.put("message", "Imagen subida exitosamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al subir imagen: " + e.getMessage());
        }
    }

    /**
     * Subir múltiples imágenes de alojamiento
     */
    @PostMapping("/alojamiento/multiple")
    public ResponseEntity<?> uploadMultipleAlojamientoImages(
            @RequestParam("files") MultipartFile[] files
    ) {
        try {
            if (files.length == 0) {
                return ResponseEntity.badRequest().body("No se enviaron archivos");
            }

            if (files.length > 10) {
                return ResponseEntity.badRequest().body("Máximo 10 imágenes por alojamiento");
            }

            java.util.List<String> urls = new java.util.ArrayList<>();

            for (MultipartFile file : files) {
                // Validar cada archivo
                if (file.isEmpty()) continue;

                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    continue;
                }

                if (file.getSize() > 10 * 1024 * 1024) {
                    continue;
                }

                // Subir a Cloudinary
                String imageUrl = cloudinaryService.uploadImage(file, "alojamientos");
                urls.add(imageUrl);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("urls", urls);
            response.put("count", urls.size());
            response.put("message", urls.size() + " imágenes subidas exitosamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al subir imágenes: " + e.getMessage());
        }
    }
}
