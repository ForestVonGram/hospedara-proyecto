package com.hospedaya.backend.controller;

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
@RequestMapping("/alojamientos")
@Tag(name = "Alojamientos", description = "Gestión de alojamientos")
public class AlojamientoController {

    @Operation(summary = "Listar alojamientos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alojamientos obtenidos"),
            @ApiResponse(responseCode = "400", description = "Petición inválida"),
            @ApiResponse(responseCode = "404", description = "No se encontraron alojamientos")
    })
    @GetMapping
    public ResponseEntity<?> listarAlojamientos() {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Crear alojamiento")
    @RequestBody(
            description = "Datos del alojamiento",
            required = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{ \"nombre\": \"Casa rural\", \"descripcion\": \"Acogedora casa en el campo\", \"direccion\": \"Calle 123\", \"precioPorNoche\": 75.5, \"anfitrionId\": 1 }"
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Alojamiento creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Anfitrión no encontrado")
    })
    @PostMapping
    public ResponseEntity<?> crearAlojamiento() {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

