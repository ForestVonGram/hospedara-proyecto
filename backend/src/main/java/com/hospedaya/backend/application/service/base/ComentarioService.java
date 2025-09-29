package com.hospedaya.backend.application.service.base;

import com.hospedaya.backend.domain.entity.Comentario;

import java.util.List;

public interface ComentarioService {

    Comentario agregarComentario(Comentario comentario);
    Comentario obtenerComentarioPorId(Long id);
    List<Comentario> listarComentariosPorAlojamiento(Long alojamientoId);
    void eliminarComentario(Long id);
}
