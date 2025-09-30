package com.hospedaya.backend.application.mapper;

import com.hospedaya.backend.application.dto.pago.PagoRequestDTO;
import com.hospedaya.backend.application.dto.pago.PagoResponseDTO;
import com.hospedaya.backend.domain.entity.Pago;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PagoMapper {

    PagoMapper INSTANCE = Mappers.getMapper(PagoMapper.class);

    Pago toEntity(PagoRequestDTO dto);

    PagoResponseDTO toResponse(Pago entity);
}
