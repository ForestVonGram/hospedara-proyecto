package com.hospedaya.backend.application.mapper;

import com.hospedaya.backend.application.dto.favorito.FavoritoRequestDTO;
import com.hospedaya.backend.application.dto.favorito.FavoritoResponseDTO;
import com.hospedaya.backend.domain.entity.Alojamiento;
import com.hospedaya.backend.domain.entity.Favorito;
import com.hospedaya.backend.domain.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FavoritoMapper {

    FavoritoMapper INSTANCE = Mappers.getMapper(FavoritoMapper.class);

    @Mappings({
            @Mapping(target = "usuario", source = "usuarioId"),
            @Mapping(target = "alojamiento", source = "alojamientoId")
    })
    Favorito toEntity(FavoritoRequestDTO dto);

    @Mappings({
            @Mapping(target = "usuarioId", source = "usuario.id"),
            @Mapping(target = "alojamientoId", source = "alojamiento.id")
    })
    FavoritoResponseDTO toResponse(Favorito entity);

    // Helpers for MapStruct to build references by id
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
