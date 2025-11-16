package com.hospedaya.backend.application.service.impl;

import com.hospedaya.backend.application.dto.usuario.UsuarioRequestDTO;
import com.hospedaya.backend.application.dto.usuario.UsuarioResponseDTO;
import com.hospedaya.backend.application.dto.usuario.UsuarioUpdateDTO;
import com.hospedaya.backend.application.mapper.UsuarioMapper;
import com.hospedaya.backend.application.service.base.UsuarioService;
import com.hospedaya.backend.domain.entity.Reserva;
import com.hospedaya.backend.domain.entity.Usuario;
import com.hospedaya.backend.domain.enums.Rol;
import com.hospedaya.backend.domain.enums.EstadoReserva;
import com.hospedaya.backend.exception.ResourceNotFoundException;
import com.hospedaya.backend.infraestructure.repository.ReservaRepository;
import com.hospedaya.backend.infraestructure.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final ReservaRepository reservaRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              UsuarioMapper usuarioMapper,
                              PasswordEncoder passwordEncoder,
                              ReservaRepository reservaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
        this.reservaRepository = reservaRepository;
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
        if (usuario == null) {
            throw new com.hospedaya.backend.exception.ValidationException("El usuario no puede ser nulo");
        }
        String nombre = usuario.getNombre() != null ? usuario.getNombre().trim() : null;
        String email = usuario.getEmail() != null ? usuario.getEmail().trim().toLowerCase() : null;
        String password = usuario.getPassword() != null ? usuario.getPassword().trim() : null;

        java.util.List<String> errores = new java.util.ArrayList<>();
        if (nombre == null || nombre.isEmpty()) {
            errores.add("El nombre es obligatorio");
        }
        if (email == null || email.isEmpty()) {
            errores.add("El email es obligatorio");
        } else {
            // Validación simple de email
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
            if (!pattern.matcher(email).matches()) {
                errores.add("El email no tiene un formato válido");
            }
        }
        if (password == null || password.isEmpty()) {
            errores.add("La contraseña es obligatoria");
        } else if (password.length() < 6) {
            errores.add("La contraseña debe tener al menos 6 caracteres");
        }

        if (!errores.isEmpty()) {
            throw new com.hospedaya.backend.exception.ValidationException("Datos de usuario inválidos", errores);
        }

        // Chequear duplicados por email
        if (usuarioRepository.existsByEmail(email)) {
            throw new com.hospedaya.backend.exception.DuplicateResourceException("Ya existe un usuario con el email: " + email);
        }

        // Normalizar valores y defaults
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        if (usuario.getFechaRegistro() == null) {
            usuario.setFechaRegistro(java.time.LocalDate.now());
        }
        if (usuario.getActivo() == null) {
            usuario.setActivo(true);
        }

        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario actualizarUsuario(Long id, Usuario usuario) {
        Usuario existente = usuarioRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        // Actualización parcial: solo campos presentes
        if (usuario.getNombre() != null && !usuario.getNombre().isBlank()) {
            existente.setNombre(usuario.getNombre());
        }
        if (usuario.getEmail() != null && !usuario.getEmail().isBlank()) {
            existente.setEmail(usuario.getEmail());
        }
        if (usuario.getPassword() != null && !usuario.getPassword().isBlank()) {
            existente.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        if (usuario.getTelefono() != null) {
            existente.setTelefono(usuario.getTelefono());
        }
        if (usuario.getFotoPerfilUrl() != null && !usuario.getFotoPerfilUrl().isBlank()) {
            existente.setFotoPerfilUrl(usuario.getFotoPerfilUrl());
        }
        if (usuario.getRol() != null) {
            existente.setRol(usuario.getRol());
        }
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
    public void cancelarReservasYEliminarUsuario(Long idUsuario) {
        // Regla de negocio:
        // - Reservas PENDIENTE / CONFIRMADA / PAGADA con fechaFin futura o en curso
        //   se consideran "activas" y bloquean el borrado de la cuenta.
        // - Reservas CANCELADA o FINALIZADA (o reservas históricas en el pasado)
        //   no deben bloquear; se eliminan junto con la cuenta.
        List<Reserva> reservas = reservaRepository.findByUsuarioId(idUsuario);
        if (!reservas.isEmpty()) {
            LocalDate hoy = LocalDate.now();

            boolean tieneReservasActivas = reservas.stream().anyMatch(reserva -> {
                EstadoReserva estado = reserva.getEstado();
                if (estado == null) {
                    estado = EstadoReserva.PENDIENTE;
                }
                boolean esEstadoActivo = estado == EstadoReserva.PENDIENTE
                        || estado == EstadoReserva.CONFIRMADA
                        || estado == EstadoReserva.PAGADA;
                boolean esFuturaOEnCurso = reserva.getFechaFin() != null && !reserva.getFechaFin().isBefore(hoy);
                return esEstadoActivo && esFuturaOEnCurso;
            });

            if (tieneReservasActivas) {
                // Lanzamos explícitamente DataIntegrityViolationException para que el controlador
                // devuelva el mensaje amigable sobre reservas activas.
                throw new DataIntegrityViolationException("El usuario tiene reservas activas");
            }

            // Solo quedan reservas históricas (canceladas o finalizadas). Las eliminamos primero
            // para que la restricción de integridad no bloquee el borrado del usuario.
            reservaRepository.deleteAll(reservas);
        }

        eliminarUsuario(idUsuario);
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

    @Override
    public Usuario activarUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + idUsuario));
        usuario.setActivo(true);
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario desactivarUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + idUsuario));
        usuario.setActivo(false);
        return usuarioRepository.save(usuario);
    }
}
