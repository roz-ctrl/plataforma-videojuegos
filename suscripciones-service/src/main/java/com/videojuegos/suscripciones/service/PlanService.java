package com.videojuegos.suscripciones.service;

import com.videojuegos.suscripciones.dto.PlanRequestDTO;
import com.videojuegos.suscripciones.dto.PlanResponseDTO;
import com.videojuegos.suscripciones.exception.RecursoNoEncontradoException;
import com.videojuegos.suscripciones.exception.ReglaNegocioException;
import com.videojuegos.suscripciones.model.Plan;
import com.videojuegos.suscripciones.repository.PlanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/** Logica de negocio del catalogo de Planes de suscripcion. */
@Service
public class PlanService {

    private static final Logger log = LoggerFactory.getLogger(PlanService.class);

    private final PlanRepository repository;

    public PlanService(PlanRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<PlanResponseDTO> listarTodos() {
        return repository.findAll().stream().map(this::aResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PlanResponseDTO obtenerPorId(Long id) {
        return aResponseDTO(buscarOFallar(id));
    }

    @Transactional
    public PlanResponseDTO crear(PlanRequestDTO dto) {
        log.info("Creando plan: {}", dto.getNombre());
        if (repository.existsByNombre(dto.getNombre())) {
            throw new ReglaNegocioException("Ya existe un plan con el nombre " + dto.getNombre());
        }
        Plan plan = new Plan();
        plan.setNombre(dto.getNombre());
        plan.setPrecioMensual(dto.getPrecioMensual());
        plan.setDuracionMeses(dto.getDuracionMeses());
        plan.setDescripcion(dto.getDescripcion());
        plan.setActivo(true);
        return aResponseDTO(repository.save(plan));
    }

    @Transactional
    public PlanResponseDTO actualizar(Long id, PlanRequestDTO dto) {
        Plan plan = buscarOFallar(id);
        plan.setNombre(dto.getNombre());
        plan.setPrecioMensual(dto.getPrecioMensual());
        plan.setDuracionMeses(dto.getDuracionMeses());
        plan.setDescripcion(dto.getDescripcion());
        log.info("Plan id={} actualizado", id);
        return aResponseDTO(repository.save(plan));
    }

    @Transactional
    public void eliminar(Long id) {
        Plan plan = buscarOFallar(id);
        repository.delete(plan);
        log.info("Plan id={} eliminado", id);
    }

    private Plan buscarOFallar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el plan con id " + id));
    }

    private PlanResponseDTO aResponseDTO(Plan plan) {
        PlanResponseDTO dto = new PlanResponseDTO();
        dto.setId(plan.getId());
        dto.setNombre(plan.getNombre());
        dto.setPrecioMensual(plan.getPrecioMensual());
        dto.setDuracionMeses(plan.getDuracionMeses());
        dto.setDescripcion(plan.getDescripcion());
        dto.setActivo(plan.isActivo());
        return dto;
    }
}
