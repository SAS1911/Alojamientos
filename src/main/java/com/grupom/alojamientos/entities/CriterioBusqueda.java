package com.grupom.alojamientos.entities;

import java.time.LocalDate;

public class CriterioBusqueda {
    private String destino;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private Integer numHuespedes;

    public CriterioBusqueda() {}

    public CriterioBusqueda(String destino, LocalDate fechaEntrada, LocalDate fechaSalida, Integer numHuespedes) {
        this.destino = destino;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.numHuespedes = numHuespedes;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public LocalDate getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(LocalDate fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public Integer getNumHuespedes() {
        return numHuespedes;
    }

    public void setNumHuespedes(Integer numHuespedes) {
        this.numHuespedes = numHuespedes;
    }
}