package com.hospedaya.backend.application.mapper;

import com.hospedaya.backend.application.dto.imagenalojamiento.ImagenAlojamientoRequestDTO;
import com.hospedaya.backend.application.dto.imagenalojamiento.ImagenAlojamientoResponseDTO;
import com.hospedaya.backend.domain.entity.ImagenAlojamiento;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ImagenAlojamientoMapper {

    ImagenAlojamientoMapper INSTANCE = Mappers.getMapper(ImagenAlojamientoMapper.class);

    ImagenAlojamiento toEntity(ImagenAlojamientoRequestDTO dto);

    ImagenAlojamientoResponseDTO toResponse(ImagenAlojamiento entity);
}
