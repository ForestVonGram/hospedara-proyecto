package com.hospedaya.backend.presentation.controller;

import com.hospedaya.backend.application.dto.servicio.ServicioRequestDTO;
import com.hospedaya.backend.application.dto.servicio.ServicioResponseDTO;
import com.hospedaya.backend.application.mapper.ServicioMapper;
import com.hospedaya.backend.application.service.base.ServicioService;
import com.hospedaya.backend.domain.entity.Servicio;
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
@RequestMapping("/servicios")
@Tag(name = "Servicios", description = "Gestión de servicios ofrecidos en alojamientos")
public class ServicioController {

    private final ServicioService servicioService;
    private final ServicioMapper servicioMapper;

    public ServicioController(ServicioService servicioService, ServicioMapper servicioMapper) {
        this.servicioService = servicioService;
        this.servicioMapper = servicioMapper;
    }

    @Operation(summary = "Listar servicios")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Servicios obtenidos"),
            @ApiResponse(responseCode = "400", description = "Petición inválida"),
            @ApiResponse(responseCode = "404", description = "No se encontraron servicios")
    })
    @GetMapping
    public ResponseEntity<List<ServicioResponseDTO>> listarServicios() {
        List<Servicio> servicios = servicioService.listarServicios();
        List<ServicioResponseDTO> response = servicios.stream()
                .map(servicioMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener servicio por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Servicio obtenido"),
            @ApiResponse(responseCode = "404", description = "Servicio no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ServicioResponseDTO> obtenerServicioPorId(@PathVariable Long id) {
        Servicio servicio = servicioService.obtenerServicioPorId(id);
        return ResponseEntity.ok(servicioMapper.toResponse(servicio));
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
    public ResponseEntity<ServicioResponseDTO> crearServicio(@org.springframework.web.bind.annotation.RequestBody ServicioRequestDTO requestDTO) {
        Servicio servicio = servicioMapper.toEntity(requestDTO);
        Servicio creado = servicioService.crearServicio(servicio);
        ServicioResponseDTO response = servicioMapper.toResponse(creado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Eliminar servicio")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Servicio eliminado"),
            @ApiResponse(responseCode = "404", description = "Servicio no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarServicio(@PathVariable Long id) {
        servicioService.eliminarServicio(id);
        return ResponseEntity.noContent().build();
    }
}
