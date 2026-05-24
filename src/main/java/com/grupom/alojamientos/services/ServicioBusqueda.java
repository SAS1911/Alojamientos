package com.grupom.alojamientos.services;

import com.grupom.alojamientos.entities.Alojamiento;
import com.grupom.alojamientos.entities.CriterioBusqueda;
import com.grupom.alojamientos.entities.ResultadoBusqueda;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ServicioBusqueda {

    // Inyectamos los 3 miniservicios expertos en lugar de los repositorios
    private final ServicioAlojamientos servicioAlojamientos;
    private final ServicioDisponibilidad servicioDisponibilidad;
    private final ServicioResenas servicioResenas;

    public ServicioBusqueda(ServicioAlojamientos servicioAlojamientos, 
                            ServicioDisponibilidad servicioDisponibilidad, 
                            ServicioResenas servicioResenas) {
        this.servicioAlojamientos = servicioAlojamientos;
        this.servicioDisponibilidad = servicioDisponibilidad;
        this.servicioResenas = servicioResenas;
    }

    /**
     * Coordina el caso de uso CU-02: Búsqueda de Alojamiento.
     * Mantiene el bloque concurrente 'par' delegando en los servicios especializados.
     */
    public ResultadoBusqueda buscar(CriterioBusqueda criterio) {
        System.out.println("[SERVICIO-BUSQUEDA] Iniciando búsqueda concurrente para destino: " + criterio.getDestino());

        // BLOQUE CONCURRENTE 'par' utilizando CompletableFuture
        
        // 1. HILO 1: Obtener alojamientos filtrados por destino
        CompletableFuture<List<Alojamiento>> futureAlojamientos = CompletableFuture.supplyAsync(() -> {
            System.out.println("[PAR - HILO 1] Delegando consulta a ServicioAlojamientos...");
            return servicioAlojamientos.filtrarPorAtributos(criterio);
        });

        // 2. HILO 2: Obtener el rating medio de las reseñas concurrentemente
        CompletableFuture<Double> futureRating = CompletableFuture.supplyAsync(() -> {
            System.out.println("[PAR - HILO 2] Delegando consulta a ServicioResenas...");
            // Pasamos null (o una lista vacía) ya que el cálculo se hace a nivel global
            return servicioResenas.obtenerRatingMedio(null);
        });

        // Sincronizamos y unimos ambos hilos
        CompletableFuture.allOf(futureAlojamientos, futureRating).join();

        List<Alojamiento> alojamientosCandidatos = futureAlojamientos.join();
        double ratingMedio = futureRating.join();

        // 3. Filtrar los candidatos por disponibilidad de fechas delegando en el servicio experto
        List<Alojamiento> alojamientosDisponibles = servicioDisponibilidad.filtrarPorDisponibilidad(alojamientosCandidatos, criterio);

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
