package com.videojuegos.usuarios.controller;

import com.videojuegos.usuarios.dto.UsuarioRequestDTO;
import com.videojuegos.usuarios.dto.UsuarioResponseDTO;
import com.videojuegos.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador REST del microservicio de Usuarios.
 * Solo orquesta: recibe la peticion, delega en el service y devuelve ResponseEntity.
 */
@Tag(name = "Usuarios", description = "Gestion de jugadores registrados y su saldo")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Listar usuarios", description = "Devuelve todos los jugadores registrados")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @Operation(summary = "Obtener usuario por id", description = "Busca un jugador segun su identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe el usuario")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtener(
            @Parameter(description = "Id del usuario", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @Operation(summary = "Crear usuario", description = "Registra un nuevo jugador. Email y nombre de usuario deben ser unicos")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos (validacion)"),
            @ApiResponse(responseCode = "409", description = "Email o nombre de usuario ya existe")
    })
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO creado = usuarioService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Actualizar usuario", description = "Modifica los datos de un jugador existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
            @ApiResponse(responseCode = "404", description = "No existe el usuario"),
            @ApiResponse(responseCode = "409", description = "El email ya esta en uso")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(@PathVariable Long id,
                                                         @Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizar(id, dto));
    }

    @Operation(summary = "Eliminar usuario", description = "Borra un jugador del sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
            @ApiResponse(responseCode = "404", description = "No existe el usuario")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Recargar saldo", description = "Suma saldo a la billetera del jugador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saldo recargado"),
            @ApiResponse(responseCode = "404", description = "No existe el usuario"),
            @ApiResponse(responseCode = "409", description = "El monto debe ser mayor que cero")
    })
    @PutMapping("/{id}/recargar")
    public ResponseEntity<UsuarioResponseDTO> recargar(@PathVariable Long id,
                                                       @Parameter(description = "Monto a recargar", example = "10000")
                                                       @RequestParam BigDecimal monto) {
        return ResponseEntity.ok(usuarioService.recargarSaldo(id, monto));
    }

    @Operation(summary = "Debitar saldo", description = "Descuenta saldo de la billetera (lo usa el servicio de Pagos)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saldo debitado"),
            @ApiResponse(responseCode = "404", description = "No existe el usuario"),
            @ApiResponse(responseCode = "409", description = "Saldo insuficiente o monto invalido")
    })
    @PutMapping("/{id}/debitar")
    public ResponseEntity<UsuarioResponseDTO> debitar(@PathVariable Long id,
                                                      @Parameter(description = "Monto a debitar", example = "5000")
                                                      @RequestParam BigDecimal monto) {
        return ResponseEntity.ok(usuarioService.debitarSaldo(id, monto));
    }
}
