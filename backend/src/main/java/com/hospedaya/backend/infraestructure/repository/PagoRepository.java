package com.hospedaya.backend.infraestructure.repository;

import com.hospedaya.backend.domain.entity.Pago;
import com.hospedaya.backend.domain.enums.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    Optional<Pago> findByReservaId(Long reserva);

    Optional<Pago> findByReferenciaExterna(String referenciaExterna);

    List<Pago> findByEstadoPago(EstadoPago estadoPago);

    List<Pago> findByFechaCreacionBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<Pago> findByFechaConfirmacionBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
