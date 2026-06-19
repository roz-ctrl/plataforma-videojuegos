package com.videojuegos.suscripciones.service;

import com.videojuegos.suscripciones.client.UsuarioClient;
import com.videojuegos.suscripciones.client.dto.UsuarioDTO;
import com.videojuegos.suscripciones.dto.SuscripcionRequestDTO;
import com.videojuegos.suscripciones.dto.SuscripcionResponseDTO;
import com.videojuegos.suscripciones.exception.RecursoNoEncontradoException;
import com.videojuegos.suscripciones.exception.ReglaNegocioException;
import com.videojuegos.suscripciones.model.Plan;
import com.videojuegos.suscripciones.model.Suscripcion;
import com.videojuegos.suscripciones.repository.PlanRepository;
import com.videojuegos.suscripciones.repository.SuscripcionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SuscripcionServiceTest {

    @Mock
    private SuscripcionRepository suscripcionRepository;
    @Mock
    private PlanRepository planRepository;
    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private SuscripcionService service;

    private UsuarioDTO usuarioActivo() {
        UsuarioDTO u = new UsuarioDTO();
        u.setId(1L);
        u.setActivo(true);
        return u;
    }

    private Plan plan() {
        Plan p = new Plan();
        p.setId(1L);
        p.setNombre("Pase Mensual");
        p.setPrecioMensual(new BigDecimal("5990"));
        p.setDuracionMeses(1);
        p.setActivo(true);
        return p;
    }

    @Test
    void deberiaSuscribirUsuarioCuandoNoTieneSuscripcionActiva() {
        SuscripcionRequestDTO dto = new SuscripcionRequestDTO();
        dto.setUsuarioId(1L);
        dto.setPlanId(1L);
        dto.setRenovacionAutomatica(true);

        when(usuarioClient.obtenerPorId(1L)).thenReturn(usuarioActivo());
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan()));
        when(suscripcionRepository.existsByUsuarioIdAndEstado(1L, "ACTIVA")).thenReturn(false);
        when(suscripcionRepository.save(any(Suscripcion.class))).thenAnswer(inv -> {
            Suscripcion s = inv.getArgument(0);
            s.setId(7L);
            return s;
        });

        SuscripcionResponseDTO r = service.suscribir(dto);

        assertEquals("ACTIVA", r.getEstado());
        assertEquals("Pase Mensual", r.getPlanNombre());
    }

    @Test
    void deberiaLanzarReglaNegocioSiYaTieneSuscripcionActiva() {
        SuscripcionRequestDTO dto = new SuscripcionRequestDTO();
        dto.setUsuarioId(1L);
        dto.setPlanId(1L);

        when(usuarioClient.obtenerPorId(1L)).thenReturn(usuarioActivo());
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan()));
        when(suscripcionRepository.existsByUsuarioIdAndEstado(1L, "ACTIVA")).thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> service.suscribir(dto));
        verify(suscripcionRepository, never()).save(any(Suscripcion.class));
    }

    @Test
    void deberiaCancelarSuscripcionActiva() {
        Suscripcion s = new Suscripcion();
        s.setId(1L);
        s.setUsuarioId(1L);
        s.setPlan(plan());
        s.setFechaInicio(LocalDate.now());
        s.setFechaFin(LocalDate.now().plusMonths(1));
        s.setEstado("ACTIVA");
        when(suscripcionRepository.findById(1L)).thenReturn(Optional.of(s));
        when(suscripcionRepository.save(any(Suscripcion.class))).thenAnswer(inv -> inv.getArgument(0));

        SuscripcionResponseDTO r = service.cancelar(1L);

        assertEquals("CANCELADA", r.getEstado());
    }

    @Test
    void deberiaObtenerSuscripcionPorId() {
        Suscripcion s = new Suscripcion();
        s.setId(1L);
        s.setUsuarioId(1L);
        s.setPlan(plan());
        s.setFechaInicio(LocalDate.now());
        s.setFechaFin(LocalDate.now().plusMonths(1));
        s.setEstado("ACTIVA");
        when(suscripcionRepository.findById(1L)).thenReturn(Optional.of(s));

        assertEquals("Pase Mensual", service.obtenerPorId(1L).getPlanNombre());
    }

    @Test
    void deberiaLanzarRecursoNoEncontradoSiNoExiste() {
        when(suscripcionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId(99L));
    }

    @Test
    void deberiaListarSuscripcionesPorUsuario() {
        Suscripcion s = new Suscripcion();
        s.setId(1L);
        s.setUsuarioId(1L);
        s.setPlan(plan());
        s.setFechaInicio(LocalDate.now());
        s.setFechaFin(LocalDate.now().plusMonths(1));
        s.setEstado("ACTIVA");
        when(suscripcionRepository.findByUsuarioId(1L)).thenReturn(java.util.List.of(s));

        assertEquals(1, service.listarPorUsuario(1L).size());
    }
}
