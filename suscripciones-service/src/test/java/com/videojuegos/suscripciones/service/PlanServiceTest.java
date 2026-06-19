package com.videojuegos.suscripciones.service;

import com.videojuegos.suscripciones.dto.PlanRequestDTO;
import com.videojuegos.suscripciones.dto.PlanResponseDTO;
import com.videojuegos.suscripciones.exception.RecursoNoEncontradoException;
import com.videojuegos.suscripciones.exception.ReglaNegocioException;
import com.videojuegos.suscripciones.model.Plan;
import com.videojuegos.suscripciones.repository.PlanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanServiceTest {

    @Mock
    private PlanRepository repository;

    @InjectMocks
    private PlanService service;

    private Plan nuevo(Long id, String nombre) {
        Plan p = new Plan();
        p.setId(id);
        p.setNombre(nombre);
        p.setPrecioMensual(new BigDecimal("5990"));
        p.setDuracionMeses(1);
        p.setActivo(true);
        return p;
    }

    @Test
    void deberiaCrearPlanCuandoNombreEsUnico() {
        PlanRequestDTO dto = new PlanRequestDTO();
        dto.setNombre("Pase Semestral");
        dto.setPrecioMensual(new BigDecimal("4990"));
        dto.setDuracionMeses(6);
        when(repository.existsByNombre("Pase Semestral")).thenReturn(false);
        when(repository.save(any(Plan.class))).thenAnswer(inv -> {
            Plan p = inv.getArgument(0);
            p.setId(4L);
            return p;
        });

        PlanResponseDTO r = service.crear(dto);

        assertEquals(4L, r.getId());
        assertEquals("Pase Semestral", r.getNombre());
    }

    @Test
    void deberiaLanzarReglaNegocioCuandoNombreYaExiste() {
        PlanRequestDTO dto = new PlanRequestDTO();
        dto.setNombre("Pase Mensual");
        dto.setPrecioMensual(new BigDecimal("5990"));
        dto.setDuracionMeses(1);
        when(repository.existsByNombre("Pase Mensual")).thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> service.crear(dto));
        verify(repository, never()).save(any(Plan.class));
    }

    @Test
    void deberiaObtenerPlanPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(nuevo(1L, "Pase Mensual")));
        assertEquals("Pase Mensual", service.obtenerPorId(1L).getNombre());
    }

    @Test
    void deberiaLanzarRecursoNoEncontradoCuandoNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId(99L));
    }

    @Test
    void deberiaListarPlanes() {
        when(repository.findAll()).thenReturn(List.of(nuevo(1L, "Pase Mensual"), nuevo(2L, "Pase Anual")));
        assertEquals(2, service.listarTodos().size());
    }

    @Test
    void deberiaActualizarPlan() {
        Plan p = nuevo(1L, "Pase Mensual");
        PlanRequestDTO dto = new PlanRequestDTO();
        dto.setNombre("Pase Mensual Plus");
        dto.setPrecioMensual(new BigDecimal("6990"));
        dto.setDuracionMeses(1);
        when(repository.findById(1L)).thenReturn(Optional.of(p));
        when(repository.save(any(Plan.class))).thenAnswer(inv -> inv.getArgument(0));

        PlanResponseDTO r = service.actualizar(1L, dto);

        assertEquals("Pase Mensual Plus", r.getNombre());
    }
}
