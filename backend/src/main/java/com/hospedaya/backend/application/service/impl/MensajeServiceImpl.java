package com.hospedaya.backend.application.service.impl;

import com.hospedaya.backend.application.dto.mensaje.MensajeRequestDTO;
import com.hospedaya.backend.application.dto.mensaje.MensajeResponseDTO;
import com.hospedaya.backend.application.service.MensajeService;
import com.hospedaya.backend.domain.entity.Alojamiento;
import com.hospedaya.backend.domain.entity.Mensaje;
import com.hospedaya.backend.domain.entity.Usuario;
import com.hospedaya.backend.exception.ResourceNotFoundException;
import com.hospedaya.backend.infraestructure.repository.AlojamientoRepository;
import com.hospedaya.backend.infraestructure.repository.MensajeRepository;
import com.hospedaya.backend.infraestructure.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MensajeServiceImpl implements MensajeService {

    private final MensajeRepository mensajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlojamientoRepository alojamientoRepository;

    @Autowired
    public MensajeServiceImpl(MensajeRepository mensajeRepository, 
                             UsuarioRepository usuarioRepository,
                             AlojamientoRepository alojamientoRepository) {
        this.mensajeRepository = mensajeRepository;
        this.usuarioRepository = usuarioRepository;
        this.alojamientoRepository = alojamientoRepository;
    }

    @Override
    @Transactional
    public MensajeResponseDTO enviarMensaje(MensajeRequestDTO mensajeRequestDTO, Long emisorId) {
        Usuario emisor = usuarioRepository.findById(emisorId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario emisor no encontrado"));
        
        Usuario receptor = usuarioRepository.findById(mensajeRequestDTO.getReceptorId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario receptor no encontrado"));
        
        Alojamiento alojamiento = alojamientoRepository.findById(mensajeRequestDTO.getAlojamientoId())
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado"));
        
        Mensaje mensaje = new Mensaje();
        mensaje.setEmisor(emisor);
        mensaje.setReceptor(receptor);
        mensaje.setAlojamiento(alojamiento);
        mensaje.setContenido(mensajeRequestDTO.getContenido());
        
        Mensaje mensajeGuardado = mensajeRepository.save(mensaje);
        
        return convertToDTO(mensajeGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MensajeResponseDTO> obtenerConversacion(Long usuarioId, Long otroUsuarioId, Long alojamientoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        Usuario otroUsuario = usuarioRepository.findById(otroUsuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Otro usuario no encontrado"));
        
        Alojamiento alojamiento = alojamientoRepository.findById(alojamientoId)
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado"));
        
        List<Mensaje> mensajes = mensajeRepository.findConversation(usuario, otroUsuario, alojamiento);
        
        return mensajes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public int marcarComoLeidos(Long receptorId, Long emisorId, Long alojamientoId) {
        Usuario receptor = usuarioRepository.findById(receptorId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario receptor no encontrado"));
        
        Usuario emisor = usuarioRepository.findById(emisorId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario emisor no encontrado"));
        
        Alojamiento alojamiento = alojamientoRepository.findById(alojamientoId)
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado"));
        
        List<Mensaje> mensajesNoLeidos = mensajeRepository.findByEmisorAndReceptorAndAlojamientoOrderByFechaEnvioAsc(
                emisor, receptor, alojamiento);
        
        int count = 0;
        for (Mensaje mensaje : mensajesNoLeidos) {
            if (!mensaje.getLeido()) {
                mensaje.setLeido(true);
                mensajeRepository.save(mensaje);
                count++;
            }
        }
        
        return count;
    }

    @Override
    @Transactional(readOnly = true)
    public long contarMensajesNoLeidos(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        return mensajeRepository.countByReceptorAndLeidoFalse(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> obtenerAlojamientosConConversaciones(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        List<Alojamiento> alojamientos = mensajeRepository.findAlojamientosWithConversations(usuario);
        
        return alojamientos.stream()
                .map(Alojamiento::getId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> obtenerUsuariosEnConversacion(Long usuarioId, Long alojamientoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        Alojamiento alojamiento = alojamientoRepository.findById(alojamientoId)
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado"));
        
        List<Usuario> usuarios = mensajeRepository.findUsuariosInConversation(usuario, alojamiento);
        
        return usuarios.stream()
                .map(Usuario::getId)
                .collect(Collectors.toList());
    }
    
    private MensajeResponseDTO convertToDTO(Mensaje mensaje) {
        MensajeResponseDTO dto = new MensajeResponseDTO();
        dto.setId(mensaje.getId());
        dto.setEmisorId(mensaje.getEmisor().getId());
        dto.setEmisorNombre(mensaje.getEmisor().getNombre());
        dto.setReceptorId(mensaje.getReceptor().getId());
        dto.setReceptorNombre(mensaje.getReceptor().getNombre());
        dto.setAlojamientoId(mensaje.getAlojamiento().getId());
        dto.setAlojamientoNombre(mensaje.getAlojamiento().getNombre());
        dto.setContenido(mensaje.getContenido());
        dto.setFechaEnvio(mensaje.getFechaEnvio());
        dto.setLeido(mensaje.getLeido());
        return dto;
    }
}