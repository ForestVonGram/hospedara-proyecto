package com.hospedaya.backend.application.service.impl;

import com.hospedaya.backend.domain.entity.Alojamiento;
import com.hospedaya.backend.domain.entity.Comentario;
import com.hospedaya.backend.domain.entity.Usuario;
import com.hospedaya.backend.exception.ResourceNotFoundException;
import com.hospedaya.backend.infraestructure.repository.ComentarioRepository;
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
public class ComentarioServiceImplTest {

    @Mock
    private ComentarioRepository comentarioRepository;

    @InjectMocks
    private ComentarioServiceImpl comentarioService;

    private Usuario usuario;
    private Alojamiento alojamiento;
    private Comentario comentario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Carlos");

        alojamiento = new Alojamiento();
        alojamiento.setId(10L);
        alojamiento.setNombre("Cabaña");

        comentario = new Comentario();
        comentario.setId(100L);
        comentario.setUsuario(usuario);
        comentario.setAlojamiento(alojamiento);
        comentario.setContenido("Muy buen lugar");
        comentario.setCalificacion(5);
    }

    // agregarComentario
    @Test
    void agregarComentario_debeGuardarYRetornar() {
        when(comentarioRepository.save(any(Comentario.class))).thenReturn(comentario);
        Comentario result = comentarioService.agregarComentario(comentario);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        verify(comentarioRepository).save(any(Comentario.class));
    }

    @Test
    void agregarComentario_conNull_debeLanzarNPE() {
        when(comentarioRepository.save(null)).thenThrow(new NullPointerException("null comentario"));
        assertThrows(NullPointerException.class, () -> comentarioService.agregarComentario(null));
        verify(comentarioRepository).save(null);
    }

    @Test
    void agregarComentario_repoFalla_debePropagarExcepcion() {
        when(comentarioRepository.save(any(Comentario.class))).thenThrow(new RuntimeException("db"));
        assertThrows(RuntimeException.class, () -> comentarioService.agregarComentario(comentario));
        verify(comentarioRepository).save(any(Comentario.class));
    }

    // obtenerComentarioPorId
    @Test
    void obtenerComentarioPorId_existente_debeRetornar() {
        when(comentarioRepository.findById(anyLong())).thenReturn(Optional.of(comentario));
        Comentario found = comentarioService.obtenerComentarioPorId(100L);
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(100L);
        verify(comentarioRepository).findById(100L);
    }

    @Test
    void obtenerComentarioPorId_inexistente_debeLanzarExcepcion() {
        when(comentarioRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> comentarioService.obtenerComentarioPorId(999L));
        verify(comentarioRepository).findById(999L);
    }

    @Test
    void obtenerComentarioPorId_conNull_debeLanzarResourceNotFound() {
        // Mockito devolverá Optional.empty() y el servicio lanzará ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> comentarioService.obtenerComentarioPorId(null));
    }

    // listarComentariosPorAlojamiento
    @Test
    void listarComentariosPorAlojamiento_debeRetornarLista() {
        when(comentarioRepository.findAllByAlojamientoId(10L)).thenReturn(Arrays.asList(comentario, new Comentario()))
        ;
        List<Comentario> lista = comentarioService.listarComentariosPorAlojamiento(10L);
        assertThat(lista).hasSize(2);
        verify(comentarioRepository).findAllByAlojamientoId(10L);
    }

    @Test
    void listarComentariosPorAlojamiento_vacio_debeRetornarVacio() {
        when(comentarioRepository.findAllByAlojamientoId(10L)).thenReturn(List.of());
        List<Comentario> lista = comentarioService.listarComentariosPorAlojamiento(10L);
        assertThat(lista).isEmpty();
        verify(comentarioRepository).findAllByAlojamientoId(10L);
    }

    @Test
    void listarComentariosPorAlojamiento_unElemento_debeRetornarUno() {
        when(comentarioRepository.findAllByAlojamientoId(10L)).thenReturn(List.of(comentario));
        List<Comentario> lista = comentarioService.listarComentariosPorAlojamiento(10L);
        assertThat(lista).hasSize(1);
        verify(comentarioRepository).findAllByAlojamientoId(10L);
    }

    // eliminarComentario
    @Test
    void eliminarComentario_existente_debeEliminar() {
        when(comentarioRepository.findById(100L)).thenReturn(Optional.of(comentario));
        doNothing().when(comentarioRepository).delete(comentario);
        comentarioService.eliminarComentario(100L);
        verify(comentarioRepository).findById(100L);
        verify(comentarioRepository).delete(comentario);
    }

    @Test
    void eliminarComentario_inexistente_debeLanzarExcepcion() {
        when(comentarioRepository.findById(777L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> comentarioService.eliminarComentario(777L));
        verify(comentarioRepository).findById(777L);
        verify(comentarioRepository, never()).delete(any());
    }

    @Test
    void eliminarComentario_deleteFalla_debePropagarExcepcion() {
        when(comentarioRepository.findById(100L)).thenReturn(Optional.of(comentario));
        doThrow(new RuntimeException("del fail")).when(comentarioRepository).delete(comentario);
        assertThrows(RuntimeException.class, () -> comentarioService.eliminarComentario(100L));
        verify(comentarioRepository).findById(100L);
        verify(comentarioRepository).delete(comentario);
    }
}
