package com.hospedaya.backend.application.service.base;

import com.hospedaya.backend.domain.entity.Servicio;

import java.util.List;

public interface ServicioService {

    Servicio crearServicio(Servicio servicio);
    Servicio actualizarServicio(Servicio servicio);
    Servicio obtenerServicioPorId(Long id);
    List<Servicio> listarServicios();
    void eliminarServicio(Long id);

}
