package com.hospedaya.backend.application.service.impl;

import com.hospedaya.backend.domain.entity.Alojamiento;
import com.hospedaya.backend.domain.entity.ImagenAlojamiento;
import com.hospedaya.backend.exception.ResourceNotFoundException;
import com.hospedaya.backend.infraestructure.repository.AlojamientoRepository;
import com.hospedaya.backend.infraestructure.repository.ImagenAlojamientoRepository;
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
public class ImagenAlojamientoServiceImplTest {

    @Mock
    private ImagenAlojamientoRepository imagenAlojamientoRepository;
    @Mock
    private AlojamientoRepository alojamientoRepository;

    @InjectMocks
    private ImagenAlojamientoServiceImpl imagenService;

    private Alojamiento alojamiento;
    private ImagenAlojamiento imagen;

    @BeforeEach
    void setUp() {
        alojamiento = new Alojamiento();
        alojamiento.setId(1L);

        imagen = new ImagenAlojamiento();
        imagen.setId(10L);
        imagen.setAlojamiento(alojamiento);
        imagen.setUrl("https://cdn.example.com/img1.jpg");
        imagen.setPrincipal(true);
    }

    // agregarImagen
    @Test
    void agregarImagen_debeGuardarYRetornar() {
        when(imagenAlojamientoRepository.save(any(ImagenAlojamiento.class))).thenReturn(imagen);
        ImagenAlojamiento result = imagenService.agregarImagen(imagen);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        verify(imagenAlojamientoRepository).save(any(ImagenAlojamiento.class));
    }

    @Test
    void agregarImagen_conEntidadNula_debeLanzarIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> imagenService.agregarImagen(null));
        verify(imagenAlojamientoRepository, never()).save(any());
    }

    @Test
    void agregarImagen_conAlojamientoNulo_oUrlInvalida_debeLanzarIllegalArgument() {
        ImagenAlojamiento sinAlojamiento = new ImagenAlojamiento();
        sinAlojamiento.setUrl("https://x");
        assertThrows(IllegalArgumentException.class, () -> imagenService.agregarImagen(sinAlojamiento));

        ImagenAlojamiento sinUrl = new ImagenAlojamiento();
        sinUrl.setAlojamiento(alojamiento);
        sinUrl.setUrl("  ");
        assertThrows(IllegalArgumentException.class, () -> imagenService.agregarImagen(sinUrl));

        verify(imagenAlojamientoRepository, never()).save(any());
    }

    // listarImagenesPorAlojamiento
    @Test
    void listarImagenesPorAlojamiento_existente_debeRetornarLista() {
        when(alojamientoRepository.findById(1L)).thenReturn(Optional.of(alojamiento));
        when(imagenAlojamientoRepository.findByAlojamientoId(1L)).thenReturn(Arrays.asList(imagen, new ImagenAlojamiento()));

        List<ImagenAlojamiento> result = imagenService.listarImagenesPorAlojamiento(1L);

        assertThat(result).hasSize(2);
        verify(alojamientoRepository).findById(1L);
        verify(imagenAlojamientoRepository).findByAlojamientoId(1L);
    }

    @Test
    void listarImagenesPorAlojamiento_alojamientoNoExiste_debeLanzarExcepcion() {
        when(alojamientoRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> imagenService.listarImagenesPorAlojamiento(9L));
        verify(alojamientoRepository).findById(9L);
        verify(imagenAlojamientoRepository, never()).findByAlojamientoId(anyLong());
    }

    @Test
    void listarImagenesPorAlojamiento_existenteListaVacia_debeRetornarVacio() {
        when(alojamientoRepository.findById(1L)).thenReturn(Optional.of(alojamiento));
        when(imagenAlojamientoRepository.findByAlojamientoId(1L)).thenReturn(List.of());

        List<ImagenAlojamiento> result = imagenService.listarImagenesPorAlojamiento(1L);

        assertThat(result).isEmpty();
        verify(alojamientoRepository).findById(1L);
        verify(imagenAlojamientoRepository).findByAlojamientoId(1L);
    }

    // eliminarImagen
    @Test
    void eliminarImagen_existente_debeEliminar() {
        when(imagenAlojamientoRepository.existsById(10L)).thenReturn(true);
        doNothing().when(imagenAlojamientoRepository).deleteById(10L);

        imagenService.eliminarImagen(10L);

        verify(imagenAlojamientoRepository).existsById(10L);
        verify(imagenAlojamientoRepository).deleteById(10L);
    }

    @Test
    void eliminarImagen_inexistente_debeLanzarExcepcion() {
        when(imagenAlojamientoRepository.existsById(77L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> imagenService.eliminarImagen(77L));
        verify(imagenAlojamientoRepository).existsById(77L);
        verify(imagenAlojamientoRepository, never()).deleteById(anyLong());
    }

    @Test
    void eliminarImagen_deleteFalla_debePropagarExcepcion() {
        when(imagenAlojamientoRepository.existsById(10L)).thenReturn(true);
        doThrow(new RuntimeException("del fail")).when(imagenAlojamientoRepository).deleteById(10L);
        assertThrows(RuntimeException.class, () -> imagenService.eliminarImagen(10L));
        verify(imagenAlojamientoRepository).existsById(10L);
        verify(imagenAlojamientoRepository).deleteById(10L);
    }
}
