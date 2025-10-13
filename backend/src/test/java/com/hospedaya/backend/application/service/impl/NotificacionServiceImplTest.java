package com.hospedaya.backend.application.service.impl;

import com.hospedaya.backend.domain.entity.Notificacion;
import com.hospedaya.backend.domain.entity.Usuario;
import com.hospedaya.backend.exception.ResourceNotFoundException;
import com.hospedaya.backend.infraestructure.repository.NotificacionRepository;
import com.hospedaya.backend.infraestructure.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificacionServiceImplTest {

    @Mock
    private NotificacionRepository notificacionRepository;
    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private NotificacionServiceImpl notificacionService;

    private Usuario usuario;
    private Notificacion notificacion;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Ana");

        notificacion = new Notificacion();
        notificacion.setId(50L);
        notificacion.setUsuario(usuario);
        notificacion.setMensaje("Hola");
    }

    @Test
    void enviarNotificacion_conUsuarioValido_debeGuardar() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacion);

        Notificacion result = notificacionService.enviarNotificacion(notificacion);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(50L);
        verify(usuarioRepository).findById(1L);
        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    void enviarNotificacion_sinUsuario_debeLanzarIllegalArgument() {
        Notificacion n = new Notificacion();
        assertThrows(IllegalArgumentException.class, () -> notificacionService.enviarNotificacion(n));
        verify(usuarioRepository, never()).findById(anyLong());
    }

    @Test
    void enviarNotificacion_usuarioNoExiste_debeLanzarResourceNotFound() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> notificacionService.enviarNotificacion(notificacion));
        verify(usuarioRepository).findById(1L);
        verify(notificacionRepository, never()).save(any());
    }

    @Test
    void listarNotificacionesPorUsuario_existente_debeRetornarLista() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(notificacionRepository.findAllByUsuario(usuario)).thenReturn(Arrays.asList(notificacion));

        List<Notificacion> result = notificacionService.listarNotificacionesPorUsuario(1L);

        assertThat(result).hasSize(1);
        verify(usuarioRepository).findById(1L);
        verify(notificacionRepository).findAllByUsuario(usuario);
    }

    @Test
    void listarNotificacionesPorUsuario_usuarioNoExiste_debeLanzarExcepcion() {
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> notificacionService.listarNotificacionesPorUsuario(2L));
        verify(usuarioRepository).findById(2L);
        verify(notificacionRepository, never()).findAllByUsuario(any());
    }

    @Test
    void eliminarNotificacion_existente_debeEliminar() {
        when(notificacionRepository.existsById(50L)).thenReturn(true);
        doNothing().when(notificacionRepository).deleteById(50L);

        notificacionService.eliminarNotificacion(50L);

        verify(notificacionRepository).existsById(50L);
        verify(notificacionRepository).deleteById(50L);
    }

    @Test
    void eliminarNotificacion_inexistente_debeLanzarExcepcion() {
        when(notificacionRepository.existsById(77L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> notificacionService.eliminarNotificacion(77L));
        verify(notificacionRepository).existsById(77L);
        verify(notificacionRepository, never()).deleteById(anyLong());
    }

    // Extras to reach 3 tests per method
    @Test
    void listarNotificacionesPorUsuario_existenteListaVacia_debeRetornarVacio() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(notificacionRepository.findAllByUsuario(usuario)).thenReturn(List.of());
        List<Notificacion> result = notificacionService.listarNotificacionesPorUsuario(1L);
        assertThat(result).isEmpty();
        verify(usuarioRepository).findById(1L);
        verify(notificacionRepository).findAllByUsuario(usuario);
    }

    @Test
    void eliminarNotificacion_deleteFalla_debePropagarExcepcion() {
        when(notificacionRepository.existsById(50L)).thenReturn(true);
        doThrow(new RuntimeException("del fail")).when(notificacionRepository).deleteById(50L);
        assertThrows(RuntimeException.class, () -> notificacionService.eliminarNotificacion(50L));
        verify(notificacionRepository).existsById(50L);
        verify(notificacionRepository).deleteById(50L);
    }
}
