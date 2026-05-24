package com.grupom.alojamientos.infrastructure;

import com.grupom.alojamientos.entities.*;
import com.grupom.alojamientos.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final AlojamientoRepository alojamientoRepository;
    private final ResenaRepository resenaRepository;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           AlojamientoRepository alojamientoRepository,
                           ResenaRepository resenaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.alojamientoRepository = alojamientoRepository;
        this.resenaRepository = resenaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("[DATA-INITIALIZER] Inicializando base de datos en memoria H2...");

        // 1. Crear Huésped de prueba
        Huesped huesped = new Huesped("Juan Perez", "juan.perez@example.com", "pass123", "TARJETA_CREDITO");
        usuarioRepository.save(huesped);

        // 2. Crear Anfitrión de prueba
        Anfitrion anfitrion = new Anfitrion("Maria Gomez", "maria.gomez@example.com", "pass456", true);
        usuarioRepository.save(anfitrion);

        // 3. Crear Alojamientos publicados
        Alojamiento atico = new Alojamiento(
                "Ático de Lujo en Gran Vía", 
                "Espectacular ático con terraza privada de 40 metros cuadrados en pleno centro de Madrid. Totalmente equipado y reformado.", 
                "Madrid", 
                125.0, 
                "PUBLICADO"
        );
        atico.setAnfitrion(anfitrion);
        
        // Agregar imágenes simuladas
        atico.getImagenes().add(new ImagenAlojamiento("https://cdn.grupom-alojamientos.com/storage/madrid-1.jpg", 1));
        atico.getImagenes().add(new ImagenAlojamiento("https://cdn.grupom-alojamientos.com/storage/madrid-2.jpg", 2));

        // Agregar disponibilidades (Disponible por 6 meses)
        atico.getDisponibilidades().add(new Disponibilidad(LocalDate.now(), LocalDate.now().plusMonths(6), true));

        // Agregar política de cancelación (FLEXIBLE)
        atico.setPoliticaCancelacion(new PoliticaCancelacion("FLEXIBLE", 3));

        alojamientoRepository.save(atico);


        Alojamiento apartamento = new Alojamiento(
                "Apartamento con vistas al mar", 
                "Acogedor apartamento de dos habitaciones en primera línea de playa. Aire acondicionado, wifi de alta velocidad y terraza con vistas al Mediterráneo.", 
                "Barcelona", 
                90.0, 
                "PUBLICADO"
        );
        apartamento.setAnfitrion(anfitrion);
        
        // Agregar imágenes simuladas
        apartamento.getImagenes().add(new ImagenAlojamiento("https://cdn.grupom-alojamientos.com/storage/barcelona-1.jpg", 1));

        // Agregar disponibilidades (Disponible por 6 meses)
        apartamento.getDisponibilidades().add(new Disponibilidad(LocalDate.now(), LocalDate.now().plusMonths(6), true));

        // Agregar política de cancelación (ESTRICTA)
        apartamento.setPoliticaCancelacion(new PoliticaCancelacion("ESTRICTA", 7));

        alojamientoRepository.save(apartamento);

        // 4. Crear Reseñas iniciales
        Resena resena1 = new Resena(5, "¡Increíble ático en el centro de Madrid! La terraza es insuperable y la limpieza impecable.", huesped, atico);
        resenaRepository.save(resena1);

        Resena resena2 = new Resena(4, "Apartamento muy agradable con vistas espectaculares. La política de cancelación estricta es el único contra.", huesped, apartamento);
        resenaRepository.save(resena2);

        System.out.println("[DATA-INITIALIZER] Base de datos H2 inicializada con éxito.");
    }
}
