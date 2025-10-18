package com.hospedaya.backend.application.service.base;

import com.hospedaya.backend.domain.entity.Alojamiento;
import java.util.List;

public interface AlojamientoService {

    Alojamiento crearAlojamiento(Alojamiento alojamiento);
    Alojamiento obtenerAlojamientoPorId(Long id);
    List<Alojamiento> listarAlojamientos();
    List<Alojamiento> listarAlojamientosPorAnfitrion(Long anfitrionId);
    Alojamiento actualizarAlojamiento(Alojamiento alojamiento);
    void eliminarAlojamiento(Long id);
}
