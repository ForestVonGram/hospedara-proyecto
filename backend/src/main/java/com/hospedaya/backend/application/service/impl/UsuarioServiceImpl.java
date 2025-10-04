package com.hospedaya.backend.application.service.impl;

import com.hospedaya.backend.application.dto.usuario.UsuarioRequestDTO;
import com.hospedaya.backend.application.dto.usuario.UsuarioResponseDTO;
import com.hospedaya.backend.application.dto.usuario.UsuarioUpdateDTO;
import com.hospedaya.backend.application.mapper.UsuarioMapper;
import com.hospedaya.backend.application.service.base.UsuarioService;
import com.hospedaya.backend.domain.entity.Usuario;
import com.hospedaya.backend.domain.enums.Rol;
import com.hospedaya.backend.exception.ResourceNotFoundException;
import com.hospedaya.backend.infraestructure.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

//    public UsuarioResponseDTO crearUsuarioDesdeDTO(UsuarioRequestDTO requestDTO) {
//        Usuario usuario = usuarioMapper.toEntity(requestDTO);
//        Usuario guardado = usuarioRepository.save(usuario);
//        return usuarioMapper.toResponse(guardado);
//    }
//
//    public UsuarioResponseDTO actualizarUsuarioDesdeDTO(Long id, UsuarioUpdateDTO updateDTO) {
//        Usuario existente = usuarioRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
//
//        usuarioMapper.updateEntityFromDto(updateDTO, existente);
//        Usuario actualizado = usuarioRepository.save(existente);
//        return usuarioMapper.toResponse(actualizado);
//    }
//
//    public UsuarioResponseDTO obtenerUsuarioPorIdDTO(Long id) {
//        Usuario usuario = usuarioRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
//        return usuarioMapper.toResponse(usuario);
//    }
//
//    public List<UsuarioResponseDTO> listarUsuariosDTO() {
//        return usuarioRepository.findAll().stream()
//                .map(usuarioMapper::toResponse)
//                .collect(Collectors.toList());
//    }

    @Override
    public Usuario crearUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario actualizarUsuario(Long id, Usuario usuario) {
        Usuario existente = usuarioRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        existente.setNombre(usuario.getNombre());
        existente.setEmail(usuario.getEmail());
        existente.setPassword(usuario.getPassword());
        existente.setTelefono(usuario.getTelefono());
        existente.setRol(usuario.getRol());
        return usuarioRepository.save(existente);
    }

    @Override
    public void eliminarUsuario(Long idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + idUsuario);
        }
        usuarioRepository.deleteById(idUsuario);
    }

    @Override
    public Usuario findById(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + idUsuario));
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario asignarRol(Long idUsuario, Rol nuevoRol) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + idUsuario));
        usuario.setRol(nuevoRol);
        return usuarioRepository.save(usuario);
    }
}
