package com.hospedaya.backend.application.mapper;

import com.hospedaya.backend.application.dto.alojamiento.AlojamientoRequestDTO;
import com.hospedaya.backend.application.dto.alojamiento.AlojamientoResponseDTO;
import com.hospedaya.backend.application.dto.alojamiento.AlojamientoUpdateDTO;
import com.hospedaya.backend.domain.entity.Alojamiento;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AlojamientoMapper {

    AlojamientoMapper INSTANCE = Mappers.getMapper(AlojamientoMapper.class);

    Alojamiento toEntity(AlojamientoRequestDTO dto);

    AlojamientoResponseDTO toResponse(Alojamiento entity);

    void updateEntityFromDto(AlojamientoUpdateDTO dto, @org.mapstruct.MappingTarget Alojamiento entity);
}
