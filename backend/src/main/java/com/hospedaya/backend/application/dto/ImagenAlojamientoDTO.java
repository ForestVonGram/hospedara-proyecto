package com.hospedaya.backend.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para las imágenes asociadas a un alojamiento")
public class ImagenAlojamientoDTO {

    @Schema(description = "ID de la imagen", example = "15")
    private Long id;

    @Schema(description = "ID del alojamiento", example = "2")
    private Long alojamientoId;

    @Schema(description = "URL de la imagen", example = "http://imagenes.com/casa1.jpg")
    private String url;
}
