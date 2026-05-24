package com.grupom.alojamientos.entities;

import java.util.List;

public class ResultadoBusqueda {
    private List<Alojamiento> alojamientosEncontrados;
    private double ratingMedioCalculado;

    public ResultadoBusqueda() {}

    public ResultadoBusqueda(List<Alojamiento> alojamientosEncontrados, double ratingMedioCalculado) {
        this.alojamientosEncontrados = alojamientosEncontrados;
        this.ratingMedioCalculado = ratingMedioCalculado;
    }

    // Lógica de Indirección/Mediador del CU-02
    public void agregarDatos(List<Alojamiento> alojamientos, double ratingMedio) {
        this.alojamientosEncontrados = alojamientos;
        this.ratingMedioCalculado = ratingMedio;
    }

    // Lógica de ordenación del CU-02
    public void ordenar(String criterioOrden) {
        if (alojamientosEncontrados == null || alojamientosEncontrados.isEmpty()) return;
        if ("PRECIO".equalsIgnoreCase(criterioOrden)) {
            alojamientosEncontrados.sort((a, b) -> Double.compare(a.getPrecioPorNoche(), b.getPrecioPorNoche()));
        } else if ("TITULO".equalsIgnoreCase(criterioOrden)) {
            alojamientosEncontrados.sort((a, b) -> a.getTitulo().compareToIgnoreCase(b.getTitulo()));
        }
    }

    public List<Alojamiento> getAlojamientosEncontrados() {
        return alojamientosEncontrados;
    }

    public void setAlojamientosEncontrados(List<Alojamiento> alojamientosEncontrados) {
        this.alojamientosEncontrados = alojamientosEncontrados;
    }

    public double getRatingMedioCalculado() {
        return ratingMedioCalculado;
    }

    public void setRatingMedioCalculado(double ratingMedioCalculado) {
        this.ratingMedioCalculado = ratingMedioCalculado;
    }
}