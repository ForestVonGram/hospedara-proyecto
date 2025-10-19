package com.hospedaya.backend.application.mapper;

import com.hospedaya.backend.application.dto.alojamiento.AlojamientoRequestDTO;
import com.hospedaya.backend.application.dto.alojamiento.AlojamientoResponseDTO;
import com.hospedaya.backend.application.dto.alojamiento.AlojamientoUpdateDTO;
import com.hospedaya.backend.domain.entity.Alojamiento;
import com.hospedaya.backend.domain.entity.ImagenAlojamiento;
import com.hospedaya.backend.domain.entity.Servicio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
public interface AlojamientoMapper {

    AlojamientoMapper INSTANCE = Mappers.getMapper(AlojamientoMapper.class);

    @Mapping(source = "precioPorNoche", target = "precioPorNoche", qualifiedByName = "bigDecimalToDouble")
    Alojamiento toEntity(AlojamientoRequestDTO dto);

    @Mapping(source = "nombre", target = "titulo")
    @Mapping(source = "anfitrion.id", target = "anfitrionId")
    @Mapping(source = "imagenes", target = "imagenes", qualifiedByName = "imagenesToUrls")
    @Mapping(source = "servicios", target = "servicios", qualifiedByName = "serviciosToNombres")
    AlojamientoResponseDTO toResponse(Alojamiento entity);

    @Mapping(source = "nombre", target = "nombre")
    @Mapping(source = "direccion", target = "direccion")
    @Mapping(source = "precioPorNoche", target = "precioPorNoche", qualifiedByName = "bigDecimalToDouble")
    void updateEntityFromDto(AlojamientoUpdateDTO dto, @org.mapstruct.MappingTarget Alojamiento entity);

    @Named("imagenesToUrls")
    default List<String> imagenesToUrls(List<ImagenAlojamiento> imagenes) {
        if (imagenes == null) {
            return null;
        }
        return imagenes.stream()
                .map(ImagenAlojamiento::getUrl)
                .collect(Collectors.toList());
    }

    @Named("serviciosToNombres")
    default List<String> serviciosToNombres(List<Servicio> servicios) {
        if (servicios == null) {
            return null;
        }
        return servicios.stream()
                .map(Servicio::getNombre)
                .collect(Collectors.toList());
    }

    @Named("bigDecimalToDouble")
    default Double bigDecimalToDouble(BigDecimal value) {
        return value == null ? null : value.doubleValue();
    }
}
