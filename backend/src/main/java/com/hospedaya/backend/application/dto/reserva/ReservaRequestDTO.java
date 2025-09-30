package com.hospedaya.backend.application.dto.reserva;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservaRequestDTO {

    private Long usuarioId;
    private Long alojamientoId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
