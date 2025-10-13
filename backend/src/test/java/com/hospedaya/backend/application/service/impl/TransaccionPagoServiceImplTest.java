package com.hospedaya.backend.application.service.impl;

import com.hospedaya.backend.domain.entity.TransaccionPago;
import com.hospedaya.backend.exception.ResourceNotFoundException;
import com.hospedaya.backend.infraestructure.repository.TransaccionPagoRepository;
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
public class TransaccionPagoServiceImplTest {

    @Mock
    private TransaccionPagoRepository transaccionPagoRepository;

    @InjectMocks
    private TransaccionPagoServiceImpl transaccionPagoService;

    // registrarTransaccion
    @Test
    void registrarTransaccion_debeGuardarYRetornar() {
        TransaccionPago tx = new TransaccionPago();
        when(transaccionPagoRepository.save(any(TransaccionPago.class))).thenReturn(tx);

        TransaccionPago result = transaccionPagoService.registrarTransaccion(tx);

        assertThat(result).isNotNull();
        verify(transaccionPagoRepository).save(any(TransaccionPago.class));
    }

    @Test
    void registrarTransaccion_conNull_debeLanzarNPE() {
        when(transaccionPagoRepository.save(null)).thenThrow(new NullPointerException("null transaccion"));
        assertThrows(NullPointerException.class, () -> transaccionPagoService.registrarTransaccion(null));
        verify(transaccionPagoRepository).save(null);
    }

    @Test
    void registrarTransaccion_repoFalla_debePropagarExcepcion() {
        when(transaccionPagoRepository.save(any(TransaccionPago.class))).thenThrow(new RuntimeException("db"));
        assertThrows(RuntimeException.class, () -> transaccionPagoService.registrarTransaccion(new TransaccionPago()));
        verify(transaccionPagoRepository).save(any(TransaccionPago.class));
    }

    // obtenerTransaccionPorId
    @Test
    void obtenerTransaccionPorId_existente_debeRetornar() {
        TransaccionPago tx = new TransaccionPago();
        when(transaccionPagoRepository.findById(anyLong())).thenReturn(Optional.of(tx));
        TransaccionPago found = transaccionPagoService.obtenerTransaccionPorId(1L);
        assertThat(found).isNotNull();
        verify(transaccionPagoRepository).findById(1L);
    }

    @Test
    void obtenerTransaccionPorId_inexistente_debeLanzarExcepcion() {
        when(transaccionPagoRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> transaccionPagoService.obtenerTransaccionPorId(9L));
        verify(transaccionPagoRepository).findById(9L);
    }

    @Test
    void obtenerTransaccionPorId_conNull_debeLanzarResourceNotFound() {
        // Mockito por defecto retornará Optional.empty() y el servicio lanzará ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> transaccionPagoService.obtenerTransaccionPorId(null));
    }

    // listarTransacciones
    @Test
    void listarTransacciones_debeRetornarLista() {
        when(transaccionPagoRepository.findAll()).thenReturn(Arrays.asList(new TransaccionPago(), new TransaccionPago()));
        List<TransaccionPago> lista = transaccionPagoService.listarTransacciones();
        assertThat(lista).hasSize(2);
        verify(transaccionPagoRepository).findAll();
    }

    @Test
    void listarTransacciones_vacio_debeRetornarVacio() {
        when(transaccionPagoRepository.findAll()).thenReturn(List.of());
        List<TransaccionPago> lista = transaccionPagoService.listarTransacciones();
        assertThat(lista).isEmpty();
        verify(transaccionPagoRepository).findAll();
    }

    @Test
    void listarTransacciones_unElemento_debeRetornarUno() {
        when(transaccionPagoRepository.findAll()).thenReturn(List.of(new TransaccionPago()));
        List<TransaccionPago> lista = transaccionPagoService.listarTransacciones();
        assertThat(lista).hasSize(1);
        verify(transaccionPagoRepository).findAll();
    }
}
