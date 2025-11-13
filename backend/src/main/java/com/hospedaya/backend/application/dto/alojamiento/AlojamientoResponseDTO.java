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
    private Long anfitrionId;
    private Double latitud;
    private Double longitud;
    private List<String> imagenes;
    private List<String> servicios;
}
