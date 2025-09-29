package com.hospedaya.backend.infraestructure.repository;

import com.hospedaya.backend.domain.entity.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    Optional<Comentario> findByComentarioId(Long comentarioId);

    Optional<Comentario> findByUsuarioId(Long usuarioId);

    Optional<Comentario> findByAlojamientoId(Long alojamientoId);

    List<Comentario> findByCalificacion(int calificacion);

    List<Comentario> findByFechaCreacion(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
