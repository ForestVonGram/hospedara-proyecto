package com.hospedaya.backend.application.mapper;

import com.hospedaya.backend.application.dto.pago.PagoRequestDTO;
import com.hospedaya.backend.application.dto.pago.PagoResponseDTO;
import com.hospedaya.backend.domain.entity.Pago;
import com.hospedaya.backend.domain.entity.Reserva;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;
import org.mapstruct.TargetType;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PagoMapper {

    PagoMapper INSTANCE = Mappers.getMapper(PagoMapper.class);

    @Mapping(target = "reserva", source = "reservaId")
    Pago toEntity(PagoRequestDTO dto);

    @Mapping(target = "reservaId", source = "reserva.id")
    PagoResponseDTO toResponse(Pago entity);

    @ObjectFactory
    default Reserva resolveReserva(Long reservaId, @TargetType Class<Reserva> type) {
        Reserva r = new Reserva();
        r.setId(reservaId);
        return r;
    }
}
