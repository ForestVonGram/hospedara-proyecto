package com.hospedaya.backend.presentation.controller;

import com.hospedaya.backend.application.dto.alojamientoservicio.AlojamientoServicioRequestDTO;
import com.hospedaya.backend.application.dto.alojamientoservicio.AlojamientoServicioResponseDTO;
import com.hospedaya.backend.application.mapper.AlojamientoServicioMapper;
import com.hospedaya.backend.application.service.base.AlojamientoServicioService;
import com.hospedaya.backend.domain.entity.AlojamientoServicio;
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
@RequestMapping("/alojamiento-servicios")
@Tag(name = "Alojamiento-Servicio", description = "Relación entre alojamientos y servicios")
public class AlojamientoServicioController {

    private final AlojamientoServicioService alojamientoServicioService;
    private final AlojamientoServicioMapper alojamientoServicioMapper;

    public AlojamientoServicioController(AlojamientoServicioService alojamientoServicioService, AlojamientoServicioMapper alojamientoServicioMapper) {
        this.alojamientoServicioService = alojamientoServicioService;
        this.alojamientoServicioMapper = alojamientoServicioMapper;
    }

    @Operation(summary = "Listar servicios por alojamiento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relaciones obtenidas"),
            @ApiResponse(responseCode = "400", description = "Petición inválida"),
            @ApiResponse(responseCode = "404", description = "No se encontraron servicios asociados")
    })
    @GetMapping
    public ResponseEntity<List<AlojamientoServicioResponseDTO>> listarAlojamientoServicios(
            @RequestParam(value = "alojamientoId", required = false) Long alojamientoId) {
        List<AlojamientoServicio> alojamientoServicios = alojamientoServicioService.listarAlojamientoServicios(alojamientoId);
        List<AlojamientoServicioResponseDTO> response = alojamientoServicios.stream()
                .map(alojamientoServicioMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Asignar servicio a alojamiento")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
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
    public ResponseEntity<AlojamientoServicioResponseDTO> asignarServicio(@RequestBody AlojamientoServicioRequestDTO requestDTO) {
        AlojamientoServicio alojamientoServicio = alojamientoServicioMapper.toEntity(requestDTO);
        AlojamientoServicio alojamientoServicioCreado = alojamientoServicioService.crearAlojamientoService(alojamientoServicio);
        AlojamientoServicioResponseDTO response = alojamientoServicioMapper.toResponse(alojamientoServicioCreado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Eliminar relación alojamiento-servicio")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Relación eliminada"),
            @ApiResponse(responseCode = "404", description = "Relación no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAlojamientoServicio(@PathVariable Long id) {
        alojamientoServicioService.eliminarAlojamientoServicio(id);
        return ResponseEntity.noContent().build();
    }
}
