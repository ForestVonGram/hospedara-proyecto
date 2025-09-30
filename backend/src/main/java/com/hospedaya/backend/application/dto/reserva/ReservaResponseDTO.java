package com.hospedaya.backend.application.dto.reserva;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReservaResponseDTO {

    private Long id;
    private Long usuarioId;
    private Long alojamientoId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;
    private LocalDateTime fechaCreacion;
}
