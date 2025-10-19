package com.hospedaya.backend.application.mapper;

import com.hospedaya.backend.application.dto.comentario.ComentarioRequestDTO;
import com.hospedaya.backend.application.dto.comentario.ComentarioResponseDTO;
import com.hospedaya.backend.domain.entity.Comentario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ComentarioMapper {

    ComentarioMapper INSTANCE = Mappers.getMapper(ComentarioMapper.class);

    @Mappings({
            @Mapping(target = "usuario.id", source = "usuarioId"),
            @Mapping(target = "alojamiento.id", source = "alojamientoId"),
            @Mapping(target = "contenido", source = "texto")
    })
    Comentario toEntity(ComentarioRequestDTO dto);

    @Mappings({
            @Mapping(target = "usuarioId", source = "usuario.id"),
            @Mapping(target = "alojamientoId", source = "alojamiento.id"),
            @Mapping(target = "texto", source = "contenido")
    })
    ComentarioResponseDTO toResponse(Comentario entity);
}
