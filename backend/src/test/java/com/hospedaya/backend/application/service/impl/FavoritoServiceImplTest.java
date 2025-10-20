package com.hospedaya.backend.application.service.impl;

import com.hospedaya.backend.application.mapper.FavoritoMapper;
import com.hospedaya.backend.domain.entity.Alojamiento;
import com.hospedaya.backend.domain.entity.Favorito;
import com.hospedaya.backend.domain.entity.Usuario;
import com.hospedaya.backend.exception.ResourceNotFoundException;
import com.hospedaya.backend.infraestructure.repository.AlojamientoRepository;
import com.hospedaya.backend.infraestructure.repository.FavoritoRepository;
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
public class FavoritoServiceImplTest {

    @Mock
    private FavoritoRepository favoritoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private AlojamientoRepository alojamientoRepository;
    @Mock
    private FavoritoMapper favoritoMapper;

    @InjectMocks
    private FavoritoServiceImpl favoritoService;

    private Usuario usuario;
    private Alojamiento alojamiento;
    private Favorito favorito;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");

        alojamiento = new Alojamiento();
        alojamiento.setId(10L);
        alojamiento.setNombre("Casa");

        favorito = new Favorito();
        favorito.setId(100L);
        favorito.setUsuario(usuario);
        favorito.setAlojamiento(alojamiento);
    }

    @Test
    void agregarFavorito_debeGuardarYRetornar() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(alojamientoRepository.findById(10L)).thenReturn(Optional.of(alojamiento));
        when(favoritoRepository.findByUsuarioAndAlojamiento(usuario, alojamiento)).thenReturn(Optional.empty());
        when(favoritoRepository.save(any(Favorito.class))).thenReturn(favorito);
        Favorito result = favoritoService.agregarFavorito(favorito);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        verify(usuarioRepository).findById(1L);
        verify(alojamientoRepository).findById(10L);
        verify(favoritoRepository).findByUsuarioAndAlojamiento(usuario, alojamiento);
        verify(favoritoRepository).save(any(Favorito.class));
    }

    @Test
    void listarFavoritosPorUsuario_existente_debeRetornarLista() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(favoritoRepository.findAllByUsuario(usuario)).thenReturn(Arrays.asList(favorito));

        List<Favorito> lista = favoritoService.listarFavoritosPorUsuario(1L);

        assertThat(lista).hasSize(1);
        verify(usuarioRepository).findById(1L);
        verify(favoritoRepository).findAllByUsuario(usuario);
    }

    @Test
    void listarFavoritosPorUsuario_usuarioNoExiste_debeLanzarExcepcion() {
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> favoritoService.listarFavoritosPorUsuario(9L));
        verify(usuarioRepository).findById(9L);
        verify(favoritoRepository, never()).findAllByUsuario(any());
    }

    @Test
    void eliminarFavorito_existente_debeEliminar() {
        when(favoritoRepository.findById(100L)).thenReturn(Optional.of(favorito));
        doNothing().when(favoritoRepository).delete(favorito);

        favoritoService.eliminarFavorito(100L);

        verify(favoritoRepository).findById(100L);
        verify(favoritoRepository).delete(favorito);
    }

    @Test
    void eliminarFavorito_inexistente_debeLanzarExcepcion() {
        when(favoritoRepository.findById(123L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> favoritoService.eliminarFavorito(123L));
        verify(favoritoRepository).findById(123L);
        verify(favoritoRepository, never()).delete(any());
    }

    // Extras to reach 3 tests per method
    @Test
    void agregarFavorito_conNull_debeLanzarNPE() {
        assertThrows(NullPointerException.class, () -> favoritoService.agregarFavorito(null));
        verify(favoritoRepository, never()).save(any());
    }

    @Test
    void agregarFavorito_repoFalla_debePropagarExcepcion() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(alojamientoRepository.findById(10L)).thenReturn(Optional.of(alojamiento));
        when(favoritoRepository.findByUsuarioAndAlojamiento(usuario, alojamiento)).thenReturn(Optional.empty());
        when(favoritoRepository.save(any(Favorito.class))).thenThrow(new RuntimeException("db"));
        assertThrows(RuntimeException.class, () -> favoritoService.agregarFavorito(favorito));
        verify(usuarioRepository).findById(1L);
        verify(alojamientoRepository).findById(10L);
        verify(favoritoRepository).findByUsuarioAndAlojamiento(usuario, alojamiento);
        verify(favoritoRepository).save(any(Favorito.class));
    }

    @Test
    void listarFavoritosPorUsuario_existenteListaVacia_debeRetornarVacio() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(favoritoRepository.findAllByUsuario(usuario)).thenReturn(List.of());
        List<Favorito> result = favoritoService.listarFavoritosPorUsuario(1L);
        assertThat(result).isEmpty();
        verify(usuarioRepository).findById(1L);
        verify(favoritoRepository).findAllByUsuario(usuario);
    }

    @Test
    void eliminarFavorito_deleteFalla_debePropagarExcepcion() {
        when(favoritoRepository.findById(100L)).thenReturn(Optional.of(favorito));
        doThrow(new RuntimeException("del fail")).when(favoritoRepository).delete(favorito);
        assertThrows(RuntimeException.class, () -> favoritoService.eliminarFavorito(100L));
        verify(favoritoRepository).findById(100L);
        verify(favoritoRepository).delete(favorito);
    }
}
