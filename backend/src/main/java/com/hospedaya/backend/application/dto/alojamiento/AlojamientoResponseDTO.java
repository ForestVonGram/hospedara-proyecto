package com.hospedaya.backend.application.dto.alojamiento;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AlojamientoResponseDTO {

    private Long id;
    private String titulo;
    private String descripcion;
    private String direccion;
    private BigDecimal precioPorNoche;
    private Integer maxHuespedes;
    private Long anfitrionId;
    private List<String> imagenes;
    private List<String> servicios;

    // Indicador para UI: tiene reservas activas (PENDIENTE/CONFIRMADA/PAGADA)
    private Boolean hasReservasActivas;
}
