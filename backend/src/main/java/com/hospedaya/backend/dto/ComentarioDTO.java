package com.hospedaya.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para comentarios de los usuarios en alojamientos")
public class ComentarioDTO {

    @Schema(description = "ID del comentario", example = "12")
    private Long id;

    @Schema(description = "ID del usuario que escribe el comentario", example = "1")
    private Long usuarioId;

    @Schema(description = "ID del alojamiento comentado", example = "2")
    private Long alojamientoId;

    @Schema(description = "Texto del comentario", example = "Muy buen alojamiento, cómodo y limpio")
    private String texto;

    @Schema(description = "Calificación otorgada (1-5)", example = "5")
    private Integer calificacion;
}
