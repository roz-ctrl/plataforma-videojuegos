package com.videojuegos.juegos.controller;

import com.videojuegos.juegos.dto.JuegoRequestDTO;
import com.videojuegos.juegos.dto.JuegoResponseDTO;
import com.videojuegos.juegos.service.JuegoService;
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

/**
 * Controlador REST del microservicio de Juegos.
 */
@Tag(name = "Juegos", description = "Gestion del catalogo de juegos (valida desarrolladora y categoria via Feign)")
@RestController
@RequestMapping("/api/juegos")
public class JuegoController {

    private final JuegoService service;

    public JuegoController(JuegoService service) {
        this.service = service;
    }

    @Operation(summary = "Listar juegos", description = "Devuelve el catalogo con precio final y nombres remotos")
    @GetMapping
    public ResponseEntity<List<JuegoResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Obtener juego por id", description = "Busca un juego por su identificador")
    @GetMapping("/{id}")
    public ResponseEntity<JuegoResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @Operation(summary = "Crear juego", description = "Publica un juego; valida que la desarrolladora y la categoria existan")
    @PostMapping
    public ResponseEntity<JuegoResponseDTO> crear(@Valid @RequestBody JuegoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @Operation(summary = "Actualizar juego", description = "Modifica un juego existente del catalogo")
    @PutMapping("/{id}")
    public ResponseEntity<JuegoResponseDTO> actualizar(@PathVariable Long id,
                                                      @Valid @RequestBody JuegoRequestDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @Operation(summary = "Eliminar juego", description = "Borra un juego del catalogo")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
