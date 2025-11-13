package com.hospedaya.backend.presentation.controller;

import com.hospedaya.backend.application.dto.mensaje.ChatMessageDTO;
import com.hospedaya.backend.application.dto.mensaje.MensajeRequestDTO;
import com.hospedaya.backend.application.dto.mensaje.MensajeResponseDTO;
import com.hospedaya.backend.application.service.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MensajeService mensajeService;

    @Autowired
    public ChatController(SimpMessagingTemplate messagingTemplate, MensajeService mensajeService) {
        this.messagingTemplate = messagingTemplate;
        this.mensajeService = mensajeService;
    }

    /**
     * Maneja los mensajes enviados al destino /app/chat.enviar
     * Guarda el mensaje en la base de datos y lo envía al destinatario a través de WebSocket
     */
    @MessageMapping("/chat.enviar")
    public void enviarMensaje(@Payload MensajeRequestDTO mensajeRequestDTO, Principal principal) {
        // Obtener el ID del usuario autenticado
        Long emisorId = Long.parseLong(principal.getName());
        
        // Guardar el mensaje en la base de datos
        MensajeResponseDTO mensajeGuardado = mensajeService.enviarMensaje(mensajeRequestDTO, emisorId);
        
        // Convertir a ChatMessageDTO para enviar por WebSocket
        ChatMessageDTO chatMessage = new ChatMessageDTO(mensajeGuardado);
        
        // Enviar el mensaje al emisor (para confirmar recepción)
        messagingTemplate.convertAndSendToUser(
                emisorId.toString(),
                "/topic/mensajes",
                chatMessage);
        
        // Enviar el mensaje al receptor
        messagingTemplate.convertAndSendToUser(
                mensajeRequestDTO.getReceptorId().toString(),
                "/topic/mensajes",
                chatMessage);
    }

    /**
     * Maneja los mensajes enviados al destino /app/chat.marcarLeido
     * Marca un mensaje como leído y notifica al emisor original
     */
    @MessageMapping("/chat.marcarLeido")
    public void marcarMensajeComoLeido(@Payload ChatMessageDTO chatMessageDTO, Principal principal) {
        // Obtener el ID del usuario autenticado (receptor del mensaje original)
        Long receptorId = Long.parseLong(principal.getName());
        
        // Marcar los mensajes como leídos
        mensajeService.marcarComoLeidos(receptorId, chatMessageDTO.getEmisorId(), chatMessageDTO.getAlojamientoId());
        
        // Notificar al emisor original que sus mensajes han sido leídos
        chatMessageDTO.setLeido(true);
        messagingTemplate.convertAndSendToUser(
                chatMessageDTO.getEmisorId().toString(),
                "/topic/mensajes.leidos",
                chatMessageDTO);
    }
}