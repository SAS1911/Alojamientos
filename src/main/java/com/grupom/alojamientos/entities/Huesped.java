package com.grupom.alojamientos.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Huesped extends Usuario {
    private String metodoPagoRegistrado;

    @OneToMany(mappedBy = "huesped")
    @JsonIgnore
    private List<Reserva> reservas = new ArrayList<>();

    public Huesped() {}

    public Huesped(String nombre, String email, String contrasenia, String metodoPagoRegistrado) {
        super(nombre, email, contrasenia);
        this.metodoPagoRegistrado = metodoPagoRegistrado;
    }

    public String getMetodoPagoRegistrado() {
        return metodoPagoRegistrado;
    }

    public void setMetodoPagoRegistrado(String metodoPagoRegistrado) {
        this.metodoPagoRegistrado = metodoPagoRegistrado;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }
}