package com.hospedaya.backend.application.service.impl;

import com.hospedaya.backend.domain.entity.Alojamiento;
import com.hospedaya.backend.exception.ResourceNotFoundException;
import com.hospedaya.backend.infraestructure.repository.AlojamientoRepository;
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
public class AlojamientoServiceImplTest {

    @Mock
    private AlojamientoRepository alojamientoRepository;

    @InjectMocks
    private AlojamientoServiceImpl alojamientoService;

    private Alojamiento alojamiento;

    @BeforeEach
    void setUp() {
        alojamiento = new Alojamiento();
        alojamiento.setId(1L);
        alojamiento.setNombre("Depto Centro");
        alojamiento.setDescripcion("Lindo depto");
        alojamiento.setDireccion("Calle 123");
        alojamiento.setPrecioPorNoche(50.0);
    }

    // crearAlojamiento
    @Test
    void crearAlojamiento_debeGuardarYRetornar() {
        when(alojamientoRepository.save(any(Alojamiento.class))).thenReturn(alojamiento);
        Alojamiento creado = alojamientoService.crearAlojamiento(alojamiento);
        assertThat(creado).isNotNull();
        assertThat(creado.getId()).isEqualTo(1L);
        verify(alojamientoRepository).save(any(Alojamiento.class));
    }

    @Test
    void crearAlojamiento_conNull_debeLanzarNPE() {
        when(alojamientoRepository.save(null)).thenThrow(new NullPointerException("null alojamiento"));
        assertThrows(NullPointerException.class, () -> alojamientoService.crearAlojamiento(null));
        verify(alojamientoRepository).save(null);
    }

    @Test
    void crearAlojamiento_repoFalla_debePropagarExcepcion() {
        when(alojamientoRepository.save(any(Alojamiento.class))).thenThrow(new RuntimeException("db"));
        assertThrows(RuntimeException.class, () -> alojamientoService.crearAlojamiento(alojamiento));
        verify(alojamientoRepository).save(any(Alojamiento.class));
    }

    // obtenerAlojamientoPorId
    @Test
    void obtenerAlojamientoPorId_existente_debeRetornar() {
        when(alojamientoRepository.findById(anyLong())).thenReturn(Optional.of(alojamiento));
        Alojamiento found = alojamientoService.obtenerAlojamientoPorId(1L);
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        verify(alojamientoRepository).findById(1L);
    }

    @Test
    void obtenerAlojamientoPorId_inexistente_debeLanzarExcepcion() {
        when(alojamientoRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> alojamientoService.obtenerAlojamientoPorId(9L));
        verify(alojamientoRepository).findById(9L);
    }

    @Test
    void obtenerAlojamientoPorId_conNull_debeLanzarResourceNotFound() {
        // Mockito devuelve Optional.empty() para Optional por defecto; esperamos ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> alojamientoService.obtenerAlojamientoPorId(null));
    }

    // listarAlojamientos
    @Test
    void listarAlojamientos_debeRetornarLista() {
        when(alojamientoRepository.findAll()).thenReturn(Arrays.asList(alojamiento, new Alojamiento()));
        List<Alojamiento> lista = alojamientoService.listarAlojamientos();
        assertThat(lista).hasSize(2);
        verify(alojamientoRepository).findAll();
    }

    @Test
    void listarAlojamientos_vacio_debeRetornarVacio() {
        when(alojamientoRepository.findAll()).thenReturn(List.of());
        List<Alojamiento> lista = alojamientoService.listarAlojamientos();
        assertThat(lista).isEmpty();
        verify(alojamientoRepository).findAll();
    }

    @Test
    void listarAlojamientos_unElemento_debeRetornarUno() {
        when(alojamientoRepository.findAll()).thenReturn(List.of(alojamiento));
        List<Alojamiento> lista = alojamientoService.listarAlojamientos();
        assertThat(lista).hasSize(1);
        verify(alojamientoRepository).findAll();
    }

    // eliminarAlojamiento
    @Test
    void eliminarAlojamiento_existente_debeEliminar() {
        when(alojamientoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(alojamientoRepository).deleteById(1L);
        alojamientoService.eliminarAlojamiento(1L);
        verify(alojamientoRepository).existsById(1L);
        verify(alojamientoRepository).deleteById(1L);
    }

    @Test
    void eliminarAlojamiento_inexistente_debeLanzarExcepcion() {
        when(alojamientoRepository.existsById(9L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> alojamientoService.eliminarAlojamiento(9L));
        verify(alojamientoRepository).existsById(9L);
        verify(alojamientoRepository, never()).deleteById(anyLong());
    }

    @Test
    void eliminarAlojamiento_deleteFalla_debePropagarExcepcion() {
        when(alojamientoRepository.existsById(1L)).thenReturn(true);
        doThrow(new RuntimeException("del fail")).when(alojamientoRepository).deleteById(1L);
        assertThrows(RuntimeException.class, () -> alojamientoService.eliminarAlojamiento(1L));
        verify(alojamientoRepository).existsById(1L);
        verify(alojamientoRepository).deleteById(1L);
    }
}
