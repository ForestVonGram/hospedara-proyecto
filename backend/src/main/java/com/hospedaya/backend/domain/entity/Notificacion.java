package com.hospedaya.backend.domain.entity;

import com.hospedaya.backend.domain.enums.TipoNotificacion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Getter
@Setter
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false, length = 255)
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoNotificacion tipo;

    @Column(nullable = false)
    private boolean leida = false; //esto significa si el usuario leyó la notificación

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
