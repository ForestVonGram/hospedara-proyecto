package com.hospedaya.backend.application.dto.favorito;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FavoritoResponseDTO {

    private Long id;
    private Long usuarioId;
    private Long alojamientoId;
    private LocalDateTime fechaAgregado;
}
