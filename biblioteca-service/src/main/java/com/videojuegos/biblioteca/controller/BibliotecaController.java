package com.videojuegos.biblioteca.controller;

import com.videojuegos.biblioteca.dto.AdquirirJuegoRequestDTO;
import com.videojuegos.biblioteca.dto.EntradaBibliotecaResponseDTO;
import com.videojuegos.biblioteca.service.BibliotecaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST de la Biblioteca.
 */
@RestController
@RequestMapping("/api/biblioteca")
public class BibliotecaController {

    private final BibliotecaService service;

    public BibliotecaController(BibliotecaService service) {
        this.service = service;
    }

    /** Lista los juegos que posee un usuario. */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<EntradaBibliotecaResponseDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.listarPorUsuario(usuarioId));
    }

    /** Verifica si un usuario posee un juego (consumido por el servicio de Reseñas). */
    @GetMapping("/usuario/{usuarioId}/posee/{juegoId}")
    public ResponseEntity<Boolean> poseeJuego(@PathVariable Long usuarioId, @PathVariable Long juegoId) {
        return ResponseEntity.ok(service.usuarioPoseeJuego(usuarioId, juegoId));
    }

    /** Registra la adquisicion de un juego (consumido por el servicio de Pagos). */
    @PostMapping
    public ResponseEntity<EntradaBibliotecaResponseDTO> adquirir(@Valid @RequestBody AdquirirJuegoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.adquirir(dto));
    }

    /** Acumula horas jugadas a una entrada. */
    @PatchMapping("/{id}/horas")
    public ResponseEntity<EntradaBibliotecaResponseDTO> registrarHoras(@PathVariable Long id,
                                                                      @RequestParam int horas) {
        return ResponseEntity.ok(service.registrarHoras(id, horas));
    }

    /** Marca un juego como instalado/desinstalado. */
    @PatchMapping("/{id}/instalado")
    public ResponseEntity<EntradaBibliotecaResponseDTO> cambiarInstalado(@PathVariable Long id,
                                                                        @RequestParam boolean valor) {
        return ResponseEntity.ok(service.cambiarInstalado(id, valor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
