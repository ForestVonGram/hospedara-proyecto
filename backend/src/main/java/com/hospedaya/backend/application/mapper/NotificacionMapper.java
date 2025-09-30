package com.hospedaya.backend.application.mapper;

import com.hospedaya.backend.application.dto.notificacion.NotificacionRequestDTO;
import com.hospedaya.backend.application.dto.notificacion.NotificacionResponseDTO;
import com.hospedaya.backend.domain.entity.Notificacion;
import com.hospedaya.backend.domain.entity.Usuario;

public class NotificacionMapper {

    public static Notificacion toEntity(NotificacionRequestDTO dto) {
        if (dto == null) return null;
        Notificacion n = new Notificacion();
        Usuario u = new Usuario();
        u.setId(dto.getUsuarioId());
        n.setUsuario(u);
        n.setMensaje(dto.getMensaje());
        n.setTipo(dto.getTipo());
        // leida y fechaCreacion se mantienen con sus valores por defecto en la entidad
        return n;
    }

    public static NotificacionResponseDTO toResponseDTO(Notificacion entity) {
        if (entity == null) return null;
        NotificacionResponseDTO dto = new NotificacionResponseDTO();
        dto.setId(entity.getId());
        dto.setUsuarioId(entity.getUsuario() != null ? entity.getUsuario().getId() : null);
        dto.setMensaje(entity.getMensaje());
        dto.setTipo(entity.getTipo());
        dto.setLeida(entity.isLeida());
        dto.setFechaCreacion(entity.getFechaCreacion());
        return dto;
    }
}
