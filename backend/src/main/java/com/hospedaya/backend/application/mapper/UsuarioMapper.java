package com.hospedaya.backend.application.mapper;

import com.hospedaya.backend.application.dto.usuario.UsuarioRequestDTO;
import com.hospedaya.backend.application.dto.usuario.UsuarioResponseDTO;
import com.hospedaya.backend.application.dto.usuario.UsuarioUpdateDTO;
import com.hospedaya.backend.domain.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);

    Usuario toEntity(UsuarioRequestDTO dto);

    UsuarioResponseDTO toResponse(Usuario entity);

    void updateEntityFromDto(UsuarioUpdateDTO dto, @org.mapstruct.MappingTarget Usuario entity);
}
