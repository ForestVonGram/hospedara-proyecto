package com.hospedaya.backend.application.mapper;

import com.hospedaya.backend.application.dto.transaccionpago.TransaccionPagoRequestDTO;
import com.hospedaya.backend.application.dto.transaccionpago.TransaccionPagoResponseDTO;
import com.hospedaya.backend.domain.entity.TransaccionPago;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TransaccionPagoMapper {

    TransaccionPagoMapper INSTANCE = Mappers.getMapper(TransaccionPagoMapper.class);

    TransaccionPago toEntity(TransaccionPagoRequestDTO dto);

    TransaccionPagoResponseDTO toResponse(TransaccionPago entity);
}
