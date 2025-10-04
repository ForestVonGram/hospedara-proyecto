package com.hospedaya.backend.presentation.controller;

import com.hospedaya.backend.application.dto.comentario.ComentarioRequestDTO;
import com.hospedaya.backend.application.dto.comentario.ComentarioResponseDTO;
import com.hospedaya.backend.application.mapper.ComentarioMapper;
import com.hospedaya.backend.application.service.base.ComentarioService;
import com.hospedaya.backend.domain.entity.Comentario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comentarios")
@Tag(name = "Comentarios", description = "Gestión de comentarios")
public class ComentarioController {

    private final ComentarioService comentarioService;
    private final ComentarioMapper comentarioMapper;

    public ComentarioController(ComentarioService comentarioService, ComentarioMapper comentarioMapper) {
        this.comentarioService = comentarioService;
        this.comentarioMapper = comentarioMapper;
    }

    @Operation(summary = "Listar comentarios por alojamiento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comentarios obtenidos"),
            @ApiResponse(responseCode = "400", description = "Petición inválida"),
            @ApiResponse(responseCode = "404", description = "No se encontraron comentarios")
    })
    @GetMapping("/alojamiento/{alojamientoId}")
    public ResponseEntity<List<ComentarioResponseDTO>> listarComentariosPorAlojamiento(@PathVariable Long alojamientoId) {
        List<Comentario> comentarios = comentarioService.listarComentariosPorAlojamiento(alojamientoId);
        List<ComentarioResponseDTO> response = comentarios.stream()
                .map(comentarioMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener comentario por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comentario obtenido"),
            @ApiResponse(responseCode = "404", description = "Comentario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ComentarioResponseDTO> obtenerComentarioPorId(@PathVariable Long id) {
        Comentario comentario = comentarioService.obtenerComentarioPorId(id);
        ComentarioResponseDTO response = comentarioMapper.toResponse(comentario);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Crear comentario")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del comentario",
            required = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{ \"usuarioId\": 1, \"alojamientoId\": 3, \"texto\": \"Muy buen lugar\", \"calificacion\": 5 }"
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Comentario creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario o alojamiento no encontrado")
    })
    @PostMapping
    public ResponseEntity<ComentarioResponseDTO> crearComentario(@RequestBody ComentarioRequestDTO requestDTO) {
        Comentario comentario = comentarioMapper.toEntity(requestDTO);
        Comentario comentarioCreado = comentarioService.agregarComentario(comentario);
        ComentarioResponseDTO response = comentarioMapper.toResponse(comentarioCreado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Eliminar comentario")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Comentario eliminado"),
            @ApiResponse(responseCode = "404", description = "Comentario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarComentario(@PathVariable Long id) {
        comentarioService.eliminarComentario(id);
        return ResponseEntity.noContent().build();
    }
}
