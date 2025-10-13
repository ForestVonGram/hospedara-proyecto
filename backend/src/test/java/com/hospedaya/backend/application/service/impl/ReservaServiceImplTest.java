package com.hospedaya.backend.application.service.impl;

import com.hospedaya.backend.domain.entity.Reserva;
import com.hospedaya.backend.domain.enums.EstadoReserva;
import com.hospedaya.backend.exception.ResourceNotFoundException;
import com.hospedaya.backend.infraestructure.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservaServiceImplTest {

    @Mock
    private ReservaRepository reservaRepository;

    @InjectMocks
    private ReservaServiceImpl reservaService;

    private Reserva reserva;

    @BeforeEach
    void setUp() {
        reserva = new Reserva();
        reserva.setId(1L);
        reserva.setFechaInicio(LocalDate.now().plusDays(1));
        reserva.setFechaFin(LocalDate.now().plusDays(3));
        reserva.setEstado(EstadoReserva.PENDIENTE);
    }

    // crearReserva
    @Test
    void crearReserva_debeGuardarYRetornar() {
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);
        Reserva creada = reservaService.crearReserva(reserva);
        assertThat(creada).isNotNull();
        assertThat(creada.getId()).isEqualTo(1L);
        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void crearReserva_conNull_debeLanzarNPE() {
        when(reservaRepository.save(null)).thenThrow(new NullPointerException("null reserva"));
        assertThrows(NullPointerException.class, () -> reservaService.crearReserva(null));
        verify(reservaRepository).save(null);
    }

    @Test
    void crearReserva_repoFalla_debePropagarExcepcion() {
        when(reservaRepository.save(any(Reserva.class))).thenThrow(new RuntimeException("db"));
        assertThrows(RuntimeException.class, () -> reservaService.crearReserva(reserva));
        verify(reservaRepository).save(any(Reserva.class));
    }

    // obtenerReservaPorId
    @Test
    void obtenerReservaPorId_existente_debeRetornar() {
        when(reservaRepository.findById(anyLong())).thenReturn(Optional.of(reserva));
        Reserva found = reservaService.obtenerReservaPorId(1L);
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        verify(reservaRepository).findById(1L);
    }

    @Test
    void obtenerReservaPorId_inexistente_debeLanzarExcepcion() {
        when(reservaRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> reservaService.obtenerReservaPorId(9L));
        verify(reservaRepository).findById(9L);
    }

    @Test
    void obtenerReservaPorId_conNull_debeLanzarResourceNotFound() {
        // Al pasar null, el repositorio devolverá Optional.empty() y el servicio lanzará ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> reservaService.obtenerReservaPorId(null));
    }

    // listarReservas
    @Test
    void listarReservas_debeRetornarLista() {
        when(reservaRepository.findAll()).thenReturn(Arrays.asList(reserva, new Reserva()));
        List<Reserva> lista = reservaService.listarReservas();
        assertThat(lista).hasSize(2);
        verify(reservaRepository).findAll();
    }

    @Test
    void listarReservas_vacio_debeRetornarVacio() {
        when(reservaRepository.findAll()).thenReturn(List.of());
        List<Reserva> lista = reservaService.listarReservas();
        assertThat(lista).isEmpty();
        verify(reservaRepository).findAll();
    }

    @Test
    void listarReservas_unElemento_debeRetornarUno() {
        when(reservaRepository.findAll()).thenReturn(List.of(reserva));
        List<Reserva> lista = reservaService.listarReservas();
        assertThat(lista).hasSize(1);
        verify(reservaRepository).findAll();
    }

    // cancelarReserva
    @Test
    void cancelarReserva_existente_debeCambiarEstadoYGuardar() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        reservaService.cancelarReserva(1L);

        verify(reservaRepository).findById(1L);
        verify(reservaRepository).save(argThat(r -> r.getEstado() == EstadoReserva.CANCELADA));
    }

    @Test
    void cancelarReserva_inexistente_debeLanzarExcepcion() {
        when(reservaRepository.findById(9L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> reservaService.cancelarReserva(9L));
        verify(reservaRepository).findById(9L);
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void cancelarReserva_saveFalla_debePropagarExcepcion() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenThrow(new RuntimeException("save fail"));
        assertThrows(RuntimeException.class, () -> reservaService.cancelarReserva(1L));
        verify(reservaRepository).findById(1L);
        verify(reservaRepository).save(any(Reserva.class));
    }
}
