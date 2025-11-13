package com.hospedaya.backend.application.service.base;

import com.hospedaya.backend.domain.entity.Alojamiento;

import java.util.List;

public interface RecomendacionService {
    List<Alojamiento> recomendarPorUsuario(Long usuarioId, int limit);
}
