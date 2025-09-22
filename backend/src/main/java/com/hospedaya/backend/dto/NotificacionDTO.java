package com.hospedaya.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para notificaciones enviadas a usuarios")
public class NotificacionDTO {

    @Schema(description = "ID de la notificación", example = "10")
    private Long id;

    @Schema(description = "ID del usuario que recibe la notificación", example = "1")
    private Long usuarioId;

    @Schema(description = "Mensaje de la notificación", example = "Tu reserva ha sido confirmada")
    private String mensaje;

    @Schema(description = "Estado de la notificación", example = "LEIDA")
    private String estado;
}
