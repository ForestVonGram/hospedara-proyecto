package com.hospedaya.backend.presentation.controller;

import com.hospedaya.backend.application.dto.noticia.NoticiaRequestDTO;
import com.hospedaya.backend.application.dto.noticia.NoticiaResponseDTO;
import com.hospedaya.backend.application.mapper.NoticiaMapper;
import com.hospedaya.backend.application.service.base.NoticiaService;
import com.hospedaya.backend.domain.entity.Noticia;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/noticias")
@Tag(name = "Noticias (admin)", description = "Gestión administrativa de noticias")
public class NoticiaAdminController {

    private final NoticiaService noticiaService;
    private final NoticiaMapper noticiaMapper;

    public NoticiaAdminController(NoticiaService noticiaService, NoticiaMapper noticiaMapper) {
        this.noticiaService = noticiaService;
        this.noticiaMapper = noticiaMapper;
    }

    @Operation(summary = "Listar todas las noticias (admin)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Noticias obtenidas correctamente")
    })
    @GetMapping
    public ResponseEntity<List<NoticiaResponseDTO>> listarTodas() {
        List<Noticia> noticias = noticiaService.listarTodas();
        List<NoticiaResponseDTO> response = noticias.stream()
                .map(noticiaMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener noticia por ID (admin)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Noticia obtenida"),
            @ApiResponse(responseCode = "404", description = "Noticia no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<NoticiaResponseDTO> obtenerPorId(@PathVariable Long id) {
        Noticia noticia = noticiaService.obtenerPorId(id);
        NoticiaResponseDTO response = noticiaMapper.toResponse(noticia);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Crear noticia")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos de la noticia",
            required = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{ \"titulo\": \"Nuevo anuncio\", \"resumen\": \"Resumen breve\", \"contenido\": \"Contenido detallado de la noticia\" }"
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Noticia creada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<NoticiaResponseDTO> crear(@RequestBody NoticiaRequestDTO requestDTO) {
        Noticia noticia = noticiaMapper.toEntity(requestDTO);
        Noticia creada = noticiaService.crearNoticia(noticia);
        NoticiaResponseDTO response = noticiaMapper.toResponse(creada);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Actualizar noticia")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos de la noticia a actualizar",
            required = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{ \"titulo\": \"Nuevo título\", \"resumen\": \"Resumen actualizado\", \"contenido\": \"Contenido actualizado de la noticia\" }"
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Noticia actualizada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Noticia no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<NoticiaResponseDTO> actualizar(@PathVariable Long id, @RequestBody NoticiaRequestDTO requestDTO) {
        Noticia cambios = noticiaMapper.toEntity(requestDTO);
        Noticia actualizada = noticiaService.actualizarNoticia(id, cambios);
        NoticiaResponseDTO response = noticiaMapper.toResponse(actualizada);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar noticia")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Noticia eliminada"),
            @ApiResponse(responseCode = "404", description = "Noticia no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        noticiaService.eliminarNoticia(id);
        return ResponseEntity.noContent().build();
    }
}
