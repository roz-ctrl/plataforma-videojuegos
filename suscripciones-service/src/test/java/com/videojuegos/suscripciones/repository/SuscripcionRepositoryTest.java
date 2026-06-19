package com.videojuegos.suscripciones.repository;

import com.videojuegos.suscripciones.model.Plan;
import com.videojuegos.suscripciones.model.Suscripcion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class SuscripcionRepositoryTest {

    @Autowired
    private SuscripcionRepository suscripcionRepository;
    @Autowired
    private PlanRepository planRepository;

    @Test
    void deberiaGuardarSuscripcionYBuscarPorUsuario() {
        Plan plan = new Plan();
        plan.setNombre("Pase Mensual");
        plan.setPrecioMensual(new BigDecimal("5990"));
        plan.setDuracionMeses(1);
        plan.setActivo(true);
        plan = planRepository.save(plan);

        Suscripcion s = new Suscripcion();
        s.setUsuarioId(1L);
        s.setPlan(plan);
        s.setFechaInicio(LocalDate.now());
        s.setFechaFin(LocalDate.now().plusMonths(1));
        s.setEstado("ACTIVA");
        s.setRenovacionAutomatica(true);
        suscripcionRepository.save(s);

        assertEquals(1, suscripcionRepository.findByUsuarioId(1L).size());
        assertTrue(suscripcionRepository.existsByUsuarioIdAndEstado(1L, "ACTIVA"));
    }
}
