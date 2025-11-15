package com.hospedaya.backend.application.service.base;

import com.hospedaya.backend.domain.entity.Noticia;

import java.util.List;

public interface NoticiaService {

    /**
     * Listado público de noticias (por ahora todas, ordenadas descendentemente por fecha).
     */
    List<Noticia> listarPublicas();

    /**
     * Listado completo para administración.
     */
    List<Noticia> listarTodas();

    /**
     * Obtener una noticia por su identificador.
     */
    Noticia obtenerPorId(Long id);

    /**
     * Crear una nueva noticia.
     */
    Noticia crearNoticia(Noticia noticia);

    /**
     * Actualizar una noticia existente.
     */
    Noticia actualizarNoticia(Long id, Noticia cambios);

    /**
     * Eliminar una noticia existente.
     */
    void eliminarNoticia(Long id);
}
