package com.hospedaya.backend.application.dto.alojamiento;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AlojamientoRequestDTO {

    private String titulo;
    private String descripcion;
    private String direccion;
    private BigDecimal precioPorNoche;
    private Long anfitrionId;
}
