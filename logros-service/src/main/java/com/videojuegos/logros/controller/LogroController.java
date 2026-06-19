package com.videojuegos.logros.controller;

import com.videojuegos.logros.dto.DesbloquearRequestDTO;
import com.videojuegos.logros.dto.LogroDesbloqueadoResponseDTO;
import com.videojuegos.logros.dto.LogroRequestDTO;
import com.videojuegos.logros.dto.LogroResponseDTO;
import com.videojuegos.logros.service.LogroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST del microservicio de Logros.
 */
@Tag(name = "Logros", description = "Gestion de logros por juego y su desbloqueo por usuario")
@RestController
@RequestMapping("/api/logros")
public class LogroController {

    private final LogroService service;

    public LogroController(LogroService service) {
        this.service = service;
    }

    @Operation(summary = "Logros de un juego", description = "Lista los logros definidos para un juego")
    @GetMapping("/juego/{juegoId}")
    public ResponseEntity<List<LogroResponseDTO>> porJuego(@PathVariable Long juegoId) {
        return ResponseEntity.ok(service.listarPorJuego(juegoId));
    }

    @Operation(summary = "Crear logro", description = "Crea la definicion de un logro para un juego (valida el juego via Feign)")
    @PostMapping
    public ResponseEntity<LogroResponseDTO> crear(@Valid @RequestBody LogroRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearLogro(dto));
    }

    @Operation(summary = "Eliminar logro", description = "Borra la definicion de un logro")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminarLogro(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Desbloquear logro", description = "Desbloquea un logro para un usuario (no se puede dos veces)")
    @PostMapping("/desbloquear")
    public ResponseEntity<LogroDesbloqueadoResponseDTO> desbloquear(@Valid @RequestBody DesbloquearRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.desbloquear(dto));
    }

    @Operation(summary = "Logros desbloqueados", description = "Lista los logros que un usuario ya desbloqueo")
    @GetMapping("/usuario/{usuarioId}/desbloqueados")
    public ResponseEntity<List<LogroDesbloqueadoResponseDTO>> desbloqueados(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.listarDesbloqueadosPorUsuario(usuarioId));
    }
}
