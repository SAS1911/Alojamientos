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

    // CU-04: Crear anuncio en estado BORRADOR
    @PostMapping
    public ResponseEntity<Alojamiento> crearAnuncio(
            @RequestParam Long anfitrionId,
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam String ubicacion,
            @RequestParam double precio,
            @RequestParam(required = false) List<MultipartFile> imagenes) {
        
        System.out.println("[CONTROLADOR-PUBLICACION] Creando anuncio borrador. Anfitrión: " + anfitrionId + ", Título: " + titulo);
        
        Alojamiento nuevoAnuncio = servicioAnuncios.publicarAlojamiento(
                anfitrionId, titulo, descripcion, ubicacion, precio, imagenes);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoAnuncio);
    }

    // CU-04 (Paso 2): Confirmar publicación oficial
    @PostMapping("/{id}/confirmar")
    public ResponseEntity<Alojamiento> confirmarAnuncio(@PathVariable Long id) {
        System.out.println("[CONTROLADOR-PUBLICACION] Confirmando publicación para Alojamiento #" + id);
        
        Alojamiento publicado = servicioAnuncios.confirmarPublicacion(id);
        
        return ResponseEntity.ok(publicado);
    }
}