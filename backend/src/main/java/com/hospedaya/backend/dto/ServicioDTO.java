package com.hospedaya.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para servicios ofrecidos en los alojamientos")
public class ServicioDTO {

    @Schema(description = "ID del servicio", example = "3")
    private Long id;

    @Schema(description = "Nombre del servicio", example = "Piscina")
    private String nombre;

    @Schema(description = "Descripción del servicio", example = "Piscina climatizada disponible todo el año")
    private String descripcion;
}
