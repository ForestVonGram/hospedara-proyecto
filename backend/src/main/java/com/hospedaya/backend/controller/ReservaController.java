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
@RequestMapping("/reservas")
@Tag(name = "Reservas", description = "Gestión de reservas")
public class ReservaController {

    @Operation(summary = "Listar reservas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservas obtenidas"),
            @ApiResponse(responseCode = "400", description = "Petición inválida"),
            @ApiResponse(responseCode = "404", description = "Reservas no encontradas")
    })
    @GetMapping
    public ResponseEntity<?> listarReservas() {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Crear reserva")
    @RequestBody(
            description = "Datos de la reserva",
            required = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{ \"usuarioId\": 1, \"alojamientoId\": 2, \"fechaInicio\": \"2025-10-01\", \"fechaFin\": \"2025-10-05\" }"
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reserva creada"),
            @ApiResponse(responseCode = "400", description = "Fechas inválidas o solapamiento"),
            @ApiResponse(responseCode = "404", description = "Usuario o alojamiento no encontrado")
    })
    @PostMapping
    public ResponseEntity<?> crearReserva() {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

