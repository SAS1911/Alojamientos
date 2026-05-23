package com.grupom.alojamientos.services;

import com.grupom.alojamientos.entities.Alojamiento;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
public class ServicioAnuncios {

    // Aquí se inyectarán:
    // - AlojamientoRepository (Para persistencia real en H2)
    // - GestorImagenes (Fabricación Pura para salvar la cohesión)

    /**
     * Coordina el caso de uso CU-04: Publicación de Alojamiento.
     */
    public Alojamiento publicarAlojamiento(Long anfitrionId, String titulo, String descripcion, double precio, List<MultipartFile> archivosImagenes) {
        // 1. Crear la instancia de Alojamiento en estado "BORRADOR"
        // 2. Delegar de inmediato los archivos binarios al GestorImagenes (Fabricación Pura)
        // 3. Recibir las URLs devueltas y asociar los objetos ImagenAlojamiento al Alojamiento
        // 4. Pasar el estado a "PUBLICADO" y salvar en el repositorio
        
        return null;
    }
}