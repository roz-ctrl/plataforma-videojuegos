package com.videojuegos.desarrolladoras.controller;

import com.videojuegos.desarrolladoras.dto.DesarrolladoraRequestDTO;
import com.videojuegos.desarrolladoras.dto.DesarrolladoraResponseDTO;
import com.videojuegos.desarrolladoras.service.DesarrolladoraService;
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
 * Controlador REST del microservicio de Desarrolladoras.
 */
@RestController
@RequestMapping("/api/desarrolladoras")
public class DesarrolladoraController {

    private final DesarrolladoraService service;

    public DesarrolladoraController(DesarrolladoraService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<DesarrolladoraResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DesarrolladoraResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<DesarrolladoraResponseDTO> crear(@Valid @RequestBody DesarrolladoraRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DesarrolladoraResponseDTO> actualizar(@PathVariable Long id,
                                                               @Valid @RequestBody DesarrolladoraRequestDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
