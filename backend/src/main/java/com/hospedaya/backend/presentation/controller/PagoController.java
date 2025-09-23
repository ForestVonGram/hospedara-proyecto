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
@RequestMapping("/pagos")
@Tag(name = "Pagos", description = "Gestión de pagos")
public class PagoController {

    @Operation(summary = "Listar pagos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pagos obtenidos"),
            @ApiResponse(responseCode = "400", description = "Petición inválida"),
            @ApiResponse(responseCode = "404", description = "No se encontraron pagos")
    })
    @GetMapping
    public ResponseEntity<?> listarPagos() {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Registrar pago")
    @RequestBody(
            description = "Datos del pago",
            required = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{ \"reservaId\": 5, \"monto\": 450000, \"metodo\": \"tarjeta\", \"estado\": \"COMPLETADO\" }"
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pago registrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    @PostMapping
    public ResponseEntity<?> registrarPago() {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
