package com.hospedaya.backend.application.service.impl;

import com.hospedaya.backend.application.service.base.AlojamientoService;
import com.hospedaya.backend.domain.entity.Alojamiento;
import com.hospedaya.backend.exception.ResourceNotFoundException;
import com.hospedaya.backend.infraestructure.repository.AlojamientoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AlojamientoServiceImpl implements AlojamientoService {

    private final AlojamientoRepository alojamientoRepository;

    public AlojamientoServiceImpl(AlojamientoRepository alojamientoRepository) {
        this.alojamientoRepository = alojamientoRepository;
    }

    @Override
    public Alojamiento crearAlojamiento(Alojamiento alojamiento) {
        return alojamientoRepository.save(alojamiento);
    }

    @Override
    public Alojamiento obtenerAlojamientoPorId(Long id) {
        return alojamientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado con ID: " + id));
    }

    @Override
    public List<Alojamiento> listarAlojamientos() {
        return alojamientoRepository.findAll();
    }

    @Override
    public void eliminarAlojamiento(Long id) {
        if (!alojamientoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Alojamiento no encontrado con ID: " + id);
        }
        alojamientoRepository.deleteById(id);
    }
}
