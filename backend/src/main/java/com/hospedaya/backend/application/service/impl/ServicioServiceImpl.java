package com.hospedaya.backend.application.service.impl;

import com.hospedaya.backend.application.service.base.ServicioService;
import com.hospedaya.backend.domain.entity.Servicio;
import com.hospedaya.backend.exception.ResourceNotFoundException;
import com.hospedaya.backend.infraestructure.repository.AlojamientoServicioRepository;
import com.hospedaya.backend.infraestructure.repository.ServicioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ServicioServiceImpl implements ServicioService {

    private final ServicioRepository servicioRepository;
    private final AlojamientoServicioRepository alojamientoServicioRepository;

    public ServicioServiceImpl(ServicioRepository servicioRepository,
                               AlojamientoServicioRepository alojamientoServicioRepository) {
        this.servicioRepository = servicioRepository;
        this.alojamientoServicioRepository = alojamientoServicioRepository;
    }

    @Override
    public Servicio crearServicio(Servicio servicio) {
        // Validaciones para evitar crear servicios vacíos o inválidos
        if (servicio == null) {
            throw new com.hospedaya.backend.exception.ValidationException("El servicio no puede ser nulo");
        }
        String nombre = servicio.getNombre();
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new com.hospedaya.backend.exception.ValidationException("El nombre del servicio es obligatorio");
        }
        nombre = nombre.trim();
        if (nombre.length() > 100) {
            throw new com.hospedaya.backend.exception.ValidationException("El nombre del servicio no puede superar 100 caracteres");
        }
        servicio.setNombre(nombre);

        String descripcion = servicio.getDescripcion();
        if (descripcion != null) {
            servicio.setDescripcion(descripcion.trim());
        }

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
        // Asegurar que el servicio exista y obtenerlo (aunque no lo usemos luego)
        servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con id: " + id));

        // Eliminar relaciones en la tabla intermedia para evitar violaciones de integridad referencial
        alojamientoServicioRepository.deleteByServicio_Id(id);

        // Eliminar el servicio
        servicioRepository.deleteById(id);
    }
}
