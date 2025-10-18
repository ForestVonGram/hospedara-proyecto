package com.hospedaya.backend.application.dto.alojamientoservicio;

import lombok.Data;

@Data
public class AlojamientoServicioResponseDTO {

    // Datos del servicio asociado que se espera en la respuesta
    private Long id; // id del servicio
    private String nombre;
    private String descripcion;
}
