package com.hospedaya.backend.application.dto.notificacion;

import com.hospedaya.backend.domain.enums.TipoNotificacion;
import lombok.Data;

@Data
public class NotificacionRequestDTO {

    private Long usuarioId;
    private String mensaje;
    private TipoNotificacion tipo;
}
