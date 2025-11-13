package com.hospedaya.backend.infraestructure.repository;

import com.hospedaya.backend.domain.entity.Reserva;
import com.hospedaya.backend.domain.enums.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByUsuarioId(Long usuarioId);

    List<Reserva> findByAlojamientoId(Long alojamientoId);

    List<Reserva> findByEstado(EstadoReserva estado);

    List<Reserva> findByFechaInicioBetween(LocalDate fechaInicio, LocalDate fechaFin);

    boolean existsByAlojamientoIdAndEstadoIn(Long alojamientoId, Collection<EstadoReserva> estados);
}
