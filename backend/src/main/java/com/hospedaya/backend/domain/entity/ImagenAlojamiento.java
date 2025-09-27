package com.hospedaya.backend.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "imagenesAlojamientos")
public class ImagenAlojamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
