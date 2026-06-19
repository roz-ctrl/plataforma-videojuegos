package com.videojuegos.carrito.service;

import com.videojuegos.carrito.client.JuegoClient;
import com.videojuegos.carrito.client.UsuarioClient;
import com.videojuegos.carrito.client.dto.JuegoDTO;
import com.videojuegos.carrito.client.dto.UsuarioDTO;
import com.videojuegos.carrito.dto.AgregarItemRequestDTO;
import com.videojuegos.carrito.dto.CarritoResponseDTO;
import com.videojuegos.carrito.exception.RecursoNoEncontradoException;
import com.videojuegos.carrito.exception.ReglaNegocioException;
import com.videojuegos.carrito.model.Carrito;
import com.videojuegos.carrito.model.ItemCarrito;
import com.videojuegos.carrito.repository.CarritoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de CarritoService.
 * Se mockean el repositorio y los clientes Feign (UsuarioClient, JuegoClient).
 */
@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock
    private CarritoRepository repository;
    @Mock
    private UsuarioClient usuarioClient;
    @Mock
    private JuegoClient juegoClient;

    @InjectMocks
    private CarritoService service;

    private UsuarioDTO usuarioActivo() {
        UsuarioDTO u = new UsuarioDTO();
        u.setId(1L);
        u.setNombreUsuario("gabe");
        u.setActivo(true);
        return u;
    }

    private JuegoDTO juegoActivo() {
        JuegoDTO j = new JuegoDTO();
        j.setId(1L);
        j.setTitulo("The Witcher 3");
        j.setPrecioFinal(new BigDecimal("19990"));
        j.setActivo(true);
        return j;
    }

    private Carrito carritoConItem() {
        Carrito c = new Carrito();
        c.setUsuarioId(1L);
        c.setEstado("ABIERTO");
        c.setFechaCreacion(LocalDateTime.now());
        ItemCarrito item = new ItemCarrito();
        item.setJuegoId(1L);
        item.setTituloJuego("The Witcher 3");
        item.setPrecioUnitario(new BigDecimal("19990"));
        c.agregarItem(item);
        return c;
    }

    @Test
    void deberiaAgregarItemAlCarrito() {
        AgregarItemRequestDTO dto = new AgregarItemRequestDTO();
        dto.setUsuarioId(1L);
        dto.setJuegoId(1L);

        when(usuarioClient.obtenerPorId(1L)).thenReturn(usuarioActivo());
        when(juegoClient.obtenerPorId(1L)).thenReturn(juegoActivo());
        when(repository.findByUsuarioIdAndEstado(1L, "ABIERTO")).thenReturn(Optional.empty());
        when(repository.save(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        CarritoResponseDTO r = service.agregarItem(dto);

        assertEquals(1, r.getCantidadItems());
        assertEquals(0, new BigDecimal("19990").compareTo(r.getTotal()));
    }

    @Test
    void deberiaLanzarReglaNegocioSiElJuegoYaEstaEnElCarrito() {
        AgregarItemRequestDTO dto = new AgregarItemRequestDTO();
        dto.setUsuarioId(1L);
        dto.setJuegoId(1L);

        when(usuarioClient.obtenerPorId(1L)).thenReturn(usuarioActivo());
        when(juegoClient.obtenerPorId(1L)).thenReturn(juegoActivo());
        when(repository.findByUsuarioIdAndEstado(1L, "ABIERTO")).thenReturn(Optional.of(carritoConItem()));

        assertThrows(ReglaNegocioException.class, () -> service.agregarItem(dto));
        verify(repository, never()).save(any(Carrito.class));
    }

    @Test
    void deberiaVerCarritoDelUsuario() {
        when(repository.findByUsuarioIdAndEstado(1L, "ABIERTO")).thenReturn(Optional.of(carritoConItem()));

        CarritoResponseDTO r = service.verCarrito(1L);

        assertEquals(1L, r.getUsuarioId());
        assertEquals(1, r.getCantidadItems());
    }

    @Test
    void deberiaLanzarRecursoNoEncontradoSiNoHayCarritoAbierto() {
        when(repository.findByUsuarioIdAndEstado(1L, "ABIERTO")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.verCarrito(1L));
    }

    @Test
    void deberiaLanzarReglaNegocioAlPagarCarritoVacio() {
        Carrito vacio = new Carrito();
        vacio.setUsuarioId(1L);
        vacio.setEstado("ABIERTO");
        vacio.setFechaCreacion(LocalDateTime.now());
        when(repository.findByUsuarioIdAndEstado(1L, "ABIERTO")).thenReturn(Optional.of(vacio));

        assertThrows(ReglaNegocioException.class, () -> service.marcarComoPagado(1L));
    }

    @Test
    void deberiaEliminarItemDelCarrito() {
        when(repository.findByUsuarioIdAndEstado(1L, "ABIERTO")).thenReturn(Optional.of(carritoConItem()));
        when(repository.save(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        CarritoResponseDTO r = service.eliminarItem(1L, 1L);

        assertEquals(0, r.getCantidadItems());
    }

    @Test
    void deberiaVaciarElCarrito() {
        when(repository.findByUsuarioIdAndEstado(1L, "ABIERTO")).thenReturn(Optional.of(carritoConItem()));
        when(repository.save(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        CarritoResponseDTO r = service.vaciar(1L);

        assertEquals(0, r.getCantidadItems());
    }

    @Test
    void deberiaMarcarCarritoComoPagado() {
        when(repository.findByUsuarioIdAndEstado(1L, "ABIERTO")).thenReturn(Optional.of(carritoConItem()));
        when(repository.save(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        CarritoResponseDTO r = service.marcarComoPagado(1L);

        assertEquals("PAGADO", r.getEstado());
    }
}
