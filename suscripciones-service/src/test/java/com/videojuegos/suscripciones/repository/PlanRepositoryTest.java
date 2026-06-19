package com.videojuegos.suscripciones.repository;

import com.videojuegos.suscripciones.model.Plan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class PlanRepositoryTest {

    @Autowired
    private PlanRepository repository;

    private Plan nuevo(String nombre) {
        Plan p = new Plan();
        p.setNombre(nombre);
        p.setPrecioMensual(new BigDecimal("5990"));
        p.setDuracionMeses(1);
        p.setActivo(true);
        return p;
    }

    @Test
    void deberiaGuardarYGenerarId() {
        Plan guardado = repository.save(nuevo("Pase Mensual"));
        assertNotNull(guardado.getId());
    }

    @Test
    void deberiaConfirmarQueExisteElNombre() {
        repository.save(nuevo("Pase Anual"));
        assertTrue(repository.existsByNombre("Pase Anual"));
        assertFalse(repository.existsByNombre("NoExiste"));
    }
}
