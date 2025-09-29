package com.hospedaya.backend.infraestructure.repository;

import com.hospedaya.backend.domain.entity.Reserva;
import com.hospedaya.backend.domain.enums.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    Optional<Reserva> findByUsuarioId(Long usuarioId);

    Optional<Reserva> findByAlojamientoId(Long alojamientoId);

    List<Reserva> findByEstadoReserva(EstadoReserva estado);

    List<Reserva> findByFechaInicioBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
