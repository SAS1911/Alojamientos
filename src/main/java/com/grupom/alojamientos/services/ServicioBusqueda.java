package com.grupom.alojamientos.services;

import com.grupom.alojamientos.entities.Alojamiento;
import com.grupom.alojamientos.entities.CriterioBusqueda;
import com.grupom.alojamientos.entities.Resena;
import com.grupom.alojamientos.entities.ResultadoBusqueda;
import com.grupom.alojamientos.repositories.AlojamientoRepository;
import com.grupom.alojamientos.repositories.ResenaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ServicioBusqueda {

    private final AlojamientoRepository alojamientoRepository;
    private final ResenaRepository resenaRepository;

    public ServicioBusqueda(AlojamientoRepository alojamientoRepository, ResenaRepository resenaRepository) {
        this.alojamientoRepository = alojamientoRepository;
        this.resenaRepository = resenaRepository;
    }

    /**
     * Coordina el caso de uso CU-02: Búsqueda de Alojamiento.
     * Utiliza CompletableFuture para implementar el bloque concurrente 'par'.
     */
    public ResultadoBusqueda buscar(CriterioBusqueda criterio) {
        System.out.println("[SERVICIO-BUSQUEDA] Iniciando búsqueda concurrente para destino: " + criterio.getDestino());

        // BLOQUE CONCURRENTE 'par' utilizando CompletableFuture
        
        // 1. Obtener alojamientos filtrados por destino
        CompletableFuture<List<Alojamiento>> futureAlojamientos = CompletableFuture.supplyAsync(() -> {
            System.out.println("[PAR - HILO 1] Consultando alojamientos en " + criterio.getDestino() + "...");
            try { Thread.sleep(60); } catch (InterruptedException ignored) {}
            return alojamientoRepository.findByUbicacionContainingIgnoreCaseAndEstado(criterio.getDestino(), "PUBLICADO");
        });

        // 2. Obtener el rating medio de las reseñas concurrentemente
        CompletableFuture<Double> futureRating = CompletableFuture.supplyAsync(() -> {
            System.out.println("[PAR - HILO 2] Consultando valoraciones medias de los alojamientos...");
            try { Thread.sleep(60); } catch (InterruptedException ignored) {}
            List<Resena> todasResenas = resenaRepository.findAll();
            if (todasResenas.isEmpty()) {
                return 4.8; // Valoración por defecto
            }
            return todasResenas.stream().mapToInt(Resena::getPuntuacion).average().orElse(4.8);
        });

        // Unimos hilos
        CompletableFuture.allOf(futureAlojamientos, futureRating).join();

        List<Alojamiento> alojamientosCandidatos = futureAlojamientos.join();
        double ratingMedio = futureRating.join();

        // 3. Filtrar alojamientos por disponibilidad de fechas (Patrón Experto)
        List<Alojamiento> alojamientosDisponibles = alojamientosCandidatos.stream()
                .filter(aloj -> {
                    if (aloj.getDisponibilidades() == null || aloj.getDisponibilidades().isEmpty()) {
                        return false;
                    }
                    // Debe tener al menos una disponibilidad que cubra las fechas requeridas
                    return aloj.getDisponibilidades().stream()
                            .anyMatch(disp -> disp.isDisponible() && disp.solapaCon(criterio.getFechaEntrada(), criterio.getFechaSalida()));
                })
                .collect(Collectors.toList());

        // 4. Inicializar Indirección/Mediador
        ResultadoBusqueda resultado = new ResultadoBusqueda();

        // 5. Se delega la agregación en el objeto experto
        resultado.agregarDatos(alojamientosDisponibles, ratingMedio);

        // 6. Ordenamos los alojamientos (por ejemplo, por precio)
        resultado.ordenar("PRECIO");

        System.out.println("[SERVICIO-BUSQUEDA] Búsqueda finalizada. Alojamientos encontrados: " + alojamientosDisponibles.size());
        return resultado;
    }
}