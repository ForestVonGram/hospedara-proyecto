package com.hospedaya.backend.application.service.impl;

import com.hospedaya.backend.application.service.base.NoticiaService;
import com.hospedaya.backend.domain.entity.Noticia;
import com.hospedaya.backend.exception.ResourceNotFoundException;
import com.hospedaya.backend.infraestructure.repository.NoticiaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class NoticiaServiceImpl implements NoticiaService {

    private final NoticiaRepository noticiaRepository;

    public NoticiaServiceImpl(NoticiaRepository noticiaRepository) {
        this.noticiaRepository = noticiaRepository;
    }

    @Override
    public List<Noticia> listarPublicas() {
        // Por ahora todas son públicas. Se listan de más reciente a más antigua.
        return noticiaRepository.findAllByOrderByFechaCreacionDesc();
    }

    @Override
    public List<Noticia> listarTodas() {
        // Mismo comportamiento que listarPublicas, separado por si en el futuro hay estados internos
        return noticiaRepository.findAllByOrderByFechaCreacionDesc();
    }

    @Override
    public Noticia obtenerPorId(Long id) {
        return noticiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Noticia no encontrada con id: " + id));
    }

    @Override
    public Noticia crearNoticia(Noticia noticia) {
        if (noticia == null) {
            throw new IllegalArgumentException("La noticia no puede ser nula");
        }
        if (noticia.getTitulo() == null || noticia.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título de la noticia es obligatorio");
        }
        if (noticia.getContenido() == null || noticia.getContenido().trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido de la noticia es obligatorio");
        }
        // El campo fechaCreacion ya tiene valor por defecto en la entidad.
        return noticiaRepository.save(noticia);
    }

    @Override
    public Noticia actualizarNoticia(Long id, Noticia cambios) {
        Noticia existente = noticiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Noticia no encontrada con id: " + id));

        if (cambios.getTitulo() == null || cambios.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título de la noticia es obligatorio");
        }
        if (cambios.getContenido() == null || cambios.getContenido().trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido de la noticia es obligatorio");
        }

        existente.setTitulo(cambios.getTitulo());
        existente.setResumen(cambios.getResumen());
        existente.setContenido(cambios.getContenido());
        // fechaCreacion se mantiene tal cual.

        return noticiaRepository.save(existente);
    }

    @Override
    public void eliminarNoticia(Long id) {
        if (!noticiaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Noticia no encontrada con id: " + id);
        }
        noticiaRepository.deleteById(id);
    }
}
