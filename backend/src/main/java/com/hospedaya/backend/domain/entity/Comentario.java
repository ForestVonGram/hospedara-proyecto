package com.hospedaya.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "comentarios")
@Getter
@Setter
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "alojamiento_id")
    private Alojamiento alojamiento;

    @Column(nullable = false, length = 1000)
    private String contenido;

    @Column(nullable = false)
    private int calificacion;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
