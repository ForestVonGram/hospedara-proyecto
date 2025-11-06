package com.hospedaya.backend.application.dto.imagen;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadResponse {
    private String url;        // URL segura accesible públicamente
    private String publicId;   // ID público de Cloudinary para futuras eliminaciones
    private String format;     // jpg/png/webp, etc.
    private long bytes;        // tamaño del archivo
    private int width;         // ancho si aplica
    private int height;        // alto si aplica
}
