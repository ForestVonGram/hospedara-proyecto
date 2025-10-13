package com.hospedaya.backend.presentation.controller;

import com.hospedaya.backend.application.dto.notificacion.NotificacionRequestDTO;
import com.hospedaya.backend.application.dto.notificacion.NotificacionResponseDTO;
import com.hospedaya.backend.application.mapper.NotificacionMapper;
import com.hospedaya.backend.application.service.base.NotificacionService;
import com.hospedaya.backend.domain.entity.Notificacion;
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
@RequestMapping("/notificaciones")
@Tag(name = "Notificaciones", description = "Gestión de notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;
    private final NotificacionMapper notificacionMapper;

    public NotificacionController(NotificacionService notificacionService, NotificacionMapper notificacionMapper) {
        this.notificacionService = notificacionService;
        this.notificacionMapper = notificacionMapper;
    }

    @Operation(summary = "Listar notificaciones por usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificaciones obtenidas"),
            @ApiResponse(responseCode = "400", description = "Petición inválida"),
            @ApiResponse(responseCode = "404", description = "No se encontraron notificaciones")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<NotificacionResponseDTO>> listarNotificacionesPorUsuario(@PathVariable Long usuarioId) {
        List<Notificacion> notificaciones = notificacionService.listarNotificacionesPorUsuario(usuarioId);
        List<NotificacionResponseDTO> response = notificaciones.stream()
                .map(notificacionMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Crear notificación")
    @RequestBody(
            description = "Datos de la notificación",
            required = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{ \"usuarioId\": 1, \"mensaje\": \"Su reserva fue confirmada\", \"tipo\": \"RESERVA\" }"
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Notificación creada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping
    public ResponseEntity<NotificacionResponseDTO> crearNotificacion(@RequestBody NotificacionRequestDTO requestDTO) {
        Notificacion notificacion = notificacionMapper.toEntity(requestDTO);
        Notificacion creada = notificacionService.enviarNotificacion(notificacion);
        NotificacionResponseDTO response = notificacionMapper.toResponse(creada);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Eliminar notificación")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Notificación eliminada"),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarNotificacion(@PathVariable Long id) {
        notificacionService.eliminarNotificacion(id);
        return ResponseEntity.noContent().build();
    }
}
