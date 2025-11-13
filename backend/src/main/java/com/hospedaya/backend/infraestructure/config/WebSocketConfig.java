package com.hospedaya.backend.infraestructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilitar un broker de mensajes basado en memoria con prefijo /topic para mensajes enviados desde el servidor
        config.enableSimpleBroker("/topic");
        
        // Prefijo para mensajes enviados desde el cliente al servidor
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registrar el endpoint /ws-mensajes para conexiones WebSocket
        // Permitir solicitudes de cualquier origen (CORS)
        registry.addEndpoint("/ws-mensajes")
                .setAllowedOrigins("*")
                .withSockJS(); // Habilitar fallback a HTTP si WebSocket no está disponible
    }
}