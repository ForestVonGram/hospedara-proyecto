package com.hospedaya.backend.presentation.controller;

import com.hospedaya.backend.application.dto.imagenalojamiento.ImagenAlojamientoRequestDTO;
import com.hospedaya.backend.application.dto.imagenalojamiento.ImagenAlojamientoResponseDTO;
import com.hospedaya.backend.application.mapper.ImagenAlojamientoMapper;
import com.hospedaya.backend.application.service.base.ImagenAlojamientoService;
import com.hospedaya.backend.domain.entity.ImagenAlojamiento;
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
@RequestMapping("/imagenes-alojamiento")
@Tag(name = "Imagenes de Alojamiento", description = "Gestión de imágenes de alojamientos")
public class ImagenAlojamientoController {

    private final ImagenAlojamientoService imagenAlojamientoService;
    private final ImagenAlojamientoMapper imagenAlojamientoMapper;

    public ImagenAlojamientoController(ImagenAlojamientoService imagenAlojamientoService,
                                       ImagenAlojamientoMapper imagenAlojamientoMapper) {
        this.imagenAlojamientoService = imagenAlojamientoService;
        this.imagenAlojamientoMapper = imagenAlojamientoMapper;
    }

    @Operation(summary = "Listar imágenes por alojamiento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Imágenes obtenidas"),
            @ApiResponse(responseCode = "400", description = "Petición inválida"),
            @ApiResponse(responseCode = "404", description = "No se encontraron imágenes")
    })
    @GetMapping("/alojamiento/{alojamientoId}")
    public ResponseEntity<List<ImagenAlojamientoResponseDTO>> listarImagenesPorAlojamiento(@PathVariable Long alojamientoId) {
        List<ImagenAlojamiento> imagenes = imagenAlojamientoService.listarImagenesPorAlojamiento(alojamientoId);
        List<ImagenAlojamientoResponseDTO> response = imagenes.stream()
                .map(imagenAlojamientoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Agregar imagen a alojamiento")
    @RequestBody(
            description = "Datos de la imagen",
            required = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{ \"alojamientoId\": 2, \"url\": \"http://imagenes.com/casa1.jpg\" }"
                    )
            )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Imagen agregada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Alojamiento no encontrado")
    })
    @PostMapping
    public ResponseEntity<ImagenAlojamientoResponseDTO> agregarImagen(@org.springframework.web.bind.annotation.RequestBody ImagenAlojamientoRequestDTO requestDTO) {
        // Mapeo manual para garantizar que la asociación con Alojamiento no sea nula
        if (requestDTO == null) {
            throw new IllegalArgumentException("La imagen no puede ser nula");
        }
        if (requestDTO.getAlojamientoId() == null) {
            throw new IllegalArgumentException("La imagen debe estar asociada a un alojamiento");
        }
        if (requestDTO.getUrl() == null || requestDTO.getUrl().trim().isEmpty()) {
            throw new IllegalArgumentException("La URL de la imagen es obligatoria");
        }

        ImagenAlojamiento imagen = new ImagenAlojamiento();
        com.hospedaya.backend.domain.entity.Alojamiento alojamiento = new com.hospedaya.backend.domain.entity.Alojamiento();
        alojamiento.setId(requestDTO.getAlojamientoId());
        imagen.setAlojamiento(alojamiento);
        imagen.setUrl(requestDTO.getUrl().trim());

        ImagenAlojamiento creada = imagenAlojamientoService.agregarImagen(imagen);
        ImagenAlojamientoResponseDTO response = imagenAlojamientoMapper.toResponse(creada);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Eliminar imagen de alojamiento")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Imagen eliminada"),
            @ApiResponse(responseCode = "404", description = "Imagen no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarImagen(@PathVariable Long id) {
        imagenAlojamientoService.eliminarImagen(id);
        return ResponseEntity.noContent().build();
    }
}

