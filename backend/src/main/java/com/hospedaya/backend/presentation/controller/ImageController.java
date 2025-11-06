package com.hospedaya.backend.presentation.controller;

import com.hospedaya.backend.application.dto.imagen.ImageUploadResponse;
import com.hospedaya.backend.application.service.base.ImageStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/imagenes")
@Tag(name = "Imágenes", description = "Subida y gestión de imágenes (Cloudinary)")
public class ImageController {

    private final ImageStorageService imageStorageService;

    public ImageController(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    @Operation(summary = "Subir imagen", description = "Sube una imagen a Cloudinary. Opcionalmente indicar carpeta: perfiles, alojamientos, etc.")
    @ApiResponse(responseCode = "201", description = "Imagen subida",
            content = @Content(schema = @Schema(implementation = ImageUploadResponse.class)))
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUploadResponse> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "carpeta", required = false, defaultValue = "") String carpeta
    ) {
        ImageUploadResponse resp = imageStorageService.upload(file, carpeta);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @Operation(summary = "Eliminar imagen por publicId")
    @ApiResponse(responseCode = "204", description = "Eliminada")
    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> delete(@PathVariable String publicId) {
        imageStorageService.delete(publicId);
        return ResponseEntity.noContent().build();
    }
}
