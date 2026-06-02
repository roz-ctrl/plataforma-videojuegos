package com.videojuegos.resenas.controller;

import com.videojuegos.resenas.dto.ResenaRequestDTO;
import com.videojuegos.resenas.dto.ResenaResponseDTO;
import com.videojuegos.resenas.dto.ResumenJuegoDTO;
import com.videojuegos.resenas.service.ResenaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST del microservicio de Resenas.
 */
@RestController
@RequestMapping("/api/resenas")
public class ResenaController {

    private final ResenaService service;

    public ResenaController(ResenaService service) {
        this.service = service;
    }

    /** Lista las resenas de un juego. */
    @GetMapping("/juego/{juegoId}")
    public ResponseEntity<List<ResenaResponseDTO>> porJuego(@PathVariable Long juegoId) {
        return ResponseEntity.ok(service.listarPorJuego(juegoId));
    }

    /** Resumen de calificaciones de un juego (promedio, total, recomendaciones). */
    @GetMapping("/juego/{juegoId}/resumen")
    public ResponseEntity<ResumenJuegoDTO> resumen(@PathVariable Long juegoId) {
        return ResponseEntity.ok(service.resumenPorJuego(juegoId));
    }

    @PostMapping
    public ResponseEntity<ResenaResponseDTO> crear(@Valid @RequestBody ResenaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResenaResponseDTO> actualizar(@PathVariable Long id,
                                                       @Valid @RequestBody ResenaRequestDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
