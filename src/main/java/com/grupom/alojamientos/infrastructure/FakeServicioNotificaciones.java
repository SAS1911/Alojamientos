package com.grupom.alojamientos.infrastructure;

import org.springframework.stereotype.Component;

@Component
public class FakeServicioNotificaciones implements ServicioNotificaciones {

    @Override
    public void notificarConfirmacion(Long reservaId, String emailHuesped) {
        System.out.println("[FAKE-NOTIFICATION] Enviando correo de CONFIRMACIÓN de reserva #" 
                + reservaId + " a " + emailHuesped);
    }

    @Override
    public void notificarCancelacion(Long reservaId, String emailHuesped) {
        System.out.println("[FAKE-NOTIFICATION] Enviando correo de CANCELACIÓN de reserva #" 
                + reservaId + " a " + emailHuesped);
    }
}