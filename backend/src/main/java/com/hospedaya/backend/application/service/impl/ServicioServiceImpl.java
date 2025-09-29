package com.hospedaya.backend.application.service.impl;

import com.hospedaya.backend.application.service.base.ServicioService;
import com.hospedaya.backend.domain.entity.Servicio;
import com.hospedaya.backend.exception.ResourceNotFoundException;
import com.hospedaya.backend.infraestructure.repository.ServicioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ServicioServiceImpl implements ServicioService {

    private final ServicioRepository servicioRepository;

    public ServicioServiceImpl(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    @Override
    public Servicio crearServicio(Servicio servicio) {
        return servicioRepository.save(servicio);
    }

    @Override
    public Servicio actualizarServicio(Servicio servicio) {
        if (servicio.getId() == null) {
            throw new ResourceNotFoundException("El ID del servicio es requerido para actualizar");
        }
        Servicio existente = servicioRepository.findById(servicio.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con id: " + servicio.getId()));

        existente.setNombre(servicio.getNombre());
        existente.setDescripcion(servicio.getDescripcion());

        return servicioRepository.save(existente);
    }

    @Override
    public Servicio obtenerServicioPorId(Long id) {
        return servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con id: " + id));
    }

    @Override
    public List<Servicio> listarServicios() {
        return servicioRepository.findAll();
    }

    @Override
    public void eliminarServicio(Long id) {
        if (!servicioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Servicio no encontrado con id: " + id);
        }
        servicioRepository.deleteById(id);
    }
}
