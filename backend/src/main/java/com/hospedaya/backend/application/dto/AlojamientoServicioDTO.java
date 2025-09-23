package com.hospedaya.backend.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para la relación entre alojamientos y servicios")
public class AlojamientoServicioDTO {

    @Schema(description = "ID de la relación", example = "7")
    private Long id;

    @Schema(description = "ID del alojamiento", example = "2")
    private Long alojamientoId;

    @Schema(description = "ID del servicio asociado", example = "3")
    private Long servicioId;
}
