package com.hospedaya.backend.application.service.base;

import com.hospedaya.backend.domain.entity.Favorito;

import java.util.List;

public interface FavoritoService {

    Favorito agregarFavorito(Favorito favorito);
    List<Favorito> listarFavoritosPorUsuario(Long idUsuario);
    void eliminarFavorito(Long id);
}
