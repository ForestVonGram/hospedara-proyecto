package com.hospedaya.backend.infraestructure.repository;

import com.hospedaya.backend.domain.entity.AlojamientoServicio;
import com.hospedaya.backend.domain.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlojamientoServicioRepository extends JpaRepository<AlojamientoServicio, Long> {

    Optional<AlojamientoServicio> findByAlojamientoId(Long alojamientoId);

    List<AlojamientoServicio> findByServicio(Servicio servicio);

    boolean existByAlojamientoAndServicioId(Long alojamientoId, Long servicioId);
}
