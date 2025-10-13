package com.hospedaya.backend.application.mapper;

import com.hospedaya.backend.application.dto.reserva.ReservaRequestDTO;
import com.hospedaya.backend.application.dto.reserva.ReservaResponseDTO;
import com.hospedaya.backend.domain.entity.Reserva;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

    ReservaMapper INSTANCE = Mappers.getMapper(ReservaMapper.class);

    // Indicamos a MapStruct que ignore los campos de entidad complejos (usuario, alojamiento),
    // ya que serán cargados y asignados en el controlador/servicio usando los IDs.
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "alojamiento", ignore = true)
    Reserva toEntity(ReservaRequestDTO dto);

    // Mapear IDs anidados a la respuesta
    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "alojamiento.id", target = "alojamientoId")
    ReservaResponseDTO toResponse(Reserva entity);
}