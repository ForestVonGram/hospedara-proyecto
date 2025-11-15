package com.hospedaya.backend.infraestructure.repository;

import com.hospedaya.backend.domain.entity.Noticia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticiaRepository extends JpaRepository<Noticia, Long> {

    /**
     * Devuelve todas las noticias ordenadas de más reciente a más antigua.
     */
    List<Noticia> findAllByOrderByFechaCreacionDesc();
}
