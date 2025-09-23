package com.hospedaya.backend.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para los alojamientos guardados como favoritos por los usuarios")
public class FavoritoDTO {

    @Schema(description = "ID del favorito", example = "8")
    private Long id;

    @Schema(description = "ID del usuario que guarda el alojamiento", example = "1")
    private Long usuarioId;

    @Schema(description = "ID del alojamiento marcado como favorito", example = "2")
    private Long alojamientoId;
}
