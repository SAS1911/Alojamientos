package com.grupom.alojamientos.repositories;

import com.grupom.alojamientos.entities.Alojamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlojamientoRepository extends JpaRepository<Alojamiento, Long> {
    List<Alojamiento> findByUbicacionContainingIgnoreCaseAndEstado(String ubicacion, String estado);
}
