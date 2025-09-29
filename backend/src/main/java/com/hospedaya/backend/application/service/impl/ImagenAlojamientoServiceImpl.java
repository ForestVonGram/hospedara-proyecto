package com.hospedaya.backend.application.service.impl;

import com.hospedaya.backend.application.service.base.ImagenAlojamientoService;
import com.hospedaya.backend.domain.entity.Alojamiento;
import com.hospedaya.backend.domain.entity.ImagenAlojamiento;
import com.hospedaya.backend.exception.ResourceNotFoundException;
import com.hospedaya.backend.infraestructure.repository.AlojamientoRepository;
import com.hospedaya.backend.infraestructure.repository.ImagenAlojamientoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ImagenAlojamientoServiceImpl implements ImagenAlojamientoService {

    private final ImagenAlojamientoRepository imagenAlojamientoRepository;
    private final AlojamientoRepository alojamientoRepository;

    public ImagenAlojamientoServiceImpl(ImagenAlojamientoRepository imagenAlojamientoRepository,
                                        AlojamientoRepository alojamientoRepository) {
        this.imagenAlojamientoRepository = imagenAlojamientoRepository;
        this.alojamientoRepository = alojamientoRepository;
    }

    @Override
    public ImagenAlojamiento agregarImagen(ImagenAlojamiento imagen) {
        if (imagen == null) {
            throw new IllegalArgumentException("La imagen no puede ser nula");
        }
        if (imagen.getAlojamiento() == null) {
            throw new IllegalArgumentException("La imagen debe estar asociada a un alojamiento");
        }
        if (imagen.getUrl() == null || imagen.getUrl().trim().isEmpty()) {
            throw new IllegalArgumentException("La URL de la imagen es obligatoria");
        }

        // Guardar directamente; la relación debe venir con un alojamiento válido (ID gestionado)
        return imagenAlojamientoRepository.save(imagen);
    }

    @Override
    public List<ImagenAlojamiento> listarImagenesPorAlojamiento(Long alojamientoId) {
        alojamientoRepository.findById(alojamientoId)
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado con ID: " + alojamientoId));
        return imagenAlojamientoRepository.findByAlojamientoId(alojamientoId);
    }

    @Override
    public void eliminarImagen(Long id) {
        if (!imagenAlojamientoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Imagen de alojamiento no encontrada con ID: " + id);
        }
        imagenAlojamientoRepository.deleteById(id);
    }
}
