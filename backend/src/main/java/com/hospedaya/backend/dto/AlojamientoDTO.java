package com.hospedaya.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para los alojamientos registrados por anfitriones")
public class AlojamientoDTO {

    @Schema(description = "ID del alojamiento", example = "2")
    private Long id;

    @Schema(description = "Nombre del alojamiento", example = "Casa rural")
    private String nombre;

    @Schema(description = "Descripción del alojamiento", example = "Acogedora casa en el campo con chimenea")
    private String descripcion;

    @Schema(description = "Dirección del alojamiento", example = "Calle 123, Salento")
    private String direccion;

    @Schema(description = "Precio por noche", example = "75.5")
    private Double precioPorNoche;

    @Schema(description = "ID del anfitrión", example = "1")
    private Long anfitrionId;
}
