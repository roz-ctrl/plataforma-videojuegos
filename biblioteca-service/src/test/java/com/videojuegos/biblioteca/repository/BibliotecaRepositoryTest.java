package com.videojuegos.biblioteca.repository;

import com.videojuegos.biblioteca.model.EntradaBiblioteca;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class BibliotecaRepositoryTest {

    @Autowired
    private BibliotecaRepository repository;

    private EntradaBiblioteca entrada(Long usuarioId, Long juegoId) {
        EntradaBiblioteca e = new EntradaBiblioteca();
        e.setUsuarioId(usuarioId);
        e.setJuegoId(juegoId);
        e.setTituloJuego("Hades");
        e.setFechaAdquisicion(LocalDateTime.now());
        e.setHorasJugadas(0);
        e.setInstalado(false);
        return e;
    }

    @Test
    void deberiaGuardarYListarPorUsuario() {
        repository.save(entrada(1L, 2L));
        repository.save(entrada(1L, 3L));

        assertEquals(2, repository.findByUsuarioId(1L).size());
    }

    @Test
    void deberiaConfirmarPosesionDeJuego() {
        repository.save(entrada(1L, 2L));

        assertTrue(repository.existsByUsuarioIdAndJuegoId(1L, 2L));
        assertFalse(repository.existsByUsuarioIdAndJuegoId(1L, 99L));
    }
}
