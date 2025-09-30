package com.hospedaya.backend.application.mapper;

import com.hospedaya.backend.application.dto.servicio.ServicioRequestDTO;
import com.hospedaya.backend.application.dto.servicio.ServicioResponseDTO;
import com.hospedaya.backend.domain.entity.Servicio;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ServicioMapper {

    ServicioMapper INSTANCE = Mappers.getMapper(ServicioMapper.class);

    Servicio toEntity(ServicioRequestDTO dto);

    ServicioResponseDTO toResponse(Servicio entity);
}
