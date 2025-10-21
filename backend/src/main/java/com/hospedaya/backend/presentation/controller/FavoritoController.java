package com.hospedaya.backend.presentation.controller;

import com.hospedaya.backend.application.dto.favorito.FavoritoRequestDTO;
import com.hospedaya.backend.application.dto.favorito.FavoritoResponseDTO;
import com.hospedaya.backend.application.mapper.FavoritoMapper;
import com.hospedaya.backend.application.service.base.FavoritoService;
import com.hospedaya.backend.domain.entity.Favorito;
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
@RequestMapping("/favoritos")
@Tag(name = "Favoritos", description = "Gestión de favoritos de usuarios")
public class FavoritoController {

    private final FavoritoService favoritoService;
    private final FavoritoMapper favoritoMapper;

    public FavoritoController(FavoritoService favoritoService, FavoritoMapper favoritoMapper) {
        this.favoritoService = favoritoService;
        this.favoritoMapper = favoritoMapper;
    }

    @Operation(summary = "Listar favoritos por usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Favoritos obtenidos"),
            @ApiResponse(responseCode = "400", description = "Petición inválida"),
            @ApiResponse(responseCode = "404", description = "No se encontraron favoritos")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<FavoritoResponseDTO>> listarFavoritosPorUsuario(@PathVariable Long usuarioId) {
        List<Favorito> favoritos = favoritoService.listarFavoritosPorUsuario(usuarioId);
        List<FavoritoResponseDTO> response = favoritos.stream()
                .map(favoritoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Agregar favorito")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del favorito",
            required = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{ \"usuarioId\": 1, \"alojamientoId\": 2 }"
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Favorito agregado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario o alojamiento no encontrado")
    })
    @PostMapping(consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
            produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FavoritoResponseDTO> agregarFavorito(@RequestBody FavoritoRequestDTO requestDTO) {
        Favorito favorito = favoritoMapper.toEntity(requestDTO);
        Favorito favoritoCreado = favoritoService.agregarFavorito(favorito);
        FavoritoResponseDTO response = favoritoMapper.toResponse(favoritoCreado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Eliminar favorito")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Favorito eliminado"),
            @ApiResponse(responseCode = "404", description = "Favorito no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarFavorito(@PathVariable Long id) {
        favoritoService.eliminarFavorito(id);
        return ResponseEntity.noContent().build();
    }
}
