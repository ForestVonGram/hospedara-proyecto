package com.hospedaya.backend.application.dto.alojamientoservicio;

import lombok.Data;

@Data
public class AlojamientoServicioResponseDTO {

    // Datos de la relación
    private Long relacionId; // id de la relación alojamiento-servicio
    private Long alojamientoId;
    private Long servicioId;
    private String detalle;

    // Datos del servicio asociado (mantener por compatibilidad)
    private Long id; // id del servicio (compatibilidad)
    private String nombre;
    private String descripcion;
}
