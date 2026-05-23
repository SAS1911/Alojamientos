package com.grupom.alojamientos.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Alojamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String descripcion;
    private String ubicacion;
    private double precioPorNoche;
    private String estado; // Para controlar si es "BORRADOR" o "PUBLICADO" (CU-04)
}