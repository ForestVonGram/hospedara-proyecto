package com.hospedaya.backend.presentation.controller;

import com.hospedaya.backend.application.dto.reserva.ReservaRequestDTO;
import com.hospedaya.backend.application.dto.reserva.ReservaResponseDTO;
import com.hospedaya.backend.application.mapper.ReservaMapper;
import com.hospedaya.backend.application.service.base.AlojamientoService;
import com.hospedaya.backend.application.service.base.ReservaService;
import com.hospedaya.backend.application.service.base.UsuarioService;
import com.hospedaya.backend.domain.entity.Alojamiento;
import com.hospedaya.backend.domain.entity.Reserva;
import com.hospedaya.backend.domain.entity.Usuario;
import com.hospedaya.backend.exception.BadRequestException;
import com.hospedaya.backend.exception.ResourceNotFoundException;
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

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reservas")
@Tag(name = "Reservas", description = "Gestión de reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final UsuarioService usuarioService;
    private final AlojamientoService alojamientoService;
    private final ReservaMapper reservaMapper;

    public ReservaController(ReservaService reservaService, UsuarioService usuarioService, AlojamientoService alojamientoService, ReservaMapper reservaMapper) {
        this.reservaService = reservaService;
        this.usuarioService = usuarioService;
        this.alojamientoService = alojamientoService;
        this.reservaMapper = reservaMapper;
    }

    @Operation(summary = "Listar reservas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservas obtenidas"),
            @ApiResponse(responseCode = "400", description = "Petición inválida"),
            @ApiResponse(responseCode = "404", description = "Reservas no encontradas")
    })
    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> listarReservas() {
        List<Reserva> reservas = reservaService.listarReservas();
        List<ReservaResponseDTO> response = reservas.stream().map(reservaMapper::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar reservas por usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservas del usuario obtenidas"),
            @ApiResponse(responseCode = "404", description = "Usuario o reservas no encontradas")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ReservaResponseDTO>> listarReservasPorUsuario(@PathVariable Long usuarioId) {
        List<Reserva> reservas = reservaService.listarReservasPorUsuario(usuarioId);
        List<ReservaResponseDTO> response = reservas.stream().map(reservaMapper::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener reserva por ID de reserva")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva obtenida"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    @GetMapping("/{reservaId}")
    public ResponseEntity<ReservaResponseDTO> obtenerReservaPorId(@PathVariable Long reservaId) {
        Reserva reserva = reservaService.obtenerReservaPorId(reservaId);
        return ResponseEntity.ok(reservaMapper.toResponse(reserva));
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
    public ResponseEntity<ReservaResponseDTO> crearReserva(@jakarta.validation.Valid @org.springframework.web.bind.annotation.RequestBody ReservaRequestDTO requestDTO) {
        // Validaciones básicas para evitar guardar valores nulos en DB
        if (requestDTO.getUsuarioId() == null) {
            throw new BadRequestException("El usuarioId es obligatorio");
        }
        if (requestDTO.getAlojamientoId() == null) {
            throw new BadRequestException("El alojamientoId es obligatorio");
        }
        if (requestDTO.getFechaInicio() == null) {
            throw new BadRequestException("La fecha de inicio es obligatoria");
        }
        if (requestDTO.getFechaFin() == null) {
            throw new BadRequestException("La fecha de fin es obligatoria");
        }

        LocalDate inicio = requestDTO.getFechaInicio();
        LocalDate fin = requestDTO.getFechaFin();
        if (!fin.isAfter(inicio)) {
            throw new BadRequestException("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        // Cargar entidades relacionadas
        Usuario usuario = usuarioService.findById(requestDTO.getUsuarioId());
        if (usuario == null) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + requestDTO.getUsuarioId());
        }
        Alojamiento alojamiento = alojamientoService.obtenerAlojamientoPorId(requestDTO.getAlojamientoId());
        if (alojamiento == null) {
            throw new ResourceNotFoundException("Alojamiento no encontrado con ID: " + requestDTO.getAlojamientoId());
        }

        // Mapear DTO -> Entidad y setear relaciones
        Reserva reserva = reservaMapper.toEntity(requestDTO);
        reserva.setUsuario(usuario);
        reserva.setAlojamiento(alojamiento);

        Reserva creada = reservaService.crearReserva(reserva);
        ReservaResponseDTO response = reservaMapper.toResponse(creada);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Cancelar reserva")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Reserva cancelada"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarReserva(@PathVariable Long id) {
        reservaService.cancelarReserva(id);
        return ResponseEntity.noContent().build();
    }
}

