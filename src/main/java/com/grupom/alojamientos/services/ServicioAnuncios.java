package com.grupom.alojamientos.services;

import com.grupom.alojamientos.entities.*;
import com.grupom.alojamientos.repositories.*;
import com.grupom.alojamientos.infrastructure.GestorImagenes;
import com.grupom.alojamientos.infrastructure.ServicioNotificaciones;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ServicioAnuncios {

    private final AlojamientoRepository alojamientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final GestorImagenes gestorImagenes;

    public ServicioAnuncios(AlojamientoRepository alojamientoRepository,
                            UsuarioRepository usuarioRepository,
                            GestorImagenes gestorImagenes) {
        this.alojamientoRepository = alojamientoRepository;
        this.usuarioRepository = usuarioRepository;
        this.gestorImagenes = gestorImagenes;
    }

    /**
     * Coordina la primera fase del caso de uso CU-04: Crear anuncio en estado BORRADOR.
     */
    public Alojamiento publicarAlojamiento(Long anfitrionId, String titulo, String descripcion, String ubicacion, double precio, List<MultipartFile> archivosImagenes) {
        System.out.println("[SERVICIO-ANUNCIOS] Iniciando publicación de borrador para Anfitrión #" + anfitrionId);

        // 1. Verificar que el usuario existe y es un Anfitrión
        Usuario usuario = usuarioRepository.findById(anfitrionId)
                .orElseThrow(() -> new IllegalArgumentException("Anfitrión no encontrado"));

        if (!(usuario instanceof Anfitrion)) {
            throw new IllegalArgumentException("El usuario con ID " + anfitrionId + " no es un Anfitrión válido");
        }
        Anfitrion anfitrion = (Anfitrion) usuario;

        // 2. Crear la instancia de Alojamiento en estado "BORRADOR" (Patrón Creador)
        Alojamiento alojamiento = new Alojamiento(titulo, descripcion, ubicacion, precio, "BORRADOR");
        alojamiento.setAnfitrion(anfitrion);

        // Validar los campos esenciales por patrón Experto
        if (!alojamiento.validarCampos()) {
            throw new IllegalArgumentException("Los campos del alojamiento no son válidos (título, descripción, ubicación deben estar completos y precio > 0)");
        }

        // 3. Delegar los archivos binarios al GestorImagenes (Fabricación Pura)
        List<String> urls = gestorImagenes.almacenarImagenes(archivosImagenes);

        // 4. Recibir las URLs y asociar los objetos ImagenAlojamiento
        List<ImagenAlojamiento> imagenes = new ArrayList<>();
        for (int i = 0; i < urls.size(); i++) {
            imagenes.add(new ImagenAlojamiento(urls.get(i), i + 1));
        }
        alojamiento.setImagenes(imagenes);

        // Asignamos una política de cancelación por defecto (FLEXIBLE)
        PoliticaCancelacion politicaDefault = new PoliticaCancelacion("FLEXIBLE", 5);
        alojamiento.setPoliticaCancelacion(politicaDefault);

        // Guardamos y retornamos
        Alojamiento guardado = alojamientoRepository.save(alojamiento);
        System.out.println("[SERVICIO-ANUNCIOS] Borrador del alojamiento #" + guardado.getId() + " creado con éxito.");
        return guardado;
    }

    /**
     * Coordina la segunda fase del caso de uso CU-04: Confirmar y Publicar.
     */
    public Alojamiento confirmarPublicacion(Long alojamientoId) {
        System.out.println("[SERVICIO-ANUNCIOS] Confirmando y publicando oficialmente el Alojamiento #" + alojamientoId);

        Alojamiento alojamiento = alojamientoRepository.findById(alojamientoId)
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

        if ("PUBLICADO".equalsIgnoreCase(alojamiento.getEstado())) {
            throw new IllegalStateException("El alojamiento ya se encuentra publicado");
        }

        // Cambiar estado a PUBLICADO
        alojamiento.setEstado("PUBLICADO");

        // Añadimos una disponibilidad por defecto para que sea localizable en búsquedas
        // Por defecto, estará disponible desde hoy hasta dentro de un año
        Disponibilidad dispDefault = new Disponibilidad(LocalDate.now(), LocalDate.now().plusYears(1), true);
        alojamiento.getDisponibilidades().add(dispDefault);

        Alojamiento guardado = alojamientoRepository.save(alojamiento);
        System.out.println("[SERVICIO-ANUNCIOS] Alojamiento #" + guardado.getId() + " publicado y disponible con éxito.");
        return guardado;
    }
}