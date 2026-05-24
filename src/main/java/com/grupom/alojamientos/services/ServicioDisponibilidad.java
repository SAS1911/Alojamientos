package com.grupom.alojamientos.services;

import com.grupom.alojamientos.entities.Alojamiento;
import com.grupom.alojamientos.entities.CriterioBusqueda;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicioDisponibilidad {

    /**
     * CU-02: Filtra una lista de alojamientos candidatos verificando sus fechas libres.
     * Patrón Experto: Delega en la entidad Disponibilidad la lógica de solapamiento.
     */
    public List<Alojamiento> filtrarPorDisponibilidad(List<Alojamiento> candidatos, CriterioBusqueda criterio) {
        System.out.println("[SERVICIO-DISPONIBILIDAD] Filtrando disponibilidad para fechas solicitadas.");
        
        return candidatos.stream()
                .filter(aloj -> {
                    if (aloj.getDisponibilidades() == null || aloj.getDisponibilidades().isEmpty()) {
                        return false;
                    }
                    // Verifica si tiene bloques de fechas disponibles que cubran el rango solicitado
                    return aloj.getDisponibilidades().stream()
                            .anyMatch(disp -> disp.isDisponible() && disp.solapaCon(criterio.getFechaEntrada(), criterio.getFechaSalida()));
                })
                .collect(Collectors.toList());
    }
}
