package com.hospedaya.backend.presentation.controller;

import com.hospedaya.backend.application.dto.alojamiento.AlojamientoRequestDTO;
import com.hospedaya.backend.application.dto.alojamiento.AlojamientoResponseDTO;
import com.hospedaya.backend.application.dto.alojamiento.AlojamientoUpdateDTO;
import com.hospedaya.backend.application.mapper.AlojamientoMapper;
import com.hospedaya.backend.application.service.base.AlojamientoService;
import com.hospedaya.backend.domain.entity.Alojamiento;
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
@RequestMapping("/alojamientos")
@Tag(name = "Alojamientos", description = "Gestión de alojamientos")
public class AlojamientoController {

    private final AlojamientoService alojamientoService;
    private final AlojamientoMapper alojamientoMapper;

    public AlojamientoController(AlojamientoService alojamientoService, AlojamientoMapper alojamientoMapper) {
        this.alojamientoService = alojamientoService;
        this.alojamientoMapper = alojamientoMapper;
    }

    @Operation(summary = "Listar alojamientos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alojamientos obtenidos"),
            @ApiResponse(responseCode = "400", description = "Petición inválida"),
            @ApiResponse(responseCode = "404", description = "No se encontraron alojamientos")
    })
    @GetMapping
    public ResponseEntity<List<AlojamientoResponseDTO>> listarAlojamientos() {
        List<Alojamiento> alojamientos = alojamientoService.listarAlojamientos();
        List<AlojamientoResponseDTO> response = alojamientos.stream()
                .map(alojamientoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener alojamiento por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alojamiento obtenido"),
            @ApiResponse(responseCode = "404", description = "Alojamiento no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AlojamientoResponseDTO> obtenerAlojamientoPorId(@PathVariable Long id) {
        Alojamiento alojamiento = alojamientoService.obtenerAlojamientoPorId(id);
        AlojamientoResponseDTO response = alojamientoMapper.toResponse(alojamiento);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Crear alojamiento")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del alojamiento",
            required = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{ \"titulo\": \"Casa rural\", \"descripcion\": \"Acogedora casa en el campo\", \"direccion\": \"Calle 123\", \"precioPorNoche\": 75.5, \"anfitrionId\": 1 }"
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Alojamiento creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Anfitrión no encontrado")
    })
    @PostMapping
    public ResponseEntity<AlojamientoResponseDTO> crearAlojamiento(@RequestBody AlojamientoRequestDTO requestDTO) {
        Alojamiento alojamiento = alojamientoMapper.toEntity(requestDTO);
        Alojamiento alojamientoCreado = alojamientoService.crearAlojamiento(alojamiento);
        AlojamientoResponseDTO response = alojamientoMapper.toResponse(alojamientoCreado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Actualizar alojamiento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alojamiento actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Alojamiento no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AlojamientoResponseDTO> actualizarAlojamiento(
            @PathVariable Long id,
            @RequestBody AlojamientoUpdateDTO updateDTO) {
        Alojamiento alojamiento = alojamientoService.obtenerAlojamientoPorId(id);
        alojamientoMapper.updateEntityFromDto(updateDTO, alojamiento);
        Alojamiento alojamientoActualizado = alojamientoService.crearAlojamiento(alojamiento);
        AlojamientoResponseDTO response = alojamientoMapper.toResponse(alojamientoActualizado);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar alojamiento")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Alojamiento eliminado"),
            @ApiResponse(responseCode = "404", description = "Alojamiento no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAlojamiento(@PathVariable Long id) {
        alojamientoService.eliminarAlojamiento(id);
        return ResponseEntity.noContent().build();
    }
}
