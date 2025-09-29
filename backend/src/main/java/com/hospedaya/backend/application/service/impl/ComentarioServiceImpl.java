package com.hospedaya.backend.application.service.impl;

import com.hospedaya.backend.application.service.base.ComentarioService;
import com.hospedaya.backend.domain.entity.Comentario;
import com.hospedaya.backend.exception.ResourceNotFoundException;
import com.hospedaya.backend.infraestructure.repository.ComentarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ComentarioServiceImpl implements ComentarioService {

    private final ComentarioRepository comentarioRepository;

    public ComentarioServiceImpl(ComentarioRepository comentarioRepository) {
        this.comentarioRepository = comentarioRepository;
    }

    @Override
    public Comentario agregarComentario(Comentario comentario) {
        // Asumimos que Usuario y Alojamiento vienen ya seteados y válidos.
        // Agregar validaciones adicionales si es necesario (e.g., rango de calificación 1-5)
        return comentarioRepository.save(comentario);
    }

    @Override
    public Comentario obtenerComentarioPorId(Long id) {
        return comentarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario no encontrado con id: " + id));
    }

    @Override
    public List<Comentario> listarComentariosPorAlojamiento(Long alojamientoId) {
        return comentarioRepository.findAllByAlojamientoId(alojamientoId);
    }

    @Override
    public void eliminarComentario(Long id) {
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario no encontrado con id: " + id));
        comentarioRepository.delete(comentario);
    }
}
