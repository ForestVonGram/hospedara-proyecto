package com.hospedaya.backend.presentation.controller;

import com.hospedaya.backend.application.dto.alojamiento.AlojamientoRequestDTO;
import com.hospedaya.backend.application.dto.alojamiento.AlojamientoResponseDTO;
import com.hospedaya.backend.application.dto.alojamiento.AlojamientoUpdateDTO;
import com.hospedaya.backend.application.mapper.AlojamientoMapper;
import com.hospedaya.backend.application.service.base.AlojamientoService;
import com.hospedaya.backend.application.service.base.UsuarioService;
import com.hospedaya.backend.domain.entity.Alojamiento;
import com.hospedaya.backend.domain.entity.Usuario;
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
@RequestMapping("/alojamientos")
@Tag(name = "Alojamientos", description = "Gestión de alojamientos")
public class AlojamientoController {

    private final AlojamientoService alojamientoService;
    private final UsuarioService usuarioService;
    private final AlojamientoMapper alojamientoMapper;

    public AlojamientoController(AlojamientoService alojamientoService, UsuarioService usuarioService, AlojamientoMapper alojamientoMapper) {
        this.alojamientoService = alojamientoService;
        this.usuarioService = usuarioService;
        this.alojamientoMapper = alojamientoMapper;
    }

    @GetMapping 
    public ResponseEntity<List<AlojamientoResponseDTO>> listarAlojamientos() {
        List<Alojamiento> alojamientos = alojamientoService.listarAlojamientos();
        List<AlojamientoResponseDTO> response = alojamientos.stream()
                .map(alojamientoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlojamientoResponseDTO> obtenerAlojamientoPorId(@PathVariable Long id) {
        Alojamiento alojamiento = alojamientoService.obtenerAlojamientoPorId(id);
        AlojamientoResponseDTO response = alojamientoMapper.toResponse(alojamiento);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> crearAlojamiento(@RequestBody AlojamientoRequestDTO requestDTO) {
        //DEBUG 1: Imprimir el contenido del requestDTO
        System.out.println("Datos recibidos en el controlador -> " + requestDTO);
        System.out.println("------- DATOS RECIBIDOS EN EL REQUEST -------");
        System.out.println("Nombre: " + requestDTO.getNombre());
        System.out.println("Descripcion: " + requestDTO.getDescripcion());
        System.out.println("Direccion: " + requestDTO.getDireccion());
        System.out.println("Precio por noche: " + requestDTO.getPrecioPorNoche());
        System.out.println("Anfitrion ID: " + requestDTO.getAnfitrionId());
        System.out.println("---------------------------------------------");

        if (requestDTO.getAnfitrionId() == null) {
            throw new IllegalArgumentException("El id del anfitrión no puede ser nulo");
        }

        Usuario anfitrion = usuarioService.findById(requestDTO.getAnfitrionId());
        System.out.println("DEBUG 2: Resultado de usuarioService.findById() = " + anfitrion);

        if (anfitrion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Anfitrión no encontrado");
        }

        if (anfitrion.getRol() != null && !anfitrion.getRol().name().equals("ANFITRION")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("El usuario no tiene permisos para crear alojamientos");
        }

        Alojamiento alojamiento = alojamientoMapper.toEntity(requestDTO);
        alojamiento.setAnfitrion(anfitrion);

        System.out.println("DEBUG 3: Entidad Alojamiento creada desde DTO -> " + alojamiento);

        Alojamiento alojamientoCreado = alojamientoService.crearAlojamiento(alojamiento);
        AlojamientoResponseDTO response = alojamientoMapper.toResponse(alojamientoCreado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

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

    @Operation(summary = "Eliminar alojamiento por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alojamiento eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Alojamiento no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarAlojamiento(@PathVariable Long id) {
        System.out.println("🔹 Solicitando eliminación del alojamiento con id = " + id);

        try {
            alojamientoService.eliminarAlojamiento(id);
            System.out.println("Alojamiento eliminado correctamente con id = " + id);
            return ResponseEntity.ok("Alojamiento eliminado correctamente");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado al eliminar alojamiento: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor al eliminar alojamiento");
        }
    }

}
