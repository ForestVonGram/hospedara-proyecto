package com.hospedaya.backend.application.mapper;

import com.hospedaya.backend.application.dto.comentario.ComentarioRequestDTO;
import com.hospedaya.backend.application.dto.comentario.ComentarioResponseDTO;
import com.hospedaya.backend.domain.entity.Comentario;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ComentarioMapper {

    ComentarioMapper INSTANCE = Mappers.getMapper(ComentarioMapper.class);

    Comentario toEntity(ComentarioRequestDTO dto);

    ComentarioResponseDTO toResponse(Comentario entity);
}
