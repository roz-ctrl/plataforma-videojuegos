package com.videojuegos.logros.repository;

import com.videojuegos.logros.model.Logro;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class LogroRepositoryTest {

    @Autowired
    private LogroRepository repository;

    private Logro nuevo(Long juegoId, String nombre) {
        Logro l = new Logro();
        l.setJuegoId(juegoId);
        l.setNombre(nombre);
        l.setDescripcion("desc");
        l.setPuntos(50);
        return l;
    }

    @Test
    void deberiaGuardarYBuscarPorJuego() {
        repository.save(nuevo(2L, "Logro A"));
        repository.save(nuevo(2L, "Logro B"));
        repository.save(nuevo(3L, "Logro C"));

        assertEquals(2, repository.findByJuegoId(2L).size());
    }
}
