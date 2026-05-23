package com.grupom.alojamientos.infrastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class GestorImagenes {

    /**
     * Procesa los archivos binarios enviados por el controlador y devuelve 
     * una lista de URLs virtuales/simuladas para almacenar en el dominio.
     */
    public List<String> almacenarImagenes(List<MultipartFile> archivos) {
        List<String> urlsAlmacenadas = new ArrayList<>();
        
        if (archivos == null || archivos.isEmpty()) {
            return urlsAlmacenadas;
        }

        for (MultipartFile archivo : archivos) {
            System.out.println("[FABRICACIÓN PURA - IMÁGENES] Procesando archivo físico: " + archivo.getOriginalFilename());
            
            // Simulamos el guardado asignando un identificador único (UUID)
            String urlSimulada = "https://cdn.grupom-alojamientos.com/storage/" + UUID.randomUUID() + ".jpg";
            urlsAlmacenadas.add(urlSimulada);
        }

        return urlsAlmacenadas;
    }
}