package com.videojuegos.resenas.repository;

import com.videojuegos.resenas.model.Resena;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ResenaRepositoryTest {

    @Autowired
    private ResenaRepository repository;

    private Resena nueva(Long usuarioId, Long juegoId, int calificacion) {
        Resena r = new Resena();
        r.setUsuarioId(usuarioId);
        r.setJuegoId(juegoId);
        r.setCalificacion(calificacion);
        r.setComentario("comentario");
        r.setRecomendado(true);
        r.setFecha(LocalDateTime.now());
        return r;
    }

    @Test
    void deberiaGuardarYBuscarPorJuego() {
        repository.save(nueva(1L, 2L, 5));
        repository.save(nueva(3L, 2L, 4));

        assertEquals(2, repository.findByJuegoId(2L).size());
    }

    @Test
    void deberiaConfirmarSiUsuarioYaReseno() {
        repository.save(nueva(1L, 2L, 5));

        assertTrue(repository.existsByUsuarioIdAndJuegoId(1L, 2L));
        assertFalse(repository.existsByUsuarioIdAndJuegoId(1L, 99L));
    }
}
