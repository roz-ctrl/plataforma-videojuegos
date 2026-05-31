package com.videojuegos.pagos.service;

import com.videojuegos.pagos.client.BibliotecaClient;
import com.videojuegos.pagos.client.CarritoClient;
import com.videojuegos.pagos.client.UsuarioClient;
import com.videojuegos.pagos.client.dto.AdquirirJuegoDTO;
import com.videojuegos.pagos.client.dto.CarritoDTO;
import com.videojuegos.pagos.client.dto.ItemCarritoDTO;
import com.videojuegos.pagos.dto.DetallePagoResponseDTO;
import com.videojuegos.pagos.dto.PagoResponseDTO;
import com.videojuegos.pagos.dto.ProcesarPagoRequestDTO;
import com.videojuegos.pagos.exception.ComunicacionException;
import com.videojuegos.pagos.exception.RecursoNoEncontradoException;
import com.videojuegos.pagos.exception.ReglaNegocioException;
import com.videojuegos.pagos.model.DetallePago;
import com.videojuegos.pagos.model.Pago;
import com.videojuegos.pagos.repository.PagoRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Logica de negocio del microservicio de Pagos.
 *
 * El metodo procesarPago orquesta la compra completa coordinando cuatro
 * microservicios (Carrito, Usuarios, Biblioteca y de vuelta Carrito):
 *   1. Lee el carrito abierto del usuario.
 *   2. Valida que tenga items.
 *   3. Cobra: si paga con SALDO, debita la billetera en usuarios-service.
 *   4. Registra cada juego en la biblioteca del usuario.
 *   5. Marca el carrito como PAGADO.
 *   6. Persiste la transaccion con su detalle.
 */
@Service
public class PagoService {

    private static final Logger log = LoggerFactory.getLogger(PagoService.class);

    private final PagoRepository repository;
    private final CarritoClient carritoClient;
    private final UsuarioClient usuarioClient;
    private final BibliotecaClient bibliotecaClient;

    public PagoService(PagoRepository repository, CarritoClient carritoClient,
                       UsuarioClient usuarioClient, BibliotecaClient bibliotecaClient) {
        this.repository = repository;
        this.carritoClient = carritoClient;
        this.usuarioClient = usuarioClient;
        this.bibliotecaClient = bibliotecaClient;
    }

    @Transactional(readOnly = true)
    public List<PagoResponseDTO> listarPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId).stream()
                .map(this::aResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PagoResponseDTO obtenerPorId(Long id) {
        Pago pago = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el pago con id " + id));
        return aResponseDTO(pago);
    }

    @Transactional
    public PagoResponseDTO procesarPago(ProcesarPagoRequestDTO dto) {
        log.info("Procesando pago del usuario {} con metodo {}", dto.getUsuarioId(), dto.getMetodoPago());

        // 1. Obtener el carrito del usuario desde carrito-service.
        CarritoDTO carrito = obtenerCarrito(dto.getUsuarioId());

        // 2. Regla de negocio: no se paga un carrito vacio.
        if (carrito.getItems() == null || carrito.getItems().isEmpty()) {
            throw new ReglaNegocioException("El carrito esta vacio, no hay nada que pagar");
        }

        // 3. Construir la transaccion.
        Pago pago = new Pago();
        pago.setUsuarioId(dto.getUsuarioId());
        pago.setMonto(carrito.getTotal());
        pago.setMetodoPago(dto.getMetodoPago());
        pago.setReferencia("TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        pago.setFechaPago(LocalDateTime.now());

        for (ItemCarritoDTO item : carrito.getItems()) {
            DetallePago detalle = new DetallePago();
            detalle.setJuegoId(item.getJuegoId());
            detalle.setTituloJuego(item.getTituloJuego());
            detalle.setPrecio(item.getPrecioUnitario());
            pago.agregarDetalle(detalle);
        }

        // 4. Cobrar. Con SALDO debitamos la billetera del usuario.
        if ("SALDO".equals(dto.getMetodoPago())) {
            cobrarConSaldo(dto.getUsuarioId(), carrito.getTotal());
        }
        // Con TARJETA simulamos una aprobacion inmediata de la pasarela.

        // 5. Registrar cada juego en la biblioteca del usuario.
        for (ItemCarritoDTO item : carrito.getItems()) {
            registrarEnBiblioteca(dto.getUsuarioId(), item.getJuegoId());
        }

        // 6. Marcar el carrito como pagado.
        cerrarCarrito(dto.getUsuarioId());

        pago.setEstado("COMPLETADO");
        Pago guardado = repository.save(pago);
        log.info("Pago {} COMPLETADO. Referencia={}, monto={}",
                guardado.getId(), guardado.getReferencia(), guardado.getMonto());
        return aResponseDTO(guardado);
    }

    // ---------- comunicacion remota ----------

    private CarritoDTO obtenerCarrito(Long usuarioId) {
        try {
            return carritoClient.obtenerCarrito(usuarioId);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("El usuario " + usuarioId + " no tiene un carrito abierto");
        } catch (FeignException e) {
            throw new ComunicacionException("No se pudo contactar al servicio de Carrito: " + e.getMessage());
        }
    }

    private void cobrarConSaldo(Long usuarioId, java.math.BigDecimal monto) {
        try {
            usuarioClient.debitarSaldo(usuarioId, monto);
        } catch (FeignException.Conflict e) {
            // usuarios-service responde 409 cuando el saldo es insuficiente.
            throw new ReglaNegocioException("Saldo insuficiente para completar la compra");
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("No existe el usuario con id " + usuarioId);
        } catch (FeignException e) {
            throw new ComunicacionException("No se pudo contactar al servicio de Usuarios: " + e.getMessage());
        }
    }

    private void registrarEnBiblioteca(Long usuarioId, Long juegoId) {
        try {
            bibliotecaClient.adquirir(new AdquirirJuegoDTO(usuarioId, juegoId));
        } catch (FeignException.Conflict e) {
            // El usuario ya posee el juego: lo registramos pero no abortamos la compra.
            log.warn("El usuario {} ya poseia el juego {}; se omite", usuarioId, juegoId);
        } catch (FeignException e) {
            throw new ComunicacionException("No se pudo contactar al servicio de Biblioteca: " + e.getMessage());
        }
    }

    private void cerrarCarrito(Long usuarioId) {
        try {
            carritoClient.marcarComoPagado(usuarioId);
        } catch (FeignException e) {
            throw new ComunicacionException("No se pudo cerrar el carrito: " + e.getMessage());
        }
    }

    // ---------- utilitarios ----------

    private PagoResponseDTO aResponseDTO(Pago pago) {
        PagoResponseDTO dto = new PagoResponseDTO();
        dto.setId(pago.getId());
        dto.setUsuarioId(pago.getUsuarioId());
        dto.setMonto(pago.getMonto());
        dto.setMetodoPago(pago.getMetodoPago());
        dto.setEstado(pago.getEstado());
        dto.setReferencia(pago.getReferencia());
        dto.setFechaPago(pago.getFechaPago());
        dto.setDetalles(pago.getDetalles().stream().map(d -> {
            DetallePagoResponseDTO dd = new DetallePagoResponseDTO();
            dd.setJuegoId(d.getJuegoId());
            dd.setTituloJuego(d.getTituloJuego());
            dd.setPrecio(d.getPrecio());
            return dd;
        }).collect(Collectors.toList()));
        return dto;
    }
}
