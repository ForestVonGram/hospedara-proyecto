package com.hospedaya.backend.application.service.impl;

import com.hospedaya.backend.application.service.base.AlojamientoServicioService;
import com.hospedaya.backend.domain.entity.AlojamientoServicio;
import com.hospedaya.backend.exception.ResourceNotFoundException;
import com.hospedaya.backend.infraestructure.repository.AlojamientoServicioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AlojamientoServicioServiceImpl implements AlojamientoServicioService {

    private final AlojamientoServicioRepository alojamientoServicioRepository;

    public AlojamientoServicioServiceImpl(AlojamientoServicioRepository alojamientoServicioRepository) {
        this.alojamientoServicioRepository = alojamientoServicioRepository;
    }

    @Override
    public AlojamientoServicio crearAlojamientoService(AlojamientoServicio alojamientoServicio) {
        // Se asume que la entidad contiene referencias válidas a Alojamiento y Servicio
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
