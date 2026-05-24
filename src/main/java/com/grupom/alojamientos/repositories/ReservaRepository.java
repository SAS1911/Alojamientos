package com.grupom.alojamientos.repositories;

import com.grupom.alojamientos.entities.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByAlojamientoIdAndEstadoIn(Long alojamientoId, List<String> estados);
}
