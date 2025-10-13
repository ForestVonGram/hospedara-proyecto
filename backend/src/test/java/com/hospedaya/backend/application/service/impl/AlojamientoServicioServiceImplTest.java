package com.hospedaya.backend.application.service.impl;

import com.hospedaya.backend.domain.entity.AlojamientoServicio;
import com.hospedaya.backend.exception.ResourceNotFoundException;
import com.hospedaya.backend.infraestructure.repository.AlojamientoServicioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlojamientoServicioServiceImplTest {

    @Mock
    private AlojamientoServicioRepository alojamientoServicioRepository;

    @InjectMocks
    private AlojamientoServicioServiceImpl alojamientoServicioService;

    // crearAlojamientoService
    @Test
    void crearAlojamientoService_debeGuardarYRetornar() {
        AlojamientoServicio rel = new AlojamientoServicio();
        when(alojamientoServicioRepository.save(any(AlojamientoServicio.class))).thenReturn(rel);

        AlojamientoServicio creado = alojamientoServicioService.crearAlojamientoService(rel);

        assertThat(creado).isNotNull();
        verify(alojamientoServicioRepository).save(any(AlojamientoServicio.class));
    }

    @Test
    void crearAlojamientoService_conNull_debeLanzarNPE() {
        when(alojamientoServicioRepository.save(null)).thenThrow(new NullPointerException("null relacion"));
        assertThrows(NullPointerException.class, () -> alojamientoServicioService.crearAlojamientoService(null));
        verify(alojamientoServicioRepository).save(null);
    }

    @Test
    void crearAlojamientoService_repoFalla_debePropagarExcepcion() {
        when(alojamientoServicioRepository.save(any(AlojamientoServicio.class))).thenThrow(new RuntimeException("db"));
        assertThrows(RuntimeException.class, () -> alojamientoServicioService.crearAlojamientoService(new AlojamientoServicio()));
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
