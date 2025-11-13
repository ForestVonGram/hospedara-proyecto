package com.hospedaya.backend.infraestructure.repository;

import com.hospedaya.backend.domain.entity.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    // Búsquedas básicas por claves foráneas
    List<Comentario> findAllByUsuarioId(Long usuarioId);

    List<Comentario> findAllByAlojamientoId(Long alojamientoId);

    List<Comentario> findAllByCalificacion(int calificacion);

    // Todos los comentarios recibidos por un anfitrión (para todos sus alojamientos)
    List<Comentario> findAllByAlojamiento_Anfitrion_Id(Long anfitrionId);
}
