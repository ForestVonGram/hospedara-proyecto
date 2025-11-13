package com.hospedaya.backend.application.dto.mensaje;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMessageDTO {
    private Long id;
    private Long emisorId;
    private String emisorNombre;
    private Long receptorId;
    private String receptorNombre;
    private Long alojamientoId;
    private String alojamientoNombre;
    private String contenido;
    private LocalDateTime fechaEnvio;
    private Boolean leido;
    
    // Constructor vacío requerido para deserialización
    public ChatMessageDTO() {
    }
    
    // Constructor para crear un ChatMessageDTO a partir de un MensajeResponseDTO
    public ChatMessageDTO(MensajeResponseDTO mensajeResponseDTO) {
        this.id = mensajeResponseDTO.getId();
        this.emisorId = mensajeResponseDTO.getEmisorId();
        this.emisorNombre = mensajeResponseDTO.getEmisorNombre();
        this.receptorId = mensajeResponseDTO.getReceptorId();
        this.receptorNombre = mensajeResponseDTO.getReceptorNombre();
        this.alojamientoId = mensajeResponseDTO.getAlojamientoId();
        this.alojamientoNombre = mensajeResponseDTO.getAlojamientoNombre();
        this.contenido = mensajeResponseDTO.getContenido();
        this.fechaEnvio = mensajeResponseDTO.getFechaEnvio();
        this.leido = mensajeResponseDTO.getLeido();
    }
}