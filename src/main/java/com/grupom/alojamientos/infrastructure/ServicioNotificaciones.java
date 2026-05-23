package com.grupom.alojamientos.infrastructure;

public interface ServicioNotificaciones {
    void notificarConfirmacion(Long reservaId, String emailHuesped);
    void notificarCancelacion(Long reservaId, String emailHuesped);
}