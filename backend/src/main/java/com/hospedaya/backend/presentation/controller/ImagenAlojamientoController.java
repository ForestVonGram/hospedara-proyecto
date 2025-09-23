package com.hospedaya.backend.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/imagenes-alojamiento")
@Tag(name = "Imagenes de Alojamiento", description = "Gestión de imágenes de alojamientos")
public class ImagenAlojamientoController {

    @Operation(summary = "Listar imágenes de alojamientos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Imágenes obtenidas"),
            @ApiResponse(responseCode = "400", description = "Petición inválida"),
            @ApiResponse(responseCode = "404", description = "No se encontraron imágenes")
    })
    @GetMapping
    public ResponseEntity<?> listarImagenes() {
        return ResponseEntity.ok().build();
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
    public ResponseEntity<?> agregarImagen() {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

