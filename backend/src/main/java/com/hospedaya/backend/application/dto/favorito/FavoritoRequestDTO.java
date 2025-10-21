package com.hospedaya.backend.application.dto.favorito;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FavoritoRequestDTO {

    @NotNull(message = "usuarioId es obligatorio")
    private Long usuarioId;

    @NotNull(message = "alojamientoId es obligatorio")
    private Long alojamientoId;
}
