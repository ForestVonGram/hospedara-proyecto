package com.hospedaya.backend.application.mapper;

import com.hospedaya.backend.application.dto.favorito.FavoritoRequestDTO;
import com.hospedaya.backend.application.dto.favorito.FavoritoResponseDTO;
import com.hospedaya.backend.domain.entity.Favorito;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FavoritoMapper {

    FavoritoMapper INSTANCE = Mappers.getMapper(FavoritoMapper.class);

    Favorito toEntity(FavoritoRequestDTO dto);

    FavoritoResponseDTO toResponse(Favorito entity);
}
