package com.grupom.alojamientos.services;

import com.grupom.alojamientos.entities.Alojamiento;
import com.grupom.alojamientos.entities.CriterioBusqueda;
import com.grupom.alojamientos.repositories.AlojamientoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ServicioAlojamientos {

    private final AlojamientoRepository alojamientoRepository;

    public ServicioAlojamientos(AlojamientoRepository alojamientoRepository) {
        this.alojamientoRepository = alojamientoRepository;
    }

    /**
     * CU-02: Filtra alojamientos en base al destino y que estén en estado PUBLICADO.
     * Patrón Experto en Información aplicado a la persistencia.
     */
    public List<Alojamiento> filtrarPorAtributos(CriterioBusqueda criterio) {
        System.out.println("[SERVICIO-ALOJAMIENTOS] Buscando alojamientos candidatos en: " + criterio.getDestino());
        return alojamientoRepository.findByUbicacionContainingIgnoreCaseAndEstado(criterio.getDestino(), "PUBLICADO");
    }
}
