package com.hospedaya.backend.presentation.controller;

import com.hospedaya.backend.application.dto.alojamiento.AlojamientoResponseDTO;
import com.hospedaya.backend.application.mapper.AlojamientoMapper;
import com.hospedaya.backend.application.service.base.RecomendacionService;
import com.hospedaya.backend.domain.entity.Alojamiento;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/recomendaciones")
@Tag(name = "Recomendaciones", description = "Recomendación de alojamientos basada en historial del usuario")
public class RecomendacionController {

    private final RecomendacionService recomendacionService;
    private final AlojamientoMapper alojamientoMapper;

    public RecomendacionController(RecomendacionService recomendacionService, AlojamientoMapper alojamientoMapper) {
        this.recomendacionService = recomendacionService;
        this.alojamientoMapper = alojamientoMapper;
    }

    @Operation(summary = "Recomendaciones por usuario")
    @ApiResponse(responseCode = "200", description = "Lista de alojamientos recomendados")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<AlojamientoResponseDTO>> recomendarPorUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(name = "limit", required = false, defaultValue = "8") int limit
    ) {
        List<Alojamiento> recs = recomendacionService.recomendarPorUsuario(usuarioId, limit);
        List<AlojamientoResponseDTO> body = recs.stream().map(alojamientoMapper::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(body);
    }
}
