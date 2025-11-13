package com.hospedaya.backend.presentation.controller;

import com.hospedaya.backend.application.dto.mensaje.MensajeRequestDTO;
import com.hospedaya.backend.application.dto.mensaje.MensajeResponseDTO;
import com.hospedaya.backend.application.service.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mensajes")
@CrossOrigin(origins = "*")
public class MensajeController {

    private final MensajeService mensajeService;

    @Autowired
    public MensajeController(MensajeService mensajeService) {
        this.mensajeService = mensajeService;
    }

    @PostMapping
    public ResponseEntity<MensajeResponseDTO> enviarMensaje(@RequestBody MensajeRequestDTO mensajeRequestDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long usuarioId = Long.parseLong(auth.getName());
        MensajeResponseDTO mensajeEnviado = mensajeService.enviarMensaje(mensajeRequestDTO, usuarioId);
        return new ResponseEntity<>(mensajeEnviado, HttpStatus.CREATED);
    }

    @GetMapping("/conversacion")
    public ResponseEntity<List<MensajeResponseDTO>> obtenerConversacion(
            @RequestParam Long otroUsuarioId,
            @RequestParam Long alojamientoId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long usuarioId = Long.parseLong(auth.getName());
        List<MensajeResponseDTO> conversacion = mensajeService.obtenerConversacion(usuarioId, otroUsuarioId, alojamientoId);
        return ResponseEntity.ok(conversacion);
    }

    @PutMapping("/marcar-leidos")
    public ResponseEntity<Integer> marcarComoLeidos(
            @RequestParam Long emisorId,
            @RequestParam Long alojamientoId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long receptorId = Long.parseLong(auth.getName());
        int cantidadMarcados = mensajeService.marcarComoLeidos(receptorId, emisorId, alojamientoId);
        return ResponseEntity.ok(cantidadMarcados);
    }

    @GetMapping("/no-leidos/count")
    public ResponseEntity<Long> contarMensajesNoLeidos() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long usuarioId = Long.parseLong(auth.getName());
        long cantidad = mensajeService.contarMensajesNoLeidos(usuarioId);
        return ResponseEntity.ok(cantidad);
    }

    @GetMapping("/alojamientos")
    public ResponseEntity<List<Long>> obtenerAlojamientosConConversaciones() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long usuarioId = Long.parseLong(auth.getName());
        List<Long> alojamientos = mensajeService.obtenerAlojamientosConConversaciones(usuarioId);
        return ResponseEntity.ok(alojamientos);
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<Long>> obtenerUsuariosEnConversacion(@RequestParam Long alojamientoId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long usuarioId = Long.parseLong(auth.getName());
        List<Long> usuarios = mensajeService.obtenerUsuariosEnConversacion(usuarioId, alojamientoId);
        return ResponseEntity.ok(usuarios);
    }
}