package com.hospedaya.backend.application.service.base;

import com.hospedaya.backend.domain.entity.Notificacion;

import java.util.List;

public interface NotificacionService {

    Notificacion enviarNotificacion(Notificacion notificacion);
    List<Notificacion> listarNotificacionesPorUsuario(Long usuarioId);
    void eliminarNotificacion(Long id);
}
