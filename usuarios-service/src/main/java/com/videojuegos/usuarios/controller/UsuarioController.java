package com.videojuegos.usuarios.controller;

import com.videojuegos.usuarios.dto.UsuarioRequestDTO;
import com.videojuegos.usuarios.dto.UsuarioResponseDTO;
import com.videojuegos.usuarios.service.UsuarioService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador REST del microservicio de Usuarios.
 * Solo orquesta: recibe la peticion, delega en el service y devuelve ResponseEntity.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO creado = usuarioService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(@PathVariable Long id,
                                                         @Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /** Recarga saldo: PUT /api/usuarios/{id}/recargar?monto=10000 */
    @PutMapping("/{id}/recargar")
    public ResponseEntity<UsuarioResponseDTO> recargar(@PathVariable Long id,
                                                       @RequestParam BigDecimal monto) {
        return ResponseEntity.ok(usuarioService.recargarSaldo(id, monto));
    }

    /** Debita saldo (consumido por el servicio de Pagos): PUT /api/usuarios/{id}/debitar?monto=5000 */
    @PutMapping("/{id}/debitar")
    public ResponseEntity<UsuarioResponseDTO> debitar(@PathVariable Long id,
                                                      @RequestParam BigDecimal monto) {
        return ResponseEntity.ok(usuarioService.debitarSaldo(id, monto));
    }
}
