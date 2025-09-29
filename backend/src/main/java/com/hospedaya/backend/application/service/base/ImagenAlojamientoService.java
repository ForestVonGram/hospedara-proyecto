package com.hospedaya.backend.application.service.base;

import com.hospedaya.backend.domain.entity.ImagenAlojamiento;

import java.util.List;

public interface ImagenAlojamientoService {

    ImagenAlojamiento agregarImagen(ImagenAlojamiento imagen);
    List<ImagenAlojamiento> listarImagenesPorAlojamiento(Long alojamientoId);
    void eliminarImagen(Long id);
}
