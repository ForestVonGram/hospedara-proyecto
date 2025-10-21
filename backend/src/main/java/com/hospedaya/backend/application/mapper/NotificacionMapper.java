package com.hospedaya.backend.application.mapper;

import com.hospedaya.backend.application.dto.notificacion.NotificacionRequestDTO;
import com.hospedaya.backend.application.dto.notificacion.NotificacionResponseDTO;
import com.hospedaya.backend.domain.entity.Notificacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface NotificacionMapper {

    NotificacionMapper INSTANCE = Mappers.getMapper(NotificacionMapper.class);

    @Mapping(target = "usuario.id", source = "usuarioId")
    Notificacion toEntity(NotificacionRequestDTO dto);

    @Mapping(target = "usuarioId", source = "usuario.id")
    NotificacionResponseDTO toResponse(Notificacion entity);
}
