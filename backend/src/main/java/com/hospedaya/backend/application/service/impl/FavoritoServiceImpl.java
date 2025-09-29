package com.hospedaya.backend.application.service.impl;

import com.hospedaya.backend.application.service.base.FavoritoService;
import com.hospedaya.backend.domain.entity.Favorito;
import com.hospedaya.backend.domain.entity.Usuario;
import com.hospedaya.backend.exception.ResourceNotFoundException;
import com.hospedaya.backend.infraestructure.repository.FavoritoRepository;
import com.hospedaya.backend.infraestructure.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class FavoritoServiceImpl implements FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final UsuarioRepository usuarioRepository;

    public FavoritoServiceImpl(FavoritoRepository favoritoRepository, UsuarioRepository usuarioRepository) {
        this.favoritoRepository = favoritoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Favorito agregarFavorito(Favorito favorito) {
        // Asumimos que el objeto Favorito ya contiene Usuario y Alojamiento válidos.
        // Si se requiere validación adicional, puede agregarse aquí.
        return favoritoRepository.save(favorito);
    }

    @Override
    public List<Favorito> listarFavoritosPorUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + idUsuario));
        return favoritoRepository.findAllByUsuario(usuario);
    }

    @Override
    public void eliminarFavorito(Long id) {
        Favorito favorito = favoritoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Favorito no encontrado con id: " + id));
        favoritoRepository.delete(favorito);
    }
}
 