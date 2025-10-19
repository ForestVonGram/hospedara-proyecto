package com.hospedaya.backend.infraestructure.repository;

import com.hospedaya.backend.domain.entity.AlojamientoServicio;
import com.hospedaya.backend.domain.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlojamientoServicioRepository extends JpaRepository<AlojamientoServicio, Long> {

    // Puede haber múltiples servicios por alojamiento, devolver lista
    List<AlojamientoServicio> findByAlojamiento_Id(Long alojamientoId);

    List<AlojamientoServicio> findByServicio(Servicio servicio);

    // Usar la travesía de propiedades con guion bajo para evitar ambigüedad
    boolean existsByAlojamiento_IdAndServicio_Id(Long alojamientoId, Long servicioId);
}
