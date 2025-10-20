package com.hospedaya.backend.application.mapper;

import com.hospedaya.backend.application.dto.alojamientoservicio.AlojamientoServicioRequestDTO;
import com.hospedaya.backend.application.dto.alojamientoservicio.AlojamientoServicioResponseDTO;
import com.hospedaya.backend.domain.entity.Alojamiento;
import com.hospedaya.backend.domain.entity.AlojamientoServicio;
import com.hospedaya.backend.domain.entity.Servicio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AlojamientoServicioMapper {

    AlojamientoServicioMapper INSTANCE = Mappers.getMapper(AlojamientoServicioMapper.class);

    @Mapping(target = "alojamiento", expression = "java(mapAlojamiento(dto.getAlojamientoId()))")
    @Mapping(target = "servicio", expression = "java(mapServicio(dto.getServicioId()))")
    AlojamientoServicio toEntity(AlojamientoServicioRequestDTO dto);

    // La respuesta debe contener los datos de la relación y del servicio asociado
    @Mapping(target = "relacionId", source = "id")
    @Mapping(target = "alojamientoId", source = "alojamiento.id")
    @Mapping(target = "servicioId", source = "servicio.id")
    @Mapping(target = "detalle", source = "detalle")
    @Mapping(target = "id", source = "servicio.id")
    @Mapping(target = "nombre", source = "servicio.nombre")
    @Mapping(target = "descripcion", source = "servicio.descripcion")
    AlojamientoServicioResponseDTO toResponse(AlojamientoServicio entity);

    // Helpers para construir referencias por id
    default Alojamiento mapAlojamiento(Long id) {
        if (id == null) return null;
        Alojamiento a = new Alojamiento();
        a.setId(id);
        return a;
    }

    default Servicio mapServicio(Long id) {
        if (id == null) return null;
        Servicio s = new Servicio();
        s.setId(id);
        return s;
    }
}
