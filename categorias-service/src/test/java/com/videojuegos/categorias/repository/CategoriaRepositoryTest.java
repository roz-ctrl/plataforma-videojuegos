package com.videojuegos.categorias.repository;

import com.videojuegos.categorias.model.Categoria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository repository;

    private Categoria nueva(String nombre) {
        Categoria c = new Categoria();
        c.setNombre(nombre);
        c.setDescripcion("desc");
        return c;
    }

    @Test
    void deberiaGuardarYGenerarId() {
        Categoria guardada = repository.save(nueva("RPG"));
        assertNotNull(guardada.getId());
    }

    @Test
    void deberiaConfirmarQueExisteElNombre() {
        repository.save(nueva("Shooter"));
        assertTrue(repository.existsByNombre("Shooter"));
        assertFalse(repository.existsByNombre("NoExiste"));
    }
}
