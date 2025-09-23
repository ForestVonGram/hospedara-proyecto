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
@RequestMapping("/servicios")
@Tag(name = "Servicios", description = "Gestión de servicios ofrecidos en alojamientos")
public class ServicioController {

    @Operation(summary = "Listar servicios")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Servicios obtenidos"),
            @ApiResponse(responseCode = "400", description = "Petición inválida"),
            @ApiResponse(responseCode = "404", description = "No se encontraron servicios")
    })
    @GetMapping
    public ResponseEntity<?> listarServicios() {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Crear servicio")
    @RequestBody(
            description = "Datos del servicio",
            required = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{ \"nombre\": \"Piscina\", \"descripcion\": \"Piscina climatizada disponible todo el año\" }"
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Servicio creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Error al registrar servicio")
    })
    @PostMapping
    public ResponseEntity<?> crearServicio() {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
