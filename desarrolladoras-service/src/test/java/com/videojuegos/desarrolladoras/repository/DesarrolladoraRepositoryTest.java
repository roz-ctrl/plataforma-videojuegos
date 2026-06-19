package com.videojuegos.desarrolladoras.repository;

import com.videojuegos.desarrolladoras.model.Desarrolladora;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class DesarrolladoraRepositoryTest {

    @Autowired
    private DesarrolladoraRepository repository;

    private Desarrolladora nueva(String nombre) {
        Desarrolladora d = new Desarrolladora();
        d.setNombre(nombre);
        d.setPaisOrigen("Chile");
        d.setActiva(true);
        return d;
    }

    @Test
    void deberiaGuardarYGenerarId() {
        Desarrolladora guardada = repository.save(nueva("Valve"));
        assertNotNull(guardada.getId());
    }

    @Test
    void deberiaConfirmarQueExisteElNombre() {
        repository.save(nueva("Nintendo"));
        assertTrue(repository.existsByNombre("Nintendo"));
        assertFalse(repository.existsByNombre("NoExiste"));
    }
}
