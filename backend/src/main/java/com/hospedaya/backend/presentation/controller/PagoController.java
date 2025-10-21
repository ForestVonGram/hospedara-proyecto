package com.hospedaya.backend.presentation.controller;

import com.hospedaya.backend.application.dto.pago.PagoRequestDTO;
import com.hospedaya.backend.application.dto.pago.PagoResponseDTO;
import com.hospedaya.backend.application.mapper.PagoMapper;
import com.hospedaya.backend.application.service.base.PagoService;
import com.hospedaya.backend.domain.entity.Pago;
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
@RequestMapping("/pagos")
@Tag(name = "Pagos", description = "Gestión de pagos")
public class PagoController {

    private final PagoService pagoService;
    private final PagoMapper pagoMapper;

    public PagoController(PagoService pagoService, PagoMapper pagoMapper) {
        this.pagoService = pagoService;
        this.pagoMapper = pagoMapper;
    }

    @Operation(summary = "Listar pagos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pagos obtenidos"),
            @ApiResponse(responseCode = "400", description = "Petición inválida"),
            @ApiResponse(responseCode = "404", description = "No se encontraron pagos")
    })
    @GetMapping
    public ResponseEntity<List<PagoResponseDTO>> listarPagos() {
        List<Pago> pagos = pagoService.listarPagos();
        List<PagoResponseDTO> response = pagos.stream().map(pagoMapper::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener pago por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago obtenido"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PagoResponseDTO> obtenerPagoPorId(@PathVariable Long id) {
        Pago pago = pagoService.obtenerPagoPorId(id);
        return ResponseEntity.ok(pagoMapper.toResponse(pago));
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
    public ResponseEntity<PagoResponseDTO> registrarPago(@org.springframework.web.bind.annotation.RequestBody PagoRequestDTO requestDTO) {
        Pago pago = pagoMapper.toEntity(requestDTO);
        Pago creado = pagoService.registrarPago(pago);
        PagoResponseDTO response = pagoMapper.toResponse(creado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Eliminar pago")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pago eliminado"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPago(@PathVariable Long id) {
        pagoService.eliminarPago(id);
        return ResponseEntity.noContent().build();
    }
}
