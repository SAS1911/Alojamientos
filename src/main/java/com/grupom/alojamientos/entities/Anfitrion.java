package com.grupom.alojamientos.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Anfitrion extends Usuario {
    private boolean verificado;

    @OneToMany(mappedBy = "anfitrion")
    @JsonIgnore
    private List<Alojamiento> alojamientos = new ArrayList<>();

    public Anfitrion() {}

    public Anfitrion(String nombre, String email, String contrasenia, boolean verificado) {
        super(nombre, email, contrasenia);
        this.verificado = verificado;
    }

    public boolean isVerificado() {
        return verificado;
    }

    public void setVerificado(boolean verificado) {
        this.verificado = verificado;
    }

    public List<Alojamiento> getAlojamientos() {
        return alojamientos;
    }

    public void setAlojamientos(List<Alojamiento> alojamientos) {
        this.alojamientos = alojamientos;
    }
}