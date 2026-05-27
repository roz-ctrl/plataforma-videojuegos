package com.videojuegos.desarrolladoras.service;

import com.videojuegos.desarrolladoras.dto.DesarrolladoraRequestDTO;
import com.videojuegos.desarrolladoras.dto.DesarrolladoraResponseDTO;
import com.videojuegos.desarrolladoras.exception.RecursoNoEncontradoException;
import com.videojuegos.desarrolladoras.exception.ReglaNegocioException;
import com.videojuegos.desarrolladoras.model.Desarrolladora;
import com.videojuegos.desarrolladoras.repository.DesarrolladoraRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Logica de negocio del microservicio de Desarrolladoras.
 */
@Service
public class DesarrolladoraService {

    private static final Logger log = LoggerFactory.getLogger(DesarrolladoraService.class);

    private final DesarrolladoraRepository repository;

    public DesarrolladoraService(DesarrolladoraRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<DesarrolladoraResponseDTO> listarTodas() {
        return repository.findAll().stream().map(this::aResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DesarrolladoraResponseDTO obtenerPorId(Long id) {
        return aResponseDTO(buscarOFallar(id));
    }

    @Transactional
    public DesarrolladoraResponseDTO crear(DesarrolladoraRequestDTO dto) {
        log.info("Creando desarrolladora: {}", dto.getNombre());
        if (repository.existsByNombre(dto.getNombre())) {
            throw new ReglaNegocioException("Ya existe una desarrolladora con el nombre " + dto.getNombre());
        }
        Desarrolladora d = new Desarrolladora();
        aplicar(dto, d);
        d.setActiva(true);
        return aResponseDTO(repository.save(d));
    }

    @Transactional
    public DesarrolladoraResponseDTO actualizar(Long id, DesarrolladoraRequestDTO dto) {
        Desarrolladora d = buscarOFallar(id);
        if (!d.getNombre().equals(dto.getNombre()) && repository.existsByNombre(dto.getNombre())) {
            throw new ReglaNegocioException("Ya existe una desarrolladora con el nombre " + dto.getNombre());
        }
        aplicar(dto, d);
        log.info("Desarrolladora id={} actualizada", id);
        return aResponseDTO(repository.save(d));
    }

    @Transactional
    public void eliminar(Long id) {
        Desarrolladora d = buscarOFallar(id);
        repository.delete(d);
        log.info("Desarrolladora id={} eliminada", id);
    }

    private Desarrolladora buscarOFallar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la desarrolladora con id " + id));
    }

    private void aplicar(DesarrolladoraRequestDTO dto, Desarrolladora d) {
        d.setNombre(dto.getNombre());
        d.setPaisOrigen(dto.getPaisOrigen());
        d.setSitioWeb(dto.getSitioWeb());
        d.setFechaFundacion(dto.getFechaFundacion());
    }

    private DesarrolladoraResponseDTO aResponseDTO(Desarrolladora d) {
        DesarrolladoraResponseDTO dto = new DesarrolladoraResponseDTO();
        dto.setId(d.getId());
        dto.setNombre(d.getNombre());
        dto.setPaisOrigen(d.getPaisOrigen());
        dto.setSitioWeb(d.getSitioWeb());
        dto.setFechaFundacion(d.getFechaFundacion());
        dto.setActiva(d.isActiva());
        return dto;
    }
}
