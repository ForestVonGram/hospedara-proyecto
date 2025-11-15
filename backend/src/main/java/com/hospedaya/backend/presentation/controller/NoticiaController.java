package com.hospedaya.backend.presentation.controller;

import com.hospedaya.backend.application.dto.noticia.NoticiaResponseDTO;
import com.hospedaya.backend.application.mapper.NoticiaMapper;
import com.hospedaya.backend.application.service.base.NoticiaService;
import com.hospedaya.backend.domain.entity.Noticia;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/noticias")
@Tag(name = "Noticias públicas", description = "Listado público de noticias de la plataforma")
public class NoticiaController {

    private final NoticiaService noticiaService;
    private final NoticiaMapper noticiaMapper;

    public NoticiaController(NoticiaService noticiaService, NoticiaMapper noticiaMapper) {
        this.noticiaService = noticiaService;
        this.noticiaMapper = noticiaMapper;
    }

    @Operation(summary = "Listar noticias públicas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Noticias obtenidas correctamente")
    })
    @GetMapping
    public ResponseEntity<List<NoticiaResponseDTO>> listarNoticiasPublicas() {
        List<Noticia> noticias = noticiaService.listarPublicas();
        List<NoticiaResponseDTO> response = noticias.stream()
                .map(noticiaMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
