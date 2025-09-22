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
@RequestMapping("/alojamiento-servicios")
@Tag(name = "Alojamiento-Servicio", description = "Relación entre alojamientos y servicios")
public class AlojamientoServicioController {

    @Operation(summary = "Listar servicios por alojamiento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relaciones obtenidas"),
            @ApiResponse(responseCode = "400", description = "Petición inválida"),
            @ApiResponse(responseCode = "404", description = "No se encontraron servicios asociados")
    })
    @GetMapping
    public ResponseEntity<?> listarAlojamientoServicios() {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Asignar servicio a alojamiento")
    @RequestBody(
            description = "Datos de la relación",
            required = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{ \"alojamientoId\": 2, \"servicioId\": 3 }"
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Servicio asignado al alojamiento"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Alojamiento o servicio no encontrado")
    })
    @PostMapping
    public ResponseEntity<?> asignarServicio() {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
