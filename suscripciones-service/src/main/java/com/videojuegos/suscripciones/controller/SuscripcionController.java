package com.videojuegos.suscripciones.controller;

import com.videojuegos.suscripciones.dto.SuscripcionRequestDTO;
import com.videojuegos.suscripciones.dto.SuscripcionResponseDTO;
import com.videojuegos.suscripciones.service.SuscripcionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Controlador REST de las Suscripciones de usuarios. */
@RestController
@RequestMapping("/api/suscripciones")
public class SuscripcionController {

    private final SuscripcionService service;

    public SuscripcionController(SuscripcionService service) {
        this.service = service;
    }

    /** Suscribir a un usuario a un plan. */
    @PostMapping
    public ResponseEntity<SuscripcionResponseDTO> suscribir(@Valid @RequestBody SuscripcionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.suscribir(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuscripcionResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    /** Suscripciones de un usuario. */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<SuscripcionResponseDTO>> porUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.listarPorUsuario(usuarioId));
    }

    /** Cancelar una suscripcion. */
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<SuscripcionResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancelar(id));
    }
}
