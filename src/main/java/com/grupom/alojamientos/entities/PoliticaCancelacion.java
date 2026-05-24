package com.grupom.alojamientos.entities;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PoliticaCancelacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tipo; // "FLEXIBLE" o "ESTRICTA"
    private int diasAntelacionMax;

    public PoliticaCancelacion() {}

    public PoliticaCancelacion(String tipo, int diasAntelacionMax) {
        this.tipo = tipo;
        this.diasAntelacionMax = diasAntelacionMax;
    }

    // Lógica Experto
    public double calcularReembolso(Reserva reserva, double importeOriginal) {
        long diasAntelacion = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), reserva.getFechaEntrada());
        if (diasAntelacion >= diasAntelacionMax) {
            if ("FLEXIBLE".equalsIgnoreCase(tipo)) {
                return importeOriginal; // 100% reembolso
            } else if ("ESTRICTA".equalsIgnoreCase(tipo)) {
                return importeOriginal * 0.5; // 50% reembolso
            }
        }
        return 0.0; // Sin reembolso por cancelación tardía
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getDiasAntelacionMax() {
        return diasAntelacionMax;
    }

    public void setDiasAntelacionMax(int diasAntelacionMax) {
        this.diasAntelacionMax = diasAntelacionMax;
    }
}