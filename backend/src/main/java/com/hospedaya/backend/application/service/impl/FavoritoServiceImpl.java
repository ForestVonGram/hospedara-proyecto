package com.hospedaya.backend.application.service.impl;

import com.hospedaya.backend.application.dto.favorito.FavoritoRequestDTO;
import com.hospedaya.backend.application.dto.favorito.FavoritoResponseDTO;
import com.hospedaya.backend.application.mapper.FavoritoMapper;
import com.hospedaya.backend.application.service.base.FavoritoService;
import com.hospedaya.backend.domain.entity.Alojamiento;
import com.hospedaya.backend.domain.entity.Favorito;
import com.hospedaya.backend.domain.entity.Usuario;
import com.hospedaya.backend.exception.ResourceNotFoundException;
import com.hospedaya.backend.infraestructure.repository.AlojamientoRepository;
import com.hospedaya.backend.infraestructure.repository.FavoritoRepository;
import com.hospedaya.backend.infraestructure.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FavoritoServiceImpl implements FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlojamientoRepository alojamientoRepository;
    private final FavoritoMapper favoritoMapper;

    public FavoritoServiceImpl(FavoritoRepository favoritoRepository,
                               UsuarioRepository usuarioRepository,
                               AlojamientoRepository alojamientoRespository,
                               FavoritoMapper favoritoMapper) {
        this.favoritoRepository = favoritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.alojamientoRepository = alojamientoRespository;
        this.favoritoMapper = favoritoMapper;
    }
//
//    public FavoritoResponseDTO agregarFavoritoDesdeDTO(FavoritoRequestDTO requestDTO) {
//        Usuario usuario = usuarioRepository.findById(requestDTO.getUsuarioId())
//                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
//        Alojamiento alojamiento = alojamientoRepository.findById(requestDTO.getAlojamientoId())
//                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado"));
//
//        Favorito favorito = favoritoMapper.toEntity(requestDTO);
//        favorito.setUsuario(usuario);
//        favorito.setAlojamiento(alojamiento);
//
//        Favorito guardado = favoritoRepository.save(favorito);
//        return favoritoMapper.toResponse(guardado);
//    }
//
//    public List<FavoritoResponseDTO> listarFavoritosPorUsuarioDTO(Long idUsuario) {
//        Usuario usuario = usuarioRepository.findById(idUsuario)
//                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + idUsuario));
//
//        return favoritoRepository.findAllByUsuario(usuario).stream()
//                .map(favoritoMapper::toResponse)
//                .collect(Collectors.toList());
//    }

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
 