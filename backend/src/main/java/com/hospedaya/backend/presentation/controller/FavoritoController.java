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
@RequestMapping("/favoritos")
@Tag(name = "Favoritos", description = "Gestión de favoritos de usuarios")
public class FavoritoController {

    @Operation(summary = "Listar favoritos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Favoritos obtenidos"),
            @ApiResponse(responseCode = "400", description = "Petición inválida"),
            @ApiResponse(responseCode = "404", description = "No se encontraron favoritos")
    })
    @GetMapping
    public ResponseEntity<?> listarFavoritos() {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Agregar favorito")
    @RequestBody(
            description = "Datos del favorito",
            required = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{ \"usuarioId\": 1, \"alojamientoId\": 2 }"
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Favorito agregado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario o alojamiento no encontrado")
    })
    @PostMapping
    public ResponseEntity<?> agregarFavorito() {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
