package com.hospedaya.backend.application.service;

import com.hospedaya.backend.application.dto.mensaje.MensajeRequestDTO;
import com.hospedaya.backend.application.dto.mensaje.MensajeResponseDTO;

import java.util.List;

public interface MensajeService {
    
    /**
     * Envía un mensaje de un usuario a otro relacionado con un alojamiento
     * @param mensajeRequestDTO Datos del mensaje a enviar
     * @param emisorId ID del usuario que envía el mensaje
     * @return El mensaje enviado
     */
    MensajeResponseDTO enviarMensaje(MensajeRequestDTO mensajeRequestDTO, Long emisorId);
    
    /**
     * Obtiene la conversación entre dos usuarios sobre un alojamiento específico
     * @param usuarioId ID del usuario actual
     * @param otroUsuarioId ID del otro usuario en la conversación
     * @param alojamientoId ID del alojamiento relacionado
     * @return Lista de mensajes en la conversación
     */
    List<MensajeResponseDTO> obtenerConversacion(Long usuarioId, Long otroUsuarioId, Long alojamientoId);
    
    /**
     * Marca como leídos todos los mensajes enviados por un usuario a otro sobre un alojamiento
     * @param receptorId ID del usuario que recibe los mensajes
     * @param emisorId ID del usuario que envió los mensajes
     * @param alojamientoId ID del alojamiento relacionado
     * @return Número de mensajes marcados como leídos
     */
    int marcarComoLeidos(Long receptorId, Long emisorId, Long alojamientoId);
    
    /**
     * Obtiene el número de mensajes no leídos para un usuario
     * @param usuarioId ID del usuario
     * @return Número de mensajes no leídos
     */
    long contarMensajesNoLeidos(Long usuarioId);
    
    /**
     * Obtiene los alojamientos con conversaciones para un usuario
     * @param usuarioId ID del usuario
     * @return Lista de alojamientos con conversaciones
     */
    List<Long> obtenerAlojamientosConConversaciones(Long usuarioId);
    
    /**
     * Obtiene los usuarios con los que un usuario ha conversado sobre un alojamiento específico
     * @param usuarioId ID del usuario
     * @param alojamientoId ID del alojamiento
     * @return Lista de usuarios en la conversación
     */
    List<Long> obtenerUsuariosEnConversacion(Long usuarioId, Long alojamientoId);
}