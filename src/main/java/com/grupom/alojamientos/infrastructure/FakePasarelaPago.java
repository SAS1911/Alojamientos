package com.grupom.alojamientos.infrastructure;

import com.grupom.alojamientos.entities.Pago;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class FakePasarelaPago implements PasarelaPago {

    @Override
    public Pago procesarCobro(Long reservaId, double importe) {
        System.out.println("[FAKE-PAGO] Conectando con pasarela... Procesando cobro de " + importe + "€");
        
        // Creamos un objeto Pago de vuestro dominio simulando éxito inmediato
        Pago pago = new Pago();
        pago.setImporte(importe);
        pago.setFechaPago(LocalDateTime.now());
        pago.setEstadoPago("EXITOSO");
        
        return pago;
    }

    @Override
    public void procesarReembolso(Long pagoId, double importe) {
        System.out.println("[FAKE-PAGO] Solicitando devolución del pago #" + pagoId + " por valor de " + importe + "€");
    }
}