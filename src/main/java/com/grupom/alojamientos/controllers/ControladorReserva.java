package com.grupom.alojamientos.controllers;

import com.grupom.alojamientos.entities.Reserva;
import com.grupom.alojamientos.services.ServicioReservas;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservas")
public class ControladorReserva {

    private final ServicioReservas servicioReservas;

    public ControladorReserva(ServicioReservas servicioReservas) {
        this.servicioReservas = servicioReservas;
    }

    // CU-01: Crear Reserva con Pago síncrono
    @PostMapping
    public ResponseEntity<Reserva> crearReserva(
            @RequestParam Long alojamientoid,
            @RequestParam Long huespedId,
            @RequestParam String entrada,
            @RequestParam String salida) {
        
        // llamarán aquí al método del diagrama de secuencia:
        // Reserva nuevaReserva = servicioReservas.registrarReserva(alojamientoid, huespedId, ...);
        
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // CU-03: Cancelación de Reserva y evaluación de reembolso
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarReserva(@PathVariable Long id) {
        
        // Aquí se invocará la lógica coordinada del ServicioReservas
        // servicioReservas.procesarCancelacion(id);
        
        return ResponseEntity.noContent().build(); // HTTP 204
    }
}