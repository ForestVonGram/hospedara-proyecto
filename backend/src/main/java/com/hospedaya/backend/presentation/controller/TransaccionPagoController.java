package com.hospedaya.backend.presentation.controller;

import com.hospedaya.backend.application.dto.transaccionpago.TransaccionPagoRequestDTO;
import com.hospedaya.backend.application.dto.transaccionpago.TransaccionPagoResponseDTO;
import com.hospedaya.backend.application.mapper.TransaccionPagoMapper;
import com.hospedaya.backend.application.service.base.TransaccionPagoService;
import com.hospedaya.backend.domain.entity.TransaccionPago;
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
@RequestMapping("/transacciones-pago")
@Tag(name = "Transacciones de Pago", description = "Gestión de transacciones de pago")
public class TransaccionPagoController {

    private final TransaccionPagoService transaccionPagoService;
    private final TransaccionPagoMapper transaccionPagoMapper;

    public TransaccionPagoController(TransaccionPagoService transaccionPagoService, TransaccionPagoMapper transaccionPagoMapper) {
        this.transaccionPagoService = transaccionPagoService;
        this.transaccionPagoMapper = transaccionPagoMapper;
    }

    @Operation(summary = "Listar transacciones de pago")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transacciones obtenidas"),
            @ApiResponse(responseCode = "400", description = "Petición inválida"),
            @ApiResponse(responseCode = "404", description = "No se encontraron transacciones")
    })
    @GetMapping
    public ResponseEntity<List<TransaccionPagoResponseDTO>> listarTransacciones() {
        List<TransaccionPago> transacciones = transaccionPagoService.listarTransacciones();
        List<TransaccionPagoResponseDTO> response = transacciones.stream()
                .map(transaccionPagoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener transacción de pago por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transacción obtenida"),
            @ApiResponse(responseCode = "404", description = "Transacción no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TransaccionPagoResponseDTO> obtenerTransaccionPorId(@PathVariable Long id) {
        TransaccionPago transaccion = transaccionPagoService.obtenerTransaccionPorId(id);
        return ResponseEntity.ok(transaccionPagoMapper.toResponse(transaccion));
    }

    @Operation(summary = "Registrar transacción de pago")
    @RequestBody(
            description = "Datos de la transacción de pago",
            required = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{ \"pagoId\": 3, \"monto\": 450000, \"referenciaExterna\": \"PAY-123\", \"detalle\": \"Confirmación del proveedor\" }"
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transacción registrada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    @PostMapping
    public ResponseEntity<TransaccionPagoResponseDTO> registrarTransaccion(@org.springframework.web.bind.annotation.RequestBody TransaccionPagoRequestDTO requestDTO) {
        TransaccionPago transaccion = transaccionPagoMapper.toEntity(requestDTO);
        TransaccionPago creada = transaccionPagoService.registrarTransaccion(transaccion);
        TransaccionPagoResponseDTO response = transaccionPagoMapper.toResponse(creada);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
