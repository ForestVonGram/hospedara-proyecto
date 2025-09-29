package com.hospedaya.backend.infraestructure.repository;

import com.hospedaya.backend.domain.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {

    List<Servicio> findByNombre(String nombre);
}
