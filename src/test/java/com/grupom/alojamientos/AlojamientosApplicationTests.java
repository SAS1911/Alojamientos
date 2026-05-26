package com.grupom.alojamientos;

import com.grupom.alojamientos.entities.*;
import com.grupom.alojamientos.repositories.AlojamientoRepository;
import com.grupom.alojamientos.repositories.ReservaRepository;
import com.grupom.alojamientos.repositories.UsuarioRepository;
import com.grupom.alojamientos.services.ServicioAnuncios;
import com.grupom.alojamientos.services.ServicioBusqueda;
import com.grupom.alojamientos.services.ServicioReservas;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class AlojamientosApplicationTests {

    @Autowired
    private ServicioReservas servicioReservas;

    @Autowired
    private ServicioBusqueda servicioBusqueda;

    @Autowired
    private ServicioAnuncios servicioAnuncios;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AlojamientoRepository alojamientoRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Test
    @DisplayName("CU-01 - Comprobar reserva con pago síncrono")
    void comprobarCasoUso1ReservaConPagoSincrono() {
        System.out.println("CU-01: Comprobando reserva con pago síncrono y confirmación...");
        Huesped huesped = buscarHuespedPorEmail("juan.perez@example.com");
        Alojamiento alojamiento = buscarAlojamientoPorUbicacion("Madrid");

        LocalDate entrada = LocalDate.now().plusDays(1);
        LocalDate salida = LocalDate.now().plusDays(4);

        Reserva reserva = servicioReservas.registrarReserva(alojamiento.getId(), huesped.getId(), entrada, salida);

        assertThat(reserva).isNotNull();
        assertThat(reserva.getEstado()).isEqualToIgnoringCase("CONFIRMADA");
        assertThat(reserva.getPago()).isNotNull();
        assertThat(reserva.getPago().getImporte()).isGreaterThan(0);
        assertThat(reserva.getHuesped().getId()).isEqualTo(huesped.getId());
        assertThat(reserva.getAlojamiento().getId()).isEqualTo(alojamiento.getId());
        System.out.println("CU-01: Reserva confirmada correctamente con el pago registrado.");
    }

    @Test
    @DisplayName("CU-02 - Comprobar búsqueda de alojamiento y rating")
    void comprobarCasoUso2BusquedaAlojamiento() {
        System.out.println("CU-02: Comprobando búsqueda de alojamiento y cálculo de rating medio...");
        CriterioBusqueda criterio = new CriterioBusqueda();
        criterio.setDestino("Madrid");
        criterio.setFechaEntrada(LocalDate.now().plusDays(2));
        criterio.setFechaSalida(LocalDate.now().plusDays(5));
        criterio.setNumHuespedes(2);

        ResultadoBusqueda resultado = servicioBusqueda.buscar(criterio);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getAlojamientosEncontrados()).isNotEmpty();
        assertThat(resultado.getAlojamientosEncontrados()).allMatch(a -> a.getUbicacion().equalsIgnoreCase("Madrid"));
        assertThat(resultado.getRatingMedioCalculado()).isBetween(4.0, 5.0);
        System.out.println("CU-02: Búsqueda y cálculo de rating funcionaron correctamente.");
    }

    @Test
    @DisplayName("CU-03 - Comprobar cancelación de reserva")
    void comprobarCasoUso3CancelacionReserva() {
        System.out.println("CU-03: Comprobando cancelación de reserva y liberación de fechas...");
        Huesped huesped = buscarHuespedPorEmail("juan.perez@example.com");
        Alojamiento alojamiento = buscarAlojamientoPorUbicacion("Madrid");

        LocalDate entrada = LocalDate.now().plusDays(6);
        LocalDate salida = LocalDate.now().plusDays(9);

        Reserva reserva = servicioReservas.registrarReserva(alojamiento.getId(), huesped.getId(), entrada, salida);
        assertThat(reserva.getEstado()).isEqualToIgnoringCase("CONFIRMADA");

        servicioReservas.procesarCancelacion(reserva.getId());

        Reserva reservaCancelada = reservaRepository.findById(reserva.getId()).orElseThrow();
        assertThat(reservaCancelada.getEstado()).isEqualToIgnoringCase("CANCELADA");
        System.out.println("CU-03: Reserva cancelada correctamente y estado actualizado.");
    }

    @Test
    @DisplayName("CU-04 - Comprobar publicación de anuncio")
    void comprobarCasoUso4PublicacionAnuncio() {
        System.out.println("CU-04: Comprobando creación de anuncio borrador y confirmación...");
        Anfitrion anfitrion = buscarAnfitrionPorEmail("maria.gomez@example.com");

        Alojamiento borrador = servicioAnuncios.publicarAlojamiento(
                anfitrion.getId(),
                "Loft moderno en Sevilla",
                "Loft luminoso y céntrico con todas las comodidades.",
                "Sevilla",
                75.0,
                null
        );

        assertThat(borrador).isNotNull();
        assertThat(borrador.getEstado()).isEqualToIgnoringCase("BORRADOR");
        assertThat(borrador.getImagenes()).isEmpty();

        Alojamiento publicado = servicioAnuncios.confirmarPublicacion(borrador.getId());

        assertThat(publicado).isNotNull();
        assertThat(publicado.getEstado()).isEqualToIgnoringCase("PUBLICADO");
        assertThat(publicado.getDisponibilidades()).isNotEmpty();
        System.out.println("CU-04: Anuncio publicado correctamente y con disponibilidad asignada.");
    }

    private Huesped buscarHuespedPorEmail(String email) {
        return usuarioRepository.findAll().stream()
                .filter(usuario -> usuario instanceof Huesped)
                .map(usuario -> (Huesped) usuario)
                .filter(h -> email.equalsIgnoreCase(h.getEmail()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No se encontró el huésped de prueba"));
    }

    private Anfitrion buscarAnfitrionPorEmail(String email) {
        return usuarioRepository.findAll().stream()
                .filter(usuario -> usuario instanceof Anfitrion)
                .map(usuario -> (Anfitrion) usuario)
                .filter(a -> email.equalsIgnoreCase(a.getEmail()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No se encontró el anfitrión de prueba"));
    }

    private Alojamiento buscarAlojamientoPorUbicacion(String ubicacion) {
        return alojamientoRepository.findAll().stream()
                .filter(a -> ubicacion.equalsIgnoreCase(a.getUbicacion()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No se encontró el alojamiento de prueba en " + ubicacion));
    }
}
