package com.grupom.alojamientos.controllers;

import com.grupom.alojamientos.services.ServicioReservas;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservas")
public class ControladorCancelacion {

    private final ServicioReservas servicioReservas;

    public ControladorCancelacion(ServicioReservas servicioReservas) {
        this.servicioReservas = servicioReservas;
    }

    /**
     * CU-03: Cancelación de Reserva y evaluación automática de reembolso.
     * Mapea la petición web directamente hacia el servicio experto de negocio.
     */
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarReserva(@PathVariable Long id) {
        System.out.println("[CONTROLADOR-CANCELACIÓN] Solicitud de cancelación para Reserva #" + id);
        
        // Delegación directa en el servicio que mantiene la lógica impecable
        servicioReservas.procesarCancelacion(id);
        
        return ResponseEntity.ok().build(); // Retorna HTTP 200 OK
    }
}
