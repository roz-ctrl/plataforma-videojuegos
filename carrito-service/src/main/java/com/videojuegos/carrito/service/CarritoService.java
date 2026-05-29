package com.videojuegos.carrito.service;

import com.videojuegos.carrito.client.JuegoClient;
import com.videojuegos.carrito.client.UsuarioClient;
import com.videojuegos.carrito.client.dto.JuegoDTO;
import com.videojuegos.carrito.client.dto.UsuarioDTO;
import com.videojuegos.carrito.dto.AgregarItemRequestDTO;
import com.videojuegos.carrito.dto.CarritoResponseDTO;
import com.videojuegos.carrito.dto.ItemResponseDTO;
import com.videojuegos.carrito.exception.ComunicacionException;
import com.videojuegos.carrito.exception.RecursoNoEncontradoException;
import com.videojuegos.carrito.exception.ReglaNegocioException;
import com.videojuegos.carrito.model.Carrito;
import com.videojuegos.carrito.model.ItemCarrito;
import com.videojuegos.carrito.repository.CarritoRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Logica de negocio del Carrito de Compras.
 * Reglas principales:
 *  - Solo puede existir un carrito ABIERTO por usuario.
 *  - No se puede agregar dos veces el mismo juego (los juegos son digitales).
 *  - El usuario y el juego deben existir y estar activos (validados via Feign).
 */
@Service
public class CarritoService {

    private static final Logger log = LoggerFactory.getLogger(CarritoService.class);
    private static final String ESTADO_ABIERTO = "ABIERTO";
    private static final String ESTADO_PAGADO = "PAGADO";

    private final CarritoRepository repository;
    private final UsuarioClient usuarioClient;
    private final JuegoClient juegoClient;

    public CarritoService(CarritoRepository repository, UsuarioClient usuarioClient, JuegoClient juegoClient) {
        this.repository = repository;
        this.usuarioClient = usuarioClient;
        this.juegoClient = juegoClient;
    }

    @Transactional(readOnly = true)
    public CarritoResponseDTO verCarrito(Long usuarioId) {
        Carrito carrito = repository.findByUsuarioIdAndEstado(usuarioId, ESTADO_ABIERTO)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El usuario " + usuarioId + " no tiene un carrito abierto"));
        return aResponseDTO(carrito);
    }

    @Transactional
    public CarritoResponseDTO agregarItem(AgregarItemRequestDTO dto) {
        log.info("Agregando juego {} al carrito del usuario {}", dto.getJuegoId(), dto.getUsuarioId());

        validarUsuario(dto.getUsuarioId());
        JuegoDTO juego = obtenerJuego(dto.getJuegoId());
        if (!juego.isActivo()) {
            throw new ReglaNegocioException("El juego '" + juego.getTitulo() + "' no esta disponible para la venta");
        }

        Carrito carrito = repository.findByUsuarioIdAndEstado(dto.getUsuarioId(), ESTADO_ABIERTO)
                .orElseGet(() -> crearCarrito(dto.getUsuarioId()));

        // Regla: un juego digital no se compra dos veces en el mismo carrito.
        boolean yaExiste = carrito.getItems().stream()
                .anyMatch(i -> i.getJuegoId().equals(dto.getJuegoId()));
        if (yaExiste) {
            throw new ReglaNegocioException("El juego ya se encuentra en el carrito");
        }

        ItemCarrito item = new ItemCarrito();
        item.setJuegoId(juego.getId());
        item.setTituloJuego(juego.getTitulo());
        item.setPrecioUnitario(juego.getPrecioFinal());
        carrito.agregarItem(item);

        Carrito guardado = repository.save(carrito);
        log.info("Item agregado. Carrito {} tiene {} items", guardado.getId(), guardado.getItems().size());
        return aResponseDTO(guardado);
    }

    @Transactional
    public CarritoResponseDTO eliminarItem(Long usuarioId, Long juegoId) {
        Carrito carrito = repository.findByUsuarioIdAndEstado(usuarioId, ESTADO_ABIERTO)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El usuario " + usuarioId + " no tiene un carrito abierto"));

        ItemCarrito item = carrito.getItems().stream()
                .filter(i -> i.getJuegoId().equals(juegoId))
                .findFirst()
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El juego " + juegoId + " no esta en el carrito"));

        carrito.removerItem(item);
        log.info("Juego {} eliminado del carrito {}", juegoId, carrito.getId());
        return aResponseDTO(repository.save(carrito));
    }

    @Transactional
    public CarritoResponseDTO vaciar(Long usuarioId) {
        Carrito carrito = repository.findByUsuarioIdAndEstado(usuarioId, ESTADO_ABIERTO)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El usuario " + usuarioId + " no tiene un carrito abierto"));
        carrito.getItems().clear();
        log.info("Carrito {} vaciado", carrito.getId());
        return aResponseDTO(repository.save(carrito));
    }

    /**
     * Marca el carrito como PAGADO. Lo invoca el microservicio de Pagos
     * una vez confirmada la transaccion.
     */
    @Transactional
    public CarritoResponseDTO marcarComoPagado(Long usuarioId) {
        Carrito carrito = repository.findByUsuarioIdAndEstado(usuarioId, ESTADO_ABIERTO)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El usuario " + usuarioId + " no tiene un carrito abierto"));
        if (carrito.getItems().isEmpty()) {
            throw new ReglaNegocioException("No se puede pagar un carrito vacio");
        }
        carrito.setEstado(ESTADO_PAGADO);
        log.info("Carrito {} marcado como PAGADO", carrito.getId());
        return aResponseDTO(repository.save(carrito));
    }

    // ---------- comunicacion remota ----------

    private void validarUsuario(Long usuarioId) {
        try {
            UsuarioDTO usuario = usuarioClient.obtenerPorId(usuarioId);
            if (!usuario.isActivo()) {
                throw new ReglaNegocioException("El usuario " + usuarioId + " esta inactivo");
            }
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("No existe el usuario con id " + usuarioId);
        } catch (FeignException e) {
            throw new ComunicacionException("No se pudo contactar al servicio de Usuarios: " + e.getMessage());
        }
    }

    private JuegoDTO obtenerJuego(Long juegoId) {
        try {
            return juegoClient.obtenerPorId(juegoId);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("No existe el juego con id " + juegoId);
        } catch (FeignException e) {
            throw new ComunicacionException("No se pudo contactar al servicio de Juegos: " + e.getMessage());
        }
    }

    // ---------- utilitarios ----------

    private Carrito crearCarrito(Long usuarioId) {
        Carrito carrito = new Carrito();
        carrito.setUsuarioId(usuarioId);
        carrito.setEstado(ESTADO_ABIERTO);
        carrito.setFechaCreacion(LocalDateTime.now());
        return carrito;
    }

    private CarritoResponseDTO aResponseDTO(Carrito carrito) {
        CarritoResponseDTO dto = new CarritoResponseDTO();
        dto.setId(carrito.getId());
        dto.setUsuarioId(carrito.getUsuarioId());
        dto.setEstado(carrito.getEstado());
        dto.setFechaCreacion(carrito.getFechaCreacion());

        List<ItemResponseDTO> items = carrito.getItems().stream().map(i -> {
            ItemResponseDTO ir = new ItemResponseDTO();
            ir.setId(i.getId());
            ir.setJuegoId(i.getJuegoId());
            ir.setTituloJuego(i.getTituloJuego());
            ir.setPrecioUnitario(i.getPrecioUnitario());
            return ir;
        }).collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(ItemResponseDTO::getPrecioUnitario)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        dto.setItems(items);
        dto.setCantidadItems(items.size());
        dto.setTotal(total);
        return dto;
    }
}
