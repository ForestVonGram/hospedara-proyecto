package com.hospedaya.backend.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "alojamientosServicios")
public class AlojamientoServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
