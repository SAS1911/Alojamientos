package com.grupom.alojamientos.services;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.grupom.alojamientos.entities.Reserva;

@Service
public class ServicioReservas {

    // Aquí se inyectarán:
    // - ReservaRepository (Para persistencia real en H2)
    // - PasarelaPago (Interfaz - Variación Protegida)
    // - ServicioNotificaciones (Interfaz - Polimorfismo)

    /**
     * Coordina el caso de uso CU-01: Reserva de Alojamiento.
     */
    public Reserva registrarReserva(Long alojamientoId, Long huespedId, LocalDate entrada, LocalDate salida) {
        // 1. Validar y bloquear fechas en la entidad Disponibilidad por patrón Experto
        // 2. Crear la instancia de Reserva (Patrón Creador) en estado PENDIENTE
        // 3. Invocar de forma síncrona a la Pasarela de Pago simulada (Fake)
        // 4. Si el pago es exitoso, pasar Reserva a CONFIRMADA y disparar ServicioNotificaciones
        
        return null; 
    }

    /**
     * Coordina el caso de uso CU-03: Cancelación de Reserva.
     */
    public void procesarCancelacion(Long reservaId) {
        // 1. Recuperar la reserva de la base de datos
        // 2. Delegar el cálculo del dinero a devolver a PoliticaCancelacion (Patrón Experto)
        // 3. Liberar las fechas en Disponibilidad
        // 4. Procesar el reembolso e invocar la notificación de cancelación
    }
}