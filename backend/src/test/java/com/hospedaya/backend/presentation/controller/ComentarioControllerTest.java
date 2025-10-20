package com.hospedaya.backend.presentation.controller;

import com.hospedaya.backend.application.mapper.ComentarioMapper;
import com.hospedaya.backend.application.service.base.ComentarioService;
import com.hospedaya.backend.domain.entity.Comentario;
import com.hospedaya.backend.exception.GlobalExceptionHandler;
import com.hospedaya.backend.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ComentarioController.class)
@Import({GlobalExceptionHandler.class, ComentarioMapper.class})
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class ComentarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ComentarioService comentarioService;

    @MockBean
    private ComentarioMapper comentarioMapper; // mocked to avoid mapping concerns

    @Test
    @DisplayName("Debe responder 404 cuando no hay comentarios para el alojamiento")
    void listarComentariosPorAlojamiento_sinResultados_responde404() throws Exception {
        Mockito.when(comentarioService.listarComentariosPorAlojamiento(999L))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/comentarios/alojamiento/{alojamientoId}", 999L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe responder 404 cuando el alojamiento no existe")
    void listarComentariosPorAlojamiento_alojamientoInexistente_responde404() throws Exception {
        Mockito.when(comentarioService.listarComentariosPorAlojamiento(anyLong()))
                .thenThrow(new ResourceNotFoundException("Alojamiento no encontrado"));

        mockMvc.perform(get("/comentarios/alojamiento/{alojamientoId}", 12345L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
