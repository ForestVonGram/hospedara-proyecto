package com.hospedaya.backend.application.mapper;

import com.hospedaya.backend.application.dto.alojamientoservicio.AlojamientoServicioRequestDTO;
import com.hospedaya.backend.application.dto.alojamientoservicio.AlojamientoServicioResponseDTO;
import com.hospedaya.backend.domain.entity.AlojamientoServicio;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AlojamientoServicioMapper {

    AlojamientoServicioMapper INSTANCE = Mappers.getMapper(AlojamientoServicioMapper.class);

    AlojamientoServicio toEntity(AlojamientoServicioRequestDTO dto);

    AlojamientoServicioResponseDTO toResponse(AlojamientoServicio entity);
}
