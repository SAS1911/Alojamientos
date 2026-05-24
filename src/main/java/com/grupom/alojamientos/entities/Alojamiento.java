package com.grupom.alojamientos.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.CascadeType;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Alojamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String descripcion;
    private String ubicacion;
    private double precioPorNoche;
    private String estado; // "BORRADOR" o "PUBLICADO"

    @ManyToOne
    private Anfitrion anfitrion;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = jakarta.persistence.FetchType.EAGER)
    private List<ImagenAlojamiento> imagenes = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = jakarta.persistence.FetchType.EAGER)
    private List<Disponibilidad> disponibilidades = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    private PoliticaCancelacion politicaCancelacion;

    @OneToMany(mappedBy = "alojamiento", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Resena> resenas = new ArrayList<>();

    public Alojamiento() {}

    public Alojamiento(String titulo, String descripcion, String ubicacion, double precioPorNoche, String estado) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.precioPorNoche = precioPorNoche;
        this.estado = estado;
    }

    // CU-04 Lógica Experto
    public boolean validarCampos() {
        return titulo != null && !titulo.trim().isEmpty() &&
               descripcion != null && !descripcion.trim().isEmpty() &&
               ubicacion != null && !ubicacion.trim().isEmpty() &&
               precioPorNoche > 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public double getPrecioPorNoche() {
        return precioPorNoche;
    }

    public void setPrecioPorNoche(double precioPorNoche) {
        this.precioPorNoche = precioPorNoche;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Anfitrion getAnfitrion() {
        return anfitrion;
    }

    public void setAnfitrion(Anfitrion anfitrion) {
        this.anfitrion = anfitrion;
    }

    public List<ImagenAlojamiento> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<ImagenAlojamiento> imagenes) {
        this.imagenes = imagenes;
    }

    public List<Disponibilidad> getDisponibilidades() {
        return disponibilidades;
    }

    public void setDisponibilidades(List<Disponibilidad> disponibilidades) {
        this.disponibilidades = disponibilidades;
    }

    public PoliticaCancelacion getPoliticaCancelacion() {
        return politicaCancelacion;
    }

    public void setPoliticaCancelacion(PoliticaCancelacion politicaCancelacion) {
        this.politicaCancelacion = politicaCancelacion;
    }

    public List<Resena> getResenas() {
        return resenas;
    }

    public void setResenas(List<Resena> resenas) {
        this.resenas = resenas;
    }
}