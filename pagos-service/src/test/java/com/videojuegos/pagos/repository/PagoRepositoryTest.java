package com.videojuegos.pagos.repository;

import com.videojuegos.pagos.model.DetallePago;
import com.videojuegos.pagos.model.Pago;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class PagoRepositoryTest {

    @Autowired
    private PagoRepository repository;

    @Test
    void deberiaGuardarPagoConDetalleYBuscarPorUsuario() {
        Pago pago = new Pago();
        pago.setUsuarioId(1L);
        pago.setMonto(new BigDecimal("19990"));
        pago.setMetodoPago("SALDO");
        pago.setEstado("COMPLETADO");
        pago.setReferencia("TX-ABC123");
        pago.setFechaPago(LocalDateTime.now());

        DetallePago detalle = new DetallePago();
        detalle.setJuegoId(1L);
        detalle.setTituloJuego("The Witcher 3");
        detalle.setPrecio(new BigDecimal("19990"));
        pago.agregarDetalle(detalle);

        repository.save(pago);

        List<Pago> pagos = repository.findByUsuarioId(1L);
        assertEquals(1, pagos.size());
        assertEquals(1, pagos.get(0).getDetalles().size());
    }
}
