package com.videojuegos.carrito.controller;

import com.videojuegos.carrito.dto.AgregarItemRequestDTO;
import com.videojuegos.carrito.dto.CarritoResponseDTO;
import com.videojuegos.carrito.service.CarritoService;
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

/**
 * Controlador REST del Carrito de Compras.
 * Las operaciones se organizan en torno al usuario propietario del carrito.
 */
@Tag(name = "Carrito", description = "Gestion del carrito de compras de cada usuario")
@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    private final CarritoService service;

    public CarritoController(CarritoService service) {
        this.service = service;
    }

    @Operation(summary = "Ver carrito", description = "Devuelve el carrito abierto de un usuario con su total")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<CarritoResponseDTO> verCarrito(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.verCarrito(usuarioId));
    }

    @Operation(summary = "Agregar item", description = "Agrega un juego al carrito (valida usuario y juego via Feign)")
    @PostMapping("/items")
    public ResponseEntity<CarritoResponseDTO> agregarItem(@Valid @RequestBody AgregarItemRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.agregarItem(dto));
    }

    @Operation(summary = "Eliminar item", description = "Quita un juego del carrito de un usuario")
    @DeleteMapping("/usuario/{usuarioId}/juego/{juegoId}")
    public ResponseEntity<CarritoResponseDTO> eliminarItem(@PathVariable Long usuarioId,
                                                          @PathVariable Long juegoId) {
        return ResponseEntity.ok(service.eliminarItem(usuarioId, juegoId));
    }

    @Operation(summary = "Vaciar carrito", description = "Elimina todos los items del carrito")
    @DeleteMapping("/usuario/{usuarioId}")
    public ResponseEntity<CarritoResponseDTO> vaciar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.vaciar(usuarioId));
    }

    @Operation(summary = "Marcar como pagado", description = "Cierra el carrito tras la compra (lo consume el servicio de Pagos)")
    @PutMapping("/usuario/{usuarioId}/pagar")
    public ResponseEntity<CarritoResponseDTO> marcarComoPagado(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.marcarComoPagado(usuarioId));
    }
}
