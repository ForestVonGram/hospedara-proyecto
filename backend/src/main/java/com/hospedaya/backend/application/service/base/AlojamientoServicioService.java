package com.hospedaya.backend.application.service.base;

import com.hospedaya.backend.domain.entity.AlojamientoServicio;

import java.util.List;

public interface AlojamientoServicioService {

    AlojamientoServicio crearAlojamientoService(AlojamientoServicio alojamientoServicio);
    List<AlojamientoServicio> listarAlojamientoServicios();
    void eliminarAlojamientoServicio(Long id);
}
