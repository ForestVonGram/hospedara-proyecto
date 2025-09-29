package com.hospedaya.backend.application.service.base;

import com.hospedaya.backend.domain.entity.Reserva;

import java.util.List;

public interface ReservaService {

    Reserva crearReserva(Reserva reserva);
    Reserva obtenerReservaPorId(Long id);
    List<Reserva> listarReservas();
    void cancelarReserva(Long id);
}
