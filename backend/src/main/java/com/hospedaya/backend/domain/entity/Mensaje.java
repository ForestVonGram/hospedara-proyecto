package com.hospedaya.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "mensajes")
public class Mensaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "emisor_id", nullable = false)
    private Usuario emisor;

    @ManyToOne
    @JoinColumn(name = "receptor_id", nullable = false)
    private Usuario receptor;

    @ManyToOne
    @JoinColumn(name = "alojamiento_id", nullable = false)
    private Alojamiento alojamiento;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(nullable = false)
    private LocalDateTime fechaEnvio = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean leido = false;
}