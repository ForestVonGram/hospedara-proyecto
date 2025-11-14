package com.hospedaya.backend.application.service.base;

import com.hospedaya.backend.domain.entity.Comentario;

import java.util.List;

public interface ComentarioService {

    Comentario agregarComentario(Comentario comentario);
    Comentario obtenerComentarioPorId(Long id);
    List<Comentario> listarComentariosPorAlojamiento(Long alojamientoId);
    List<Comentario> listarComentariosPorAnfitrion(Long anfitrionId);
    /** Listar todos los comentarios del sistema (uso administrativo) */
    List<Comentario> listarTodos();
    void eliminarComentario(Long id);
}
