package com.hospedaya.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para las reservas realizadas por los usuarios")
public class ReservaDTO {

    @Schema(description = "ID de la reserva", example = "5")
    private Long id;

    @Schema(description = "ID del usuario que realiza la reserva", example = "1")
    private Long usuarioId;

    @Schema(description = "ID del alojamiento reservado", example = "2")
    private Long alojamientoId;

    @Schema(description = "Fecha de inicio de la reserva", example = "2025-09-15")
    private String fechaInicio;

    @Schema(description = "Fecha de fin de la reserva", example = "2025-09-20")
    private String fechaFin;

    @Schema(description = "Estado de la reserva", example = "CONFIRMADA")
    private String estado;
}
