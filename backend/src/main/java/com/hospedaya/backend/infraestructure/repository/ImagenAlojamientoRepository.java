package com.hospedaya.backend.infraestructure.repository;

import com.hospedaya.backend.domain.entity.ImagenAlojamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagenAlojamientoRepository extends JpaRepository<ImagenAlojamiento, Long> {

    List<ImagenAlojamiento> findByAlojamientoId(Long alojamientoId);

    boolean existsByUrlAndAlojamientoId(String url, Long alojamientoId);

    void deleteByAlojamientoId(Long alojamientoId);

    long countByAlojamientoId(Long alojamientoId);
}
