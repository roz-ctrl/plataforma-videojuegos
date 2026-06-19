package com.videojuegos.suscripciones.controller;

import com.videojuegos.suscripciones.dto.PlanRequestDTO;
import com.videojuegos.suscripciones.dto.PlanResponseDTO;
import com.videojuegos.suscripciones.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

/** Controlador REST del catalogo de Planes. */
@Tag(name = "Planes", description = "Catalogo de planes de suscripcion (pases)")
@RestController
@RequestMapping("/api/planes")
public class PlanController {

    private final PlanService service;

    public PlanController(PlanService service) {
        this.service = service;
    }

    @Operation(summary = "Listar planes", description = "Devuelve todos los planes de suscripcion disponibles")
    @GetMapping
    public ResponseEntity<List<PlanResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Obtener plan por id", description = "Busca un plan por su identificador")
    @GetMapping("/{id}")
    public ResponseEntity<PlanResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @Operation(summary = "Crear plan", description = "Registra un nuevo plan de suscripcion")
    @PostMapping
    public ResponseEntity<PlanResponseDTO> crear(@Valid @RequestBody PlanRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @Operation(summary = "Actualizar plan", description = "Modifica un plan existente")
    @PutMapping("/{id}")
    public ResponseEntity<PlanResponseDTO> actualizar(@PathVariable Long id,
                                                     @Valid @RequestBody PlanRequestDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @Operation(summary = "Eliminar plan", description = "Borra un plan del catalogo")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
