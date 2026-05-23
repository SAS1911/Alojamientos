package com.grupom.alojamientos.services;

import com.grupom.alojamientos.entities.CriterioBusqueda;
import com.grupom.alojamientos.entities.ResultadoBusqueda;
import org.springframework.stereotype.Service;

@Service
public class ServicioBusqueda {

    // Aquí vuestros compañeros inyectarán con @Autowired las interfaces estables de:
    // - ServicioAlojamientos (Fake)
    // - ServicioDisponibilidad (Fake)
    // - ServicioResenas (Fake)

    /**
     * Coordina el caso de uso CU-02: Búsqueda de Alojamiento.
     * En el futuro, usará CompletableFuture para implementar el bloque concurrente 'par'.
     */
    public ResultadoBusqueda buscar(CriterioBusqueda criterio) {
        // 1. Inicializar el objeto de Indirección/Mediador
        ResultadoBusqueda resultado = new ResultadoBusqueda();

        // 2. Aquí se lanzarán las llamadas concurrentes a los fake-services
        // var alojamientos = fakeAlojamientos.obtenerPorDestino(criterio.getDestino());
        // var disponibilidades = fakeDisponibilidad.verificarFechas(...);
        // var valoraciones = fakeResenas.getObtenerRatingMedio();

        // 3. Se delega la agregación en el objeto experto
        // resultado.agregarDatos(alojamientos, disponibilidades, valoraciones);

        return resultado;
    }
}