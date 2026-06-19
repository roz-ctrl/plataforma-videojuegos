package com.videojuegos.pagos.controller;

import com.videojuegos.pagos.dto.PagoResponseDTO;
import com.videojuegos.pagos.dto.ProcesarPagoRequestDTO;
import com.videojuegos.pagos.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Pagos", description = "Procesa la compra orquestando carrito, usuarios y biblioteca")
@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService service;

    public PagoController(PagoService service) {
        this.service = service;
    }

    @Operation(summary = "Procesar pago", description = "Paga el carrito del usuario: cobra el saldo, registra los juegos en la biblioteca y cierra el carrito")
    @PostMapping
    public ResponseEntity<PagoResponseDTO> procesar(@Valid @RequestBody ProcesarPagoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.procesarPago(dto));
    }

    @Operation(summary = "Obtener pago por id", description = "Busca una transaccion por su identificador")
    @GetMapping("/{id}")
    public ResponseEntity<PagoResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @Operation(summary = "Historial de pagos", description = "Lista todas las transacciones de un usuario")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PagoResponseDTO>> historial(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.listarPorUsuario(usuarioId));
    }
}
