package com.videojuegos.carrito.controller;

import com.videojuegos.carrito.dto.AgregarItemRequestDTO;
import com.videojuegos.carrito.dto.CarritoResponseDTO;
import com.videojuegos.carrito.service.CarritoService;
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

/**
 * Controlador REST del Carrito de Compras.
 * Las operaciones se organizan en torno al usuario propietario del carrito.
 */
@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    private final CarritoService service;

    public CarritoController(CarritoService service) {
        this.service = service;
    }

    /** Ver el carrito abierto de un usuario. */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<CarritoResponseDTO> verCarrito(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.verCarrito(usuarioId));
    }

    /** Agregar un juego al carrito. */
    @PostMapping("/items")
    public ResponseEntity<CarritoResponseDTO> agregarItem(@Valid @RequestBody AgregarItemRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.agregarItem(dto));
    }

    /** Eliminar un juego del carrito. */
    @DeleteMapping("/usuario/{usuarioId}/juego/{juegoId}")
    public ResponseEntity<CarritoResponseDTO> eliminarItem(@PathVariable Long usuarioId,
                                                          @PathVariable Long juegoId) {
        return ResponseEntity.ok(service.eliminarItem(usuarioId, juegoId));
    }

    /** Vaciar el carrito. */
    @DeleteMapping("/usuario/{usuarioId}")
    public ResponseEntity<CarritoResponseDTO> vaciar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.vaciar(usuarioId));
    }

    /** Marcar el carrito como pagado (lo consume el servicio de Pagos). */
    @PutMapping("/usuario/{usuarioId}/pagar")
    public ResponseEntity<CarritoResponseDTO> marcarComoPagado(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.marcarComoPagado(usuarioId));
    }
}
