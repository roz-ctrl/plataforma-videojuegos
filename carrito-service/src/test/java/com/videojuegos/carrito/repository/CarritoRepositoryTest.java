package com.videojuegos.carrito.repository;

import com.videojuegos.carrito.model.Carrito;
import com.videojuegos.carrito.model.ItemCarrito;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CarritoRepositoryTest {

    @Autowired
    private CarritoRepository repository;

    @Test
    void deberiaGuardarCarritoConItemsYBuscarloPorUsuarioYEstado() {
        Carrito c = new Carrito();
        c.setUsuarioId(1L);
        c.setEstado("ABIERTO");
        c.setFechaCreacion(LocalDateTime.now());
        ItemCarrito item = new ItemCarrito();
        item.setJuegoId(1L);
        item.setTituloJuego("The Witcher 3");
        item.setPrecioUnitario(new BigDecimal("19990"));
        c.agregarItem(item);

        repository.save(c);

        Optional<Carrito> encontrado = repository.findByUsuarioIdAndEstado(1L, "ABIERTO");
        assertTrue(encontrado.isPresent());
        assertEquals(1, encontrado.get().getItems().size());
    }
}
