package com.hospedaya.backend.application.mapper;

import com.hospedaya.backend.application.dto.notificacion.NotificacionRequestDTO;
import com.hospedaya.backend.application.dto.notificacion.NotificacionResponseDTO;
import com.hospedaya.backend.domain.entity.Notificacion;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface NotificacionMapper {

    NotificacionMapper INSTANCE = Mappers.getMapper(NotificacionMapper.class);

    Notificacion toEntity(NotificacionRequestDTO dto);

    NotificacionResponseDTO toResponse(Notificacion entity);
}
