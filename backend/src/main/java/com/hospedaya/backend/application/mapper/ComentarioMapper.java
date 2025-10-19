package com.hospedaya.backend.application.mapper;

import com.hospedaya.backend.application.dto.comentario.ComentarioRequestDTO;
import com.hospedaya.backend.application.dto.comentario.ComentarioResponseDTO;
import com.hospedaya.backend.domain.entity.Alojamiento;
import com.hospedaya.backend.domain.entity.Comentario;
import com.hospedaya.backend.domain.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ComentarioMapper {

    ComentarioMapper INSTANCE = Mappers.getMapper(ComentarioMapper.class);

    @Mappings({
            @Mapping(target = "usuario", source = "usuarioId"),
            @Mapping(target = "alojamiento", source = "alojamientoId"),
            @Mapping(target = "contenido", source = "texto")
    })
    Comentario toEntity(ComentarioRequestDTO dto);

    @Mappings({
            @Mapping(target = "usuarioId", source = "usuario.id"),
            @Mapping(target = "alojamientoId", source = "alojamiento.id"),
            @Mapping(target = "texto", source = "contenido")
    })
    ComentarioResponseDTO toResponse(Comentario entity);

    // Métodos auxiliares para permitir que MapStruct construya referencias por id
    default Usuario mapUsuario(Long id) {
        if (id == null) return null;
        Usuario u = new Usuario();
        u.setId(id);
        return u;
    }

    default Alojamiento mapAlojamiento(Long id) {
        if (id == null) return null;
        Alojamiento a = new Alojamiento();
        a.setId(id);
        return a;
    }

}
