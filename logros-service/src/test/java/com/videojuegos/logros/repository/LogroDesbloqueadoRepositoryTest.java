package com.videojuegos.logros.repository;

import com.videojuegos.logros.model.Logro;
import com.videojuegos.logros.model.LogroDesbloqueado;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class LogroDesbloqueadoRepositoryTest {

    @Autowired
    private LogroRepository logroRepository;
    @Autowired
    private LogroDesbloqueadoRepository desbloqueadoRepository;

    @Test
    void deberiaGuardarDesbloqueoYConsultarlo() {
        Logro logro = new Logro();
        logro.setJuegoId(2L);
        logro.setNombre("Maestro Brujo");
        logro.setPuntos(100);
        logro = logroRepository.save(logro);

        LogroDesbloqueado d = new LogroDesbloqueado();
        d.setUsuarioId(1L);
        d.setLogro(logro);
        d.setFechaDesbloqueo(LocalDateTime.now());
        desbloqueadoRepository.save(d);

        assertEquals(1, desbloqueadoRepository.findByUsuarioId(1L).size());
        assertTrue(desbloqueadoRepository.existsByUsuarioIdAndLogroId(1L, logro.getId()));
    }
}
