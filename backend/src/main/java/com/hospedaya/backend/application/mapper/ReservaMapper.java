package com.hospedaya.backend.application.mapper;

import com.hospedaya.backend.application.dto.reserva.ReservaRequestDTO;
import com.hospedaya.backend.application.dto.reserva.ReservaResponseDTO;
import com.hospedaya.backend.domain.entity.Reserva;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

    ReservaMapper INSTANCE = Mappers.getMapper(ReservaMapper.class);

    Reserva toEntity(ReservaRequestDTO dto);

    ReservaResponseDTO toResponse(Reserva entity);
}
