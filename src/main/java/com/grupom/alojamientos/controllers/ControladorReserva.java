package com.grupom.alojamientos.controllers;

import com.grupom.alojamientos.entities.Reserva;
import com.grupom.alojamientos.services.ServicioReservas;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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
        
        System.out.println("[CONTROLADOR-RESERVA] Solicitud de creación de reserva. Alojamiento: " + alojamientoid + ", Huésped: " + huespedId);
        
        Reserva nuevaReserva = servicioReservas.registrarReserva(
                alojamientoid, huespedId, LocalDate.parse(entrada), LocalDate.parse(salida));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaReserva);
    }

    // CU-03: Cancelación de Reserva y evaluación de reembolso
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarReserva(@PathVariable Long id) {
        System.out.println("[CONTROLADOR-RESERVA] Solicitud de cancelación para Reserva #" + id);
        
        servicioReservas.procesarCancelacion(id);
        
        return ResponseEntity.ok().build(); // HTTP 200 OK
    }

    // Devuelve todas las reservas para listarlas en la SPA
    @GetMapping("/listar")
    public ResponseEntity<List<Reserva>> listarReservas() {
        return ResponseEntity.ok(servicioReservas.obtenerTodas());
    }
}