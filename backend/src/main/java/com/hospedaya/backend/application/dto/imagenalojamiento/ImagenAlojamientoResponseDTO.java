package com.hospedaya.backend.application.dto.imagenalojamiento;

import lombok.Data;

@Data
public class ImagenAlojamientoResponseDTO {

    private Long id;
    private Long alojamientoId;
    private String url;
}
