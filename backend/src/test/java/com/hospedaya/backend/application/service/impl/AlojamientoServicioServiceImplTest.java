package com.hospedaya.backend.application.service.impl;

import com.hospedaya.backend.domain.entity.Alojamiento;
import com.hospedaya.backend.domain.entity.AlojamientoServicio;
import com.hospedaya.backend.domain.entity.Servicio;
import com.hospedaya.backend.exception.ResourceNotFoundException;
import com.hospedaya.backend.infraestructure.repository.AlojamientoRepository;
import com.hospedaya.backend.infraestructure.repository.AlojamientoServicioRepository;
import com.hospedaya.backend.infraestructure.repository.ServicioRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlojamientoServicioServiceImplTest {

    @Mock
    private AlojamientoServicioRepository alojamientoServicioRepository;
    @Mock
    private AlojamientoRepository alojamientoRepository;
    @Mock
    private ServicioRepository servicioRepository;

    @InjectMocks
    private AlojamientoServicioServiceImpl alojamientoServicioService;

    // crearAlojamientoService
    @Test
    void crearAlojamientoService_debeGuardarYRetornar() {
        // Arrange
        Alojamiento alojamiento = new Alojamiento(); alojamiento.setId(1L);
        Servicio servicio = new Servicio(); servicio.setId(2L);
        AlojamientoServicio rel = new AlojamientoServicio();
        rel.setAlojamiento(alojamiento);
        rel.setServicio(servicio);

        when(alojamientoRepository.findById(1L)).thenReturn(Optional.of(alojamiento));
        when(servicioRepository.findById(2L)).thenReturn(Optional.of(servicio));
        when(alojamientoServicioRepository.existsByAlojamientoIdAndServicioId(1L, 2L)).thenReturn(false);
        when(alojamientoServicioRepository.save(any(AlojamientoServicio.class))).thenReturn(rel);

        // Act
        AlojamientoServicio creado = alojamientoServicioService.crearAlojamientoService(rel);

        // Assert
        assertThat(creado).isNotNull();
        verify(alojamientoRepository).findById(1L);
        verify(servicioRepository).findById(2L);
        verify(alojamientoServicioRepository).existsByAlojamientoIdAndServicioId(1L, 2L);
        verify(alojamientoServicioRepository).save(any(AlojamientoServicio.class));
    }

    @Test
    void crearAlojamientoService_conNull_debeLanzarIAE() {
        assertThrows(IllegalArgumentException.class, () -> alojamientoServicioService.crearAlojamientoService(null));
        verify(alojamientoServicioRepository, never()).save(any());
    }

    @Test
    void crearAlojamientoService_repoFalla_debePropagarExcepcion() {
        // Arrange
        Alojamiento alojamiento = new Alojamiento(); alojamiento.setId(1L);
        Servicio servicio = new Servicio(); servicio.setId(2L);
        AlojamientoServicio rel = new AlojamientoServicio();
        rel.setAlojamiento(alojamiento);
        rel.setServicio(servicio);

        when(alojamientoRepository.findById(1L)).thenReturn(Optional.of(alojamiento));
        when(servicioRepository.findById(2L)).thenReturn(Optional.of(servicio));
        when(alojamientoServicioRepository.existsByAlojamientoIdAndServicioId(1L, 2L)).thenReturn(false);
        when(alojamientoServicioRepository.save(any(AlojamientoServicio.class))).thenThrow(new RuntimeException("db"));

        // Assert
        assertThrows(RuntimeException.class, () -> alojamientoServicioService.crearAlojamientoService(rel));
        verify(alojamientoRepository).findById(1L);
        verify(servicioRepository).findById(2L);
        verify(alojamientoServicioRepository).existsByAlojamientoIdAndServicioId(1L, 2L);
        verify(alojamientoServicioRepository).save(any(AlojamientoServicio.class));
    }

    // listarAlojamientoServicios
    @Test
    void listarAlojamientoServicios_debeRetornarLista() {
        when(alojamientoServicioRepository.findAll()).thenReturn(Arrays.asList(new AlojamientoServicio(), new AlojamientoServicio()));
        List<AlojamientoServicio> lista = alojamientoServicioService.listarAlojamientoServicios();
        assertThat(lista).hasSize(2);
        verify(alojamientoServicioRepository).findAll();
    }

    @Test
    void listarAlojamientoServicios_vacio_debeRetornarVacio() {
        when(alojamientoServicioRepository.findAll()).thenReturn(List.of());
        List<AlojamientoServicio> lista = alojamientoServicioService.listarAlojamientoServicios();
        assertThat(lista).isEmpty();
        verify(alojamientoServicioRepository).findAll();
    }

    @Test
    void listarAlojamientoServicios_unElemento_debeRetornarUno() {
        when(alojamientoServicioRepository.findAll()).thenReturn(List.of(new AlojamientoServicio()));
        List<AlojamientoServicio> lista = alojamientoServicioService.listarAlojamientoServicios();
        assertThat(lista).hasSize(1);
        verify(alojamientoServicioRepository).findAll();
    }

    // eliminarAlojamientoServicio
    @Test
    void eliminarAlojamientoServicio_existente_debeEliminar() {
        when(alojamientoServicioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(alojamientoServicioRepository).deleteById(1L);

        alojamientoServicioService.eliminarAlojamientoServicio(1L);

        verify(alojamientoServicioRepository).existsById(1L);
        verify(alojamientoServicioRepository).deleteById(1L);
    }

    @Test
    void eliminarAlojamientoServicio_inexistente_debeLanzarExcepcion() {
        when(alojamientoServicioRepository.existsById(9L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> alojamientoServicioService.eliminarAlojamientoServicio(9L));
        verify(alojamientoServicioRepository).existsById(9L);
        verify(alojamientoServicioRepository, never()).deleteById(anyLong());
    }

    @Test
    void eliminarAlojamientoServicio_deleteFalla_debePropagarExcepcion() {
        when(alojamientoServicioRepository.existsById(1L)).thenReturn(true);
        doThrow(new RuntimeException("del fail")).when(alojamientoServicioRepository).deleteById(1L);
        assertThrows(RuntimeException.class, () -> alojamientoServicioService.eliminarAlojamientoServicio(1L));
        verify(alojamientoServicioRepository).existsById(1L);
        verify(alojamientoServicioRepository).deleteById(1L);
    }
}
