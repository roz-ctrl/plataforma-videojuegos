package com.videojuegos.juegos.repository;

import com.videojuegos.juegos.model.Juego;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class JuegoRepositoryTest {

    @Autowired
    private JuegoRepository repository;

    private Juego nuevo(String titulo, Long categoriaId) {
        Juego j = new Juego();
        j.setTitulo(titulo);
        j.setPrecio(new BigDecimal("19990"));
        j.setDesarrolladoraId(2L);
        j.setCategoriaId(categoriaId);
        j.setDescuentoPorcentaje(0);
        j.setActivo(true);
        return j;
    }

    @Test
    void deberiaGuardarYGenerarId() {
        Juego guardado = repository.save(nuevo("The Witcher 3", 1L));
        assertNotNull(guardado.getId());
    }

    @Test
    void deberiaBuscarPorCategoria() {
        repository.save(nuevo("The Witcher 3", 1L));
        repository.save(nuevo("Hades", 1L));
        repository.save(nuevo("Mario", 3L));

        List<Juego> rpg = repository.findByCategoriaId(1L);

        assertEquals(2, rpg.size());
    }
}
