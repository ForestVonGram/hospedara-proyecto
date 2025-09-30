package com.hospedaya.backend.application.dto.notificacion;

import com.hospedaya.backend.domain.enums.TipoNotificacion;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificacionResponseDTO {

    private Long id;
    private Long usuarioId;
    private String mensaje;
    private TipoNotificacion tipo;
    private boolean leida;
    private LocalDateTime fechaCreacion;
}
