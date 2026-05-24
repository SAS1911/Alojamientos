package com.grupom.alojamientos.services;

import com.grupom.alojamientos.entities.Resena;
import com.grupom.alojamientos.repositories.ResenaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ServicioResenas {

    private final ResenaRepository resenaRepository;

    public ServicioResenas(ResenaRepository resenaRepository) {
        this.resenaRepository = resenaRepository;
    }

    /**
     * CU-02: Obtiene el promedio de valoraciones de la aplicación (Variación Protegida).
     */
    public double obtenerRatingMedio(List<Long> idAlojamientos) {
        System.out.println("[SERVICIO-RESEÑAS] Calculando rating medio de alojamientos.");
        
        List<Resena> todasResenas = resenaRepository.findAll();
        if (todasResenas.isEmpty()) {
            return 4.8; // Puntuación de cortesía por defecto si no hay reseñas
        }
        return todasResenas.stream()
                .mapToInt(Resena::getPuntuacion)
                .average()
                .orElse(4.8);
    }
}
