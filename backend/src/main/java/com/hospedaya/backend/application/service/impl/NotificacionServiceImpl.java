package com.hospedaya.backend.application.service.impl;

import com.hospedaya.backend.application.service.base.NotificacionService;
import com.hospedaya.backend.domain.entity.Notificacion;
import com.hospedaya.backend.domain.entity.Usuario;
import com.hospedaya.backend.exception.ResourceNotFoundException;
import com.hospedaya.backend.infraestructure.repository.NotificacionRepository;
import com.hospedaya.backend.infraestructure.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;

    public NotificacionServiceImpl(NotificacionRepository notificacionRepository,
                                   UsuarioRepository usuarioRepository) {
        this.notificacionRepository = notificacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Notificacion enviarNotificacion(Notificacion notificacion) {
        if (notificacion.getUsuario() == null || notificacion.getUsuario().getId() == null) {
            throw new IllegalArgumentException("La notificación debe incluir un usuario válido");
        }
        // Validar que el usuario exista y asociarlo a la entidad administrada
        Usuario usuario = usuarioRepository.findById(notificacion.getUsuario().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado con ID: " + notificacion.getUsuario().getId()));
        notificacion.setUsuario(usuario);
        return notificacionRepository.save(notificacion);
    }

    @Override
    public List<Notificacion> listarNotificacionesPorUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));
        return notificacionRepository.findAllByUsuario(usuario);
    }

    @Override
    public void eliminarNotificacion(Long id) {
        if (!notificacionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Notificación no encontrada con ID: " + id);
        }
        notificacionRepository.deleteById(id);
    }
}
