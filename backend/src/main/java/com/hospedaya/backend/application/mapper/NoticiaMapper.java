package com.hospedaya.backend.application.mapper;

import com.hospedaya.backend.application.dto.noticia.NoticiaRequestDTO;
import com.hospedaya.backend.application.dto.noticia.NoticiaResponseDTO;
import com.hospedaya.backend.domain.entity.Noticia;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface NoticiaMapper {

    NoticiaMapper INSTANCE = Mappers.getMapper(NoticiaMapper.class);

    Noticia toEntity(NoticiaRequestDTO dto);

    NoticiaResponseDTO toResponse(Noticia entity);
}
