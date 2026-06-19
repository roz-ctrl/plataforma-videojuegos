package com.videojuegos.suscripciones.controller;

import com.videojuegos.suscripciones.dto.SuscripcionRequestDTO;
import com.videojuegos.suscripciones.dto.SuscripcionResponseDTO;
import com.videojuegos.suscripciones.service.SuscripcionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Suscripciones", description = "Gestion de las suscripciones de los usuarios a los planes")
@RestController
@RequestMapping("/api/suscripciones")
public class SuscripcionController {

    private final SuscripcionService service;

    public SuscripcionController(SuscripcionService service) {
        this.service = service;
    }

    @Operation(summary = "Suscribir usuario", description = "Suscribe a un usuario a un plan (una sola suscripcion activa por usuario)")
    @PostMapping
    public ResponseEntity<SuscripcionResponseDTO> suscribir(@Valid @RequestBody SuscripcionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.suscribir(dto));
    }

    @Operation(summary = "Obtener suscripcion por id", description = "Busca una suscripcion por su identificador")
    @GetMapping("/{id}")
    public ResponseEntity<SuscripcionResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @Operation(summary = "Suscripciones de un usuario", description = "Lista las suscripciones de un usuario")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<SuscripcionResponseDTO>> porUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.listarPorUsuario(usuarioId));
    }

    @Operation(summary = "Cancelar suscripcion", description = "Cancela una suscripcion activa")
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<SuscripcionResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancelar(id));
    }
}
