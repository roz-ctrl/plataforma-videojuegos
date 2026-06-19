package com.videojuegos.biblioteca.controller;

import com.videojuegos.biblioteca.dto.AdquirirJuegoRequestDTO;
import com.videojuegos.biblioteca.dto.EntradaBibliotecaResponseDTO;
import com.videojuegos.biblioteca.service.BibliotecaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Biblioteca", description = "Gestion de los juegos que el usuario ya compro")
@RestController
@RequestMapping("/api/biblioteca")
public class BibliotecaController {

    private final BibliotecaService service;

    public BibliotecaController(BibliotecaService service) {
        this.service = service;
    }

    @Operation(summary = "Listar biblioteca", description = "Lista los juegos que posee un usuario")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<EntradaBibliotecaResponseDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.listarPorUsuario(usuarioId));
    }

    @Operation(summary = "Verificar posesion", description = "Indica si un usuario posee un juego (lo consume Resenas)")
    @GetMapping("/usuario/{usuarioId}/posee/{juegoId}")
    public ResponseEntity<Boolean> poseeJuego(@PathVariable Long usuarioId, @PathVariable Long juegoId) {
        return ResponseEntity.ok(service.usuarioPoseeJuego(usuarioId, juegoId));
    }

    @Operation(summary = "Adquirir juego", description = "Registra la propiedad de un juego (lo consume Pagos)")
    @PostMapping
    public ResponseEntity<EntradaBibliotecaResponseDTO> adquirir(@Valid @RequestBody AdquirirJuegoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.adquirir(dto));
    }

    @Operation(summary = "Registrar horas jugadas", description = "Acumula horas jugadas a una entrada de la biblioteca")
    @PatchMapping("/{id}/horas")
    public ResponseEntity<EntradaBibliotecaResponseDTO> registrarHoras(@PathVariable Long id,
                                                                      @RequestParam int horas) {
        return ResponseEntity.ok(service.registrarHoras(id, horas));
    }

    @Operation(summary = "Cambiar instalado", description = "Marca un juego como instalado o desinstalado")
    @PatchMapping("/{id}/instalado")
    public ResponseEntity<EntradaBibliotecaResponseDTO> cambiarInstalado(@PathVariable Long id,
                                                                        @RequestParam boolean valor) {
        return ResponseEntity.ok(service.cambiarInstalado(id, valor));
    }

    @Operation(summary = "Eliminar entrada", description = "Borra un juego de la biblioteca")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
