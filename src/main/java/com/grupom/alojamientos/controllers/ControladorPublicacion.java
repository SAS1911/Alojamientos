package com.grupom.alojamientos.controllers;

import com.grupom.alojamientos.entities.Alojamiento;
import com.grupom.alojamientos.services.ServicioAnuncios;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/anuncios")
public class ControladorPublicacion {

    private final ServicioAnuncios servicioAnuncios;

    public ControladorPublicacion(ServicioAnuncios servicioAnuncios) {
        this.servicioAnuncios = servicioAnuncios;
    }

    // CU-04: Crear anuncio en estado BORRADOR o PUBLICADO
    @PostMapping
    public ResponseEntity<Alojamiento> crearAnuncio(
            @RequestParam Long anfitrionId,
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam double precio,
            @RequestParam List<MultipartFile> imagenes) {
        
        // El servicio coordinará la creación del objeto y usará el GestorImagenes (Fabricación Pura)
        // Alojamiento nuevoAnuncio = servicioAnuncios.publicar(anfitrionId, titulo, ..., imagenes);
        
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}