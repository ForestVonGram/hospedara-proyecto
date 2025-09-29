package com.hospedaya.backend.infraestructure.repository;

import com.hospedaya.backend.domain.entity.TransaccionPago;
import com.hospedaya.backend.domain.enums.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransaccionPagoRepository extends JpaRepository<TransaccionPago, Long> {

    Optional<TransaccionPago> findByReferenciaExterna(String referenciaExterna);

    List<TransaccionPago> findByPagoId(Long pagoId);

    boolean existsByReferenciaExterna(String referenciaExterna);

    List<TransaccionPago> findByEstadoPago(EstadoPago estadoPago);
}
