package com.hospedaya.backend.application.mapper;

import com.hospedaya.backend.application.dto.imagenalojamiento.ImagenAlojamientoRequestDTO;
import com.hospedaya.backend.application.dto.imagenalojamiento.ImagenAlojamientoResponseDTO;
import com.hospedaya.backend.domain.entity.ImagenAlojamiento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ImagenAlojamientoMapper {

    ImagenAlojamientoMapper INSTANCE = Mappers.getMapper(ImagenAlojamientoMapper.class);

    @Mapping(target = "alojamiento.id", source = "alojamientoId")
    ImagenAlojamiento toEntity(ImagenAlojamientoRequestDTO dto);

    @Mapping(target = "alojamientoId", source = "alojamiento.id")
    ImagenAlojamientoResponseDTO toResponse(ImagenAlojamiento entity);
}
