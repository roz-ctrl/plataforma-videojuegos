package com.videojuegos.pagos.controller;

import com.videojuegos.pagos.dto.PagoResponseDTO;
import com.videojuegos.pagos.dto.ProcesarPagoRequestDTO;
import com.videojuegos.pagos.service.PagoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST del microservicio de Pagos.
 */
@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService service;

    public PagoController(PagoService service) {
        this.service = service;
    }

    /** Procesa el pago del carrito de un usuario. */
    @PostMapping
    public ResponseEntity<PagoResponseDTO> procesar(@Valid @RequestBody ProcesarPagoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.procesarPago(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    /** Historial de pagos de un usuario. */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PagoResponseDTO>> historial(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.listarPorUsuario(usuarioId));
    }
}
