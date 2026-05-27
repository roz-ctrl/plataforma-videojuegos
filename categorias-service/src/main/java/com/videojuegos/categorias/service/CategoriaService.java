package com.videojuegos.categorias.service;

import com.videojuegos.categorias.dto.CategoriaRequestDTO;
import com.videojuegos.categorias.dto.CategoriaResponseDTO;
import com.videojuegos.categorias.exception.RecursoNoEncontradoException;
import com.videojuegos.categorias.exception.ReglaNegocioException;
import com.videojuegos.categorias.model.Categoria;
import com.videojuegos.categorias.repository.CategoriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Logica de negocio del microservicio de Categorias.
 */
@Service
public class CategoriaService {

    private static final Logger log = LoggerFactory.getLogger(CategoriaService.class);

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarTodas() {
        return repository.findAll().stream().map(this::aResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoriaResponseDTO obtenerPorId(Long id) {
        return aResponseDTO(buscarOFallar(id));
    }

    @Transactional
    public CategoriaResponseDTO crear(CategoriaRequestDTO dto) {
        log.info("Creando categoria: {}", dto.getNombre());
        if (repository.existsByNombre(dto.getNombre())) {
            throw new ReglaNegocioException("Ya existe una categoria con el nombre " + dto.getNombre());
        }
        Categoria c = new Categoria();
        c.setNombre(dto.getNombre());
        c.setDescripcion(dto.getDescripcion());
        return aResponseDTO(repository.save(c));
    }

    @Transactional
    public CategoriaResponseDTO actualizar(Long id, CategoriaRequestDTO dto) {
        Categoria c = buscarOFallar(id);
        if (!c.getNombre().equals(dto.getNombre()) && repository.existsByNombre(dto.getNombre())) {
            throw new ReglaNegocioException("Ya existe una categoria con el nombre " + dto.getNombre());
        }
        c.setNombre(dto.getNombre());
        c.setDescripcion(dto.getDescripcion());
        log.info("Categoria id={} actualizada", id);
        return aResponseDTO(repository.save(c));
    }

    @Transactional
    public void eliminar(Long id) {
        Categoria c = buscarOFallar(id);
        repository.delete(c);
        log.info("Categoria id={} eliminada", id);
    }

    private Categoria buscarOFallar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la categoria con id " + id));
    }

    private CategoriaResponseDTO aResponseDTO(Categoria c) {
        CategoriaResponseDTO dto = new CategoriaResponseDTO();
        dto.setId(c.getId());
        dto.setNombre(c.getNombre());
        dto.setDescripcion(c.getDescripcion());
        return dto;
    }
}
