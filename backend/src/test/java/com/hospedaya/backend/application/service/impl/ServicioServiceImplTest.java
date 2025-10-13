package com.hospedaya.backend.application.service.impl;

import com.hospedaya.backend.domain.entity.Servicio;
import com.hospedaya.backend.exception.ResourceNotFoundException;
import com.hospedaya.backend.infraestructure.repository.ServicioRepository;
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
public class ServicioServiceImplTest {

    @Mock
    private ServicioRepository servicioRepository;

    @InjectMocks
    private ServicioServiceImpl servicioService;

    private Servicio servicio;

    @BeforeEach
    void setUp() {
        servicio = new Servicio();
        servicio.setId(1L);
        servicio.setNombre("Wifi");
        servicio.setDescripcion("Internet de alta velocidad");
    }

    @Test
    void crearServicio_debeGuardarYRetornar() {
        when(servicioRepository.save(any(Servicio.class))).thenReturn(servicio);

        Servicio creado = servicioService.crearServicio(servicio);

        assertThat(creado).isNotNull();
        assertThat(creado.getId()).isEqualTo(1L);
        verify(servicioRepository, times(1)).save(any(Servicio.class));
    }

    @Test
    void actualizarServicio_debeActualizarCampos() {
        Servicio update = new Servicio();
        update.setId(1L);
        update.setNombre("Piscina");
        update.setDescripcion("Piscina climatizada");

        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicio));
        when(servicioRepository.save(any(Servicio.class))).thenReturn(servicio);

        Servicio result = servicioService.actualizarServicio(update);

        assertThat(result).isNotNull();
        verify(servicioRepository).findById(1L);
        verify(servicioRepository).save(any(Servicio.class));
    }

    @Test
    void actualizarServicio_sinId_debeLanzarExcepcion() {
        Servicio update = new Servicio();
        update.setNombre("X");
        assertThrows(ResourceNotFoundException.class, () -> servicioService.actualizarServicio(update));
        verify(servicioRepository, never()).save(any());
    }

    @Test
    void actualizarServicio_idNoExiste_debeLanzarExcepcion() {
        Servicio update = new Servicio();
        update.setId(999L);
        when(servicioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> servicioService.actualizarServicio(update));
        verify(servicioRepository).findById(999L);
        verify(servicioRepository, never()).save(any());
    }

    @Test
    void obtenerServicioPorId_existente_debeRetornar() {
        when(servicioRepository.findById(anyLong())).thenReturn(Optional.of(servicio));
        Servicio found = servicioService.obtenerServicioPorId(1L);
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        verify(servicioRepository).findById(1L);
    }

    @Test
    void obtenerServicioPorId_inexistente_debeLanzarExcepcion() {
        when(servicioRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> servicioService.obtenerServicioPorId(2L));
        verify(servicioRepository).findById(2L);
    }

    @Test
    void listarServicios_debeRetornarLista() {
        when(servicioRepository.findAll()).thenReturn(Arrays.asList(servicio, new Servicio()));
        List<Servicio> servicios = servicioService.listarServicios();
        assertThat(servicios).hasSize(2);
        verify(servicioRepository).findAll();
    }

    @Test
    void eliminarServicio_existente_debeEliminar() {
        when(servicioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(servicioRepository).deleteById(1L);
        servicioService.eliminarServicio(1L);
        verify(servicioRepository).existsById(1L);
        verify(servicioRepository).deleteById(1L);
    }

    @Test
    void eliminarServicio_inexistente_debeLanzarExcepcion() {
        when(servicioRepository.existsById(9L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> servicioService.eliminarServicio(9L));
        verify(servicioRepository).existsById(9L);
        verify(servicioRepository, never()).deleteById(anyLong());
    }

    // Extras to reach 3 tests per method
    @Test
    void crearServicio_conNull_debeLanzarNPE() {
        when(servicioRepository.save(null)).thenThrow(new NullPointerException("null servicio"));
        assertThrows(NullPointerException.class, () -> servicioService.crearServicio(null));
        verify(servicioRepository).save(null);
    }

    @Test
    void crearServicio_repoFalla_debePropagarExcepcion() {
        when(servicioRepository.save(any(Servicio.class))).thenThrow(new RuntimeException("db"));
        assertThrows(RuntimeException.class, () -> servicioService.crearServicio(servicio));
        verify(servicioRepository).save(any(Servicio.class));
    }

    @Test
    void obtenerServicioPorId_conNull_debeLanzarNPE() {
        // Mockito devuelve Optional.empty() por defecto, por lo que se espera ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> servicioService.obtenerServicioPorId(null));
    }

    @Test
    void listarServicios_vacio_debeRetornarVacio() {
        when(servicioRepository.findAll()).thenReturn(List.of());
        List<Servicio> servicios = servicioService.listarServicios();
        assertThat(servicios).isEmpty();
        verify(servicioRepository).findAll();
    }

    @Test
    void listarServicios_unElemento_debeRetornarListaConUno() {
        when(servicioRepository.findAll()).thenReturn(List.of(servicio));
        List<Servicio> servicios = servicioService.listarServicios();
        assertThat(servicios).hasSize(1);
        verify(servicioRepository).findAll();
    }

    @Test
    void eliminarServicio_deleteByIdFalla_debePropagarExcepcion() {
        when(servicioRepository.existsById(1L)).thenReturn(true);
        doThrow(new RuntimeException("del fail")).when(servicioRepository).deleteById(1L);
        assertThrows(RuntimeException.class, () -> servicioService.eliminarServicio(1L));
        verify(servicioRepository).existsById(1L);
        verify(servicioRepository).deleteById(1L);
    }
}
