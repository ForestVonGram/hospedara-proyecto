package com.hospedaya.backend.application.service.impl;

import com.hospedaya.backend.domain.entity.Pago;
import com.hospedaya.backend.exception.ResourceNotFoundException;
import com.hospedaya.backend.infraestructure.repository.PagoRepository;
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
public class PagoServiceImplTest {

    @Mock
    private PagoRepository pagoRepository;

    @InjectMocks
    private PagoServiceImpl pagoService;

    // registrarPago
    @Test
    void registrarPago_debeGuardarYRetornar() {
        Pago pago = new Pago();
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);

        Pago result = pagoService.registrarPago(pago);

        assertThat(result).isNotNull();
        verify(pagoRepository).save(any(Pago.class));
    }

    @Test
    void registrarPago_conNull_debeLanzarNPE() {
        when(pagoRepository.save(null)).thenThrow(new NullPointerException("null pago"));
        assertThrows(NullPointerException.class, () -> pagoService.registrarPago(null));
        verify(pagoRepository).save(null);
    }

    @Test
    void registrarPago_repoFalla_debePropagarExcepcion() {
        when(pagoRepository.save(any(Pago.class))).thenThrow(new RuntimeException("db"));
        assertThrows(RuntimeException.class, () -> pagoService.registrarPago(new Pago()));
        verify(pagoRepository).save(any(Pago.class));
    }

    // obtenerPagoPorId
    @Test
    void obtenerPagoPorId_existente_debeRetornar() {
        Pago pago = new Pago();
        when(pagoRepository.findById(anyLong())).thenReturn(Optional.of(pago));
        Pago found = pagoService.obtenerPagoPorId(1L);
        assertThat(found).isNotNull();
        verify(pagoRepository).findById(1L);
    }

    @Test
    void obtenerPagoPorId_inexistente_debeLanzarExcepcion() {
        when(pagoRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> pagoService.obtenerPagoPorId(9L));
        verify(pagoRepository).findById(9L);
    }

    @Test
    void obtenerPagoPorId_conNull_debeLanzarResourceNotFound() {
        // Mockito por defecto retorna Optional.empty() para Optional, por lo que el servicio lanzará ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> pagoService.obtenerPagoPorId(null));
    }

    // listarPagos
    @Test
    void listarPagos_debeRetornarLista() {
        when(pagoRepository.findAll()).thenReturn(Arrays.asList(new Pago(), new Pago()));
        List<Pago> lista = pagoService.listarPagos();
        assertThat(lista).hasSize(2);
        verify(pagoRepository).findAll();
    }

    @Test
    void listarPagos_vacio_debeRetornarVacio() {
        when(pagoRepository.findAll()).thenReturn(List.of());
        List<Pago> lista = pagoService.listarPagos();
        assertThat(lista).isEmpty();
        verify(pagoRepository).findAll();
    }

    @Test
    void listarPagos_unElemento_debeRetornarUno() {
        when(pagoRepository.findAll()).thenReturn(List.of(new Pago()));
        List<Pago> lista = pagoService.listarPagos();
        assertThat(lista).hasSize(1);
        verify(pagoRepository).findAll();
    }

    // eliminarPago
    @Test
    void eliminarPago_existente_debeEliminar() {
        when(pagoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(pagoRepository).deleteById(1L);
        pagoService.eliminarPago(1L);
        verify(pagoRepository).existsById(1L);
        verify(pagoRepository).deleteById(1L);
    }

    @Test
    void eliminarPago_inexistente_debeLanzarExcepcion() {
        when(pagoRepository.existsById(9L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> pagoService.eliminarPago(9L));
        verify(pagoRepository).existsById(9L);
        verify(pagoRepository, never()).deleteById(anyLong());
    }

    @Test
    void eliminarPago_deleteFalla_debePropagarExcepcion() {
        when(pagoRepository.existsById(1L)).thenReturn(true);
        doThrow(new RuntimeException("del fail")).when(pagoRepository).deleteById(1L);
        assertThrows(RuntimeException.class, () -> pagoService.eliminarPago(1L));
        verify(pagoRepository).existsById(1L);
        verify(pagoRepository).deleteById(1L);
    }
}
