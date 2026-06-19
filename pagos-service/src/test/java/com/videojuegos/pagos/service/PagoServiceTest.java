package com.videojuegos.pagos.service;

import com.videojuegos.pagos.client.BibliotecaClient;
import com.videojuegos.pagos.client.CarritoClient;
import com.videojuegos.pagos.client.UsuarioClient;
import com.videojuegos.pagos.client.dto.CarritoDTO;
import com.videojuegos.pagos.client.dto.ItemCarritoDTO;
import com.videojuegos.pagos.dto.PagoResponseDTO;
import com.videojuegos.pagos.dto.ProcesarPagoRequestDTO;
import com.videojuegos.pagos.exception.RecursoNoEncontradoException;
import com.videojuegos.pagos.exception.ReglaNegocioException;
import com.videojuegos.pagos.model.Pago;
import com.videojuegos.pagos.repository.PagoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de PagoService (el orquestador de la compra).
 * Se mockean el repositorio y los 3 clientes Feign (Carrito, Usuario, Biblioteca).
 */
@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository repository;
    @Mock
    private CarritoClient carritoClient;
    @Mock
    private UsuarioClient usuarioClient;
    @Mock
    private BibliotecaClient bibliotecaClient;

    @InjectMocks
    private PagoService service;

    private CarritoDTO carritoConUnJuego() {
        ItemCarritoDTO item = new ItemCarritoDTO();
        item.setJuegoId(1L);
        item.setTituloJuego("The Witcher 3");
        item.setPrecioUnitario(new BigDecimal("19990"));

        CarritoDTO c = new CarritoDTO();
        c.setUsuarioId(1L);
        c.setEstado("ABIERTO");
        c.setTotal(new BigDecimal("19990"));
        c.setItems(List.of(item));
        return c;
    }

    @Test
    void deberiaProcesarPagoConSaldoCorrectamente() {
        ProcesarPagoRequestDTO dto = new ProcesarPagoRequestDTO();
        dto.setUsuarioId(1L);
        dto.setMetodoPago("SALDO");

        when(carritoClient.obtenerCarrito(1L)).thenReturn(carritoConUnJuego());
        when(repository.save(any(Pago.class))).thenAnswer(inv -> {
            Pago p = inv.getArgument(0);
            p.setId(100L);
            return p;
        });

        PagoResponseDTO r = service.procesarPago(dto);

        assertEquals("COMPLETADO", r.getEstado());
        assertEquals(0, new BigDecimal("19990").compareTo(r.getMonto()));
        assertEquals(1, r.getDetalles().size());
        // verifica la orquestacion: descuenta saldo, registra en biblioteca y cierra el carrito
        verify(usuarioClient).debitarSaldo(eq(1L), any(BigDecimal.class));
        verify(bibliotecaClient).adquirir(any());
        verify(carritoClient).marcarComoPagado(1L);
    }

    @Test
    void deberiaLanzarReglaNegocioCuandoElCarritoEstaVacio() {
        ProcesarPagoRequestDTO dto = new ProcesarPagoRequestDTO();
        dto.setUsuarioId(1L);
        dto.setMetodoPago("SALDO");

        CarritoDTO vacio = new CarritoDTO();
        vacio.setUsuarioId(1L);
        vacio.setItems(List.of());
        when(carritoClient.obtenerCarrito(1L)).thenReturn(vacio);

        assertThrows(ReglaNegocioException.class, () -> service.procesarPago(dto));
        verify(repository, never()).save(any(Pago.class));
    }

    @Test
    void deberiaLanzarRecursoNoEncontradoCuandoElPagoNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId(99L));
    }

    @Test
    void deberiaListarPagosDelUsuario() {
        Pago pago = new Pago();
        pago.setId(1L);
        pago.setUsuarioId(1L);
        pago.setMonto(new BigDecimal("19990"));
        pago.setMetodoPago("SALDO");
        pago.setEstado("COMPLETADO");
        pago.setReferencia("TX-ABC");
        when(repository.findByUsuarioId(1L)).thenReturn(List.of(pago));

        assertEquals(1, service.listarPorUsuario(1L).size());
    }

    @Test
    void deberiaObtenerPagoPorId() {
        Pago pago = new Pago();
        pago.setId(5L);
        pago.setUsuarioId(1L);
        pago.setMonto(new BigDecimal("19990"));
        pago.setMetodoPago("SALDO");
        pago.setEstado("COMPLETADO");
        pago.setReferencia("TX-XYZ");
        when(repository.findById(5L)).thenReturn(Optional.of(pago));

        assertEquals(5L, service.obtenerPorId(5L).getId());
    }

    @Test
    void deberiaProcesarPagoConTarjetaSinDebitarSaldo() {
        ProcesarPagoRequestDTO dto = new ProcesarPagoRequestDTO();
        dto.setUsuarioId(1L);
        dto.setMetodoPago("TARJETA");
        when(carritoClient.obtenerCarrito(1L)).thenReturn(carritoConUnJuego());
        when(repository.save(any(Pago.class))).thenAnswer(inv -> {
            Pago p = inv.getArgument(0);
            p.setId(101L);
            return p;
        });

        PagoResponseDTO r = service.procesarPago(dto);

        assertEquals("COMPLETADO", r.getEstado());
        // Con TARJETA NO se debita el saldo del usuario
        verify(usuarioClient, never()).debitarSaldo(any(), any());
        verify(carritoClient).marcarComoPagado(1L);
    }
}
