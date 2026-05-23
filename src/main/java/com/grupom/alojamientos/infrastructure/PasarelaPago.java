package com.grupom.alojamientos.infrastructure;

import com.grupom.alojamientos.entities.Pago;

public interface PasarelaPago {
    Pago procesarCobro(Long reservaId, double importe);
    void procesarReembolso(Long pagoId, double importe);
}