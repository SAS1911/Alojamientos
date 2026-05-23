package com.grupom.alojamientos.controllers;

import com.grupom.alojamientos.entities.CriterioBusqueda;
import com.grupom.alojamientos.entities.ResultadoBusqueda;
import com.grupom.alojamientos.services.ServicioBusqueda;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/alojamientos")
public class ControladorBusqueda {

    private final ServicioBusqueda servicioBusqueda;

    // Inyección por constructor (Garantiza bajo acoplamiento)
    public ControladorBusqueda(ServicioBusqueda servicioBusqueda) {
        this.servicioBusqueda = servicioBusqueda;
    }

    @GetMapping("/buscar")
    public ResponseEntity<ResultadoBusqueda> buscarAlojamientos(
            @RequestParam String destino,
            @RequestParam String entrada,
            @RequestParam String salida,
            @RequestParam(required = false) Integer huespedes) {
        
        // 1. Mapeamos los parámetros de la URL al objeto conceptual de vuestro dominio
        CriterioBusqueda criterio = new CriterioBusqueda();
        criterio.setDestino(destino);
        criterio.setFechaEntrada(LocalDate.parse(entrada));
        criterio.setFechaSalida(LocalDate.parse(salida));
        criterio.setNumHuespedes(huespedes);

        // 2. Delegamos en el servicio (Patrón Controlador)
        ResultadoBusqueda resultado = servicioBusqueda.buscar(criterio);

        // 3. Devolvemos el JSON con un estado HTTP 200 OK
        return ResponseEntity.ok(resultado);
    }
}