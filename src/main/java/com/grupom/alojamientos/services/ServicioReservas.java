package com.grupom.alojamientos.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupom.alojamientos.entities.*;
import com.grupom.alojamientos.repositories.*;
import com.grupom.alojamientos.infrastructure.PasarelaPago;
import com.grupom.alojamientos.infrastructure.ServicioNotificaciones;

@Service
@Transactional
public class ServicioReservas {

    private final ReservaRepository reservaRepository;
    private final AlojamientoRepository alojamientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasarelaPago pasarelaPago;
    private final ServicioNotificaciones servicioNotificaciones;

    public ServicioReservas(ReservaRepository reservaRepository,
                            AlojamientoRepository alojamientoRepository,
                            UsuarioRepository usuarioRepository,
                            PasarelaPago pasarelaPago,
                            ServicioNotificaciones servicioNotificaciones) {
        this.reservaRepository = reservaRepository;
        this.alojamientoRepository = alojamientoRepository;
        this.usuarioRepository = usuarioRepository;
        this.pasarelaPago = pasarelaPago;
        this.servicioNotificaciones = servicioNotificaciones;
    }

    /**
     * Coordina el caso de uso CU-01: Reserva de Alojamiento.
     */
    public Reserva registrarReserva(Long alojamientoId, Long huespedId, LocalDate entrada, LocalDate salida) {
        System.out.println("[SERVICIO-RESERVAS] Iniciando registro de reserva para Alojamiento #" + alojamientoId + " y Huesped #" + huespedId);

        // 1. Verificar existencia de Alojamiento y Huésped
        Alojamiento alojamiento = alojamientoRepository.findById(alojamientoId)
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));
        
        Usuario usuario = usuarioRepository.findById(huespedId)
                .orElseThrow(() -> new IllegalArgumentException("Huésped no encontrado"));
        
        if (!(usuario instanceof Huesped)) {
            throw new IllegalArgumentException("El usuario con ID " + huespedId + " no es un Huésped válido");
        }
        Huesped huesped = (Huesped) usuario;

        // 2. Comprobar disponibilidad por patrón Experto
        boolean estaDisponible = alojamiento.getDisponibilidades().stream()
                .anyMatch(d -> d.isDisponible() && d.solapaCon(entrada, salida));
        
        if (!estaDisponible) {
            throw new IllegalStateException("Las fechas seleccionadas no están disponibles para este alojamiento");
        }

        // Comprobar solapamientos con reservas ya existentes activas (CONFIRMADA o PENDIENTE)
        List<Reserva> reservasExistentes = reservaRepository.findByAlojamientoIdAndEstadoIn(
                alojamientoId, List.of("PENDIENTE", "CONFIRMADA"));
        
        for (Reserva r : reservasExistentes) {
            boolean solapa = !entrada.isAfter(r.getFechaSalida()) && !salida.isBefore(r.getFechaEntrada());
            if (solapa) {
                throw new IllegalStateException("Ya existe una reserva confirmada o pendiente en las fechas indicadas");
            }
        }

        // 3. Bloquear fechas (Establecer la disponibilidad de ese rango a false)
        for (Disponibilidad d : alojamiento.getDisponibilidades()) {
            if (d.solapaCon(entrada, salida)) {
                d.setDisponible(false);
            }
        }
        alojamientoRepository.save(alojamiento);

        // 4. Crear la instancia de Reserva (Patrón Creador) en estado PENDIENTE
        Reserva reserva = new Reserva(entrada, salida, "PENDIENTE", huesped, alojamiento);
        reserva = reservaRepository.save(reserva);

        // Calcular costo de la estancia
        long noches = ChronoUnit.DAYS.between(entrada, salida);
        if (noches <= 0) noches = 1; // Mínimo 1 noche
        double importeTotal = alojamiento.getPrecioPorNoche() * noches;

        // 5. Invocar de forma síncrona a la Pasarela de Pago (Variación Protegida)
        Pago pago = pasarelaPago.procesarCobro(reserva.getId(), importeTotal);

        if ("EXITOSO".equalsIgnoreCase(pago.getEstadoPago())) {
            // 6. Pasar Reserva a CONFIRMADA e indexar el pago
            reserva.setEstado("CONFIRMADA");
            reserva.setPago(pago);
            reserva = reservaRepository.save(reserva);

            // 7. Notificar confirmación polimórficamente (Patrón Polimorfismo)
            servicioNotificaciones.notificarConfirmacion(reserva.getId(), huesped.getEmail());
            System.out.println("[SERVICIO-RESERVAS] Reserva #" + reserva.getId() + " completada y confirmada con éxito.");
        } else {
            // Si falla el cobro, la reserva queda rechazada y liberamos fechas
            reserva.setEstado("RECHAZADA");
            reservaRepository.save(reserva);
            for (Disponibilidad d : alojamiento.getDisponibilidades()) {
                if (d.solapaCon(entrada, salida)) {
                    d.setDisponible(true);
                }
            }
            alojamientoRepository.save(alojamiento);
            throw new IllegalStateException("El procesamiento de pago ha fallado");
        }

        return reserva;
    }

    /**
     * Coordina el caso de uso CU-03: Cancelación de Reserva.
     */
    public void procesarCancelacion(Long reservaId) {
        System.out.println("[SERVICIO-RESERVAS] Procesando cancelación de reserva #" + reservaId);

        // 1. Recuperar la reserva de la base de datos
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        if ("CANCELADA".equalsIgnoreCase(reserva.getEstado())) {
            throw new IllegalStateException("La reserva ya se encuentra cancelada");
        }

        Alojamiento alojamiento = reserva.getAlojamiento();
        Pago pago = reserva.getPago();
        double importeOriginal = (pago != null) ? pago.getImporte() : 0.0;

        // 2. Delegar el cálculo del dinero a devolver a PoliticaCancelacion (Patrón Experto)
        double reembolso = 0.0;
        if (alojamiento.getPoliticaCancelacion() != null) {
            reembolso = alojamiento.getPoliticaCancelacion().calcularReembolso(reserva, importeOriginal);
        }
        System.out.println("[SERVICIO-RESERVAS] Reembolso calculado por Política de Cancelación: " + reembolso + "€");

        // 3. Liberar las fechas en Disponibilidad
        for (Disponibilidad d : alojamiento.getDisponibilidades()) {
            if (d.solapaCon(reserva.getFechaEntrada(), reserva.getFechaSalida())) {
                d.setDisponible(true);
            }
        }
        alojamientoRepository.save(alojamiento);

        // 4. Procesar el reembolso a través de la pasarela si hubo pago previo
        if (pago != null && reembolso > 0) {
            pasarelaPago.procesarReembolso(pago.getId(), reembolso);
        }

        // 5. Actualizar estado de la reserva
        reserva.setEstado("CANCELADA");
        reservaRepository.save(reserva);

        // 6. Invocar notificación de cancelación polimórficamente
        servicioNotificaciones.notificarCancelacion(reserva.getId(), reserva.getHuesped().getEmail());
        System.out.println("[SERVICIO-RESERVAS] Reserva #" + reserva.getId() + " cancelada correctamente.");
    }

    /**
     * Devuelve todas las reservas en la base de datos (para fines de la demo).
     */
    public List<Reserva> obtenerTodas() {
        return reservaRepository.findAll();
    }
}