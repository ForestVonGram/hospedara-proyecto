package com.hospedaya.backend.application.service.impl;

import com.hospedaya.backend.application.service.base.AlojamientoServicioService;
import com.hospedaya.backend.domain.entity.Alojamiento;
import com.hospedaya.backend.domain.entity.AlojamientoServicio;
import com.hospedaya.backend.domain.entity.Servicio;
import com.hospedaya.backend.exception.DuplicateResourceException;
import com.hospedaya.backend.exception.ResourceNotFoundException;
import com.hospedaya.backend.infraestructure.repository.AlojamientoRepository;
import com.hospedaya.backend.infraestructure.repository.AlojamientoServicioRepository;
import com.hospedaya.backend.infraestructure.repository.ServicioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AlojamientoServicioServiceImpl implements AlojamientoServicioService {

    private final AlojamientoServicioRepository alojamientoServicioRepository;
    private final AlojamientoRepository alojamientoRepository;
    private final ServicioRepository servicioRepository;

    public AlojamientoServicioServiceImpl(AlojamientoServicioRepository alojamientoServicioRepository,
                                          AlojamientoRepository alojamientoRepository,
                                          ServicioRepository servicioRepository) {
        this.alojamientoServicioRepository = alojamientoServicioRepository;
        this.alojamientoRepository = alojamientoRepository;
        this.servicioRepository = servicioRepository;
    }

    @Override
    public AlojamientoServicio crearAlojamientoService(AlojamientoServicio alojamientoServicio) {
        if (alojamientoServicio == null || alojamientoServicio.getAlojamiento() == null || alojamientoServicio.getServicio() == null) {
            throw new IllegalArgumentException("Los datos de asignación son inválidos.");
        }
        Long alojamientoId = alojamientoServicio.getAlojamiento().getId();
        Long servicioId = alojamientoServicio.getServicio().getId();
        if (alojamientoId == null || servicioId == null) {
            throw new IllegalArgumentException("Se requieren los IDs de alojamiento y servicio.");
        }

        // Validar existencia
        Alojamiento alojamiento = alojamientoRepository.findById(alojamientoId)
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado con id: " + alojamientoId));
        Servicio servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con id: " + servicioId));

        // Evitar duplicados
        if (alojamientoServicioRepository.existsByAlojamiento_IdAndServicio_Id(alojamientoId, servicioId)) {
            throw new DuplicateResourceException("El servicio ya está asignado a este alojamiento.");
        }

        // Asociar entidades gestionadas
        alojamientoServicio.setAlojamiento(alojamiento);
        alojamientoServicio.setServicio(servicio);

        return alojamientoServicioRepository.save(alojamientoServicio);
    }

    @Override
    public List<AlojamientoServicio> listarAlojamientoServicios() {
        return alojamientoServicioRepository.findAll();
    }

    @Override
    public void eliminarAlojamientoServicio(Long id) {
        if (!alojamientoServicioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Relación Alojamiento-Servicio no encontrada con id: " + id);
        }
        alojamientoServicioRepository.deleteById(id);
    }
}
