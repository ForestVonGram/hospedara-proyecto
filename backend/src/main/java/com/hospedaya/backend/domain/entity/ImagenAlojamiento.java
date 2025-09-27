package com.hospedaya.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "imagenesAlojamientos")
@Getter
@Setter
public class ImagenAlojamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "alojamiento_id", nullable = false)
    private Alojamiento alojamiento;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(nullable = false)
    private boolean principal = false; //esto indica si la imagen es principal, o sea, destacada!!
}
