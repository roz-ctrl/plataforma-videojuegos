package com.videojuegos.juegos.service;

import com.videojuegos.juegos.client.CategoriaClient;
import com.videojuegos.juegos.client.DesarrolladoraClient;
import com.videojuegos.juegos.client.dto.CategoriaDTO;
import com.videojuegos.juegos.client.dto.DesarrolladoraDTO;
import com.videojuegos.juegos.dto.JuegoRequestDTO;
import com.videojuegos.juegos.dto.JuegoResponseDTO;
import com.videojuegos.juegos.exception.ComunicacionException;
import com.videojuegos.juegos.exception.RecursoNoEncontradoException;
import com.videojuegos.juegos.exception.ReglaNegocioException;
import com.videojuegos.juegos.model.Juego;
import com.videojuegos.juegos.repository.JuegoRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Logica de negocio del microservicio de Juegos.
 * Valida contra los microservicios de Desarrolladoras y Categorias (Feign)
 * y enriquece la respuesta con los nombres remotos y el precio con descuento.
 */
@Service
public class JuegoService {

    private static final Logger log = LoggerFactory.getLogger(JuegoService.class);

    private final JuegoRepository repository;
    private final DesarrolladoraClient desarrolladoraClient;
    private final CategoriaClient categoriaClient;

    public JuegoService(JuegoRepository repository,
                        DesarrolladoraClient desarrolladoraClient,
                        CategoriaClient categoriaClient) {
        this.repository = repository;
        this.desarrolladoraClient = desarrolladoraClient;
        this.categoriaClient = categoriaClient;
    }

    @Transactional(readOnly = true)
    public List<JuegoResponseDTO> listarTodos() {
        return repository.findAll().stream().map(this::aResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public JuegoResponseDTO obtenerPorId(Long id) {
        return aResponseDTO(buscarOFallar(id));
    }

    @Transactional
    public JuegoResponseDTO crear(JuegoRequestDTO dto) {
        log.info("Creando juego '{}' (desarrolladora={}, categoria={})",
                dto.getTitulo(), dto.getDesarrolladoraId(), dto.getCategoriaId());

        // Regla de negocio: la desarrolladora y la categoria deben existir realmente
        // en sus microservicios antes de poder publicar el juego.
        validarDesarrolladora(dto.getDesarrolladoraId());
        validarCategoria(dto.getCategoriaId());

        Juego juego = new Juego();
        aplicar(dto, juego);
        juego.setActivo(true);
        Juego guardado = repository.save(juego);
        log.info("Juego creado con id={}", guardado.getId());
        return aResponseDTO(guardado);
    }

    @Transactional
    public JuegoResponseDTO actualizar(Long id, JuegoRequestDTO dto) {
        Juego juego = buscarOFallar(id);
        validarDesarrolladora(dto.getDesarrolladoraId());
        validarCategoria(dto.getCategoriaId());
        aplicar(dto, juego);
        log.info("Juego id={} actualizado", id);
        return aResponseDTO(repository.save(juego));
    }

    @Transactional
    public void eliminar(Long id) {
        Juego juego = buscarOFallar(id);
        repository.delete(juego);
        log.info("Juego id={} eliminado", id);
    }

    // ---------- comunicacion remota (Feign) ----------

    private void validarDesarrolladora(Long desarrolladoraId) {
        try {
            DesarrolladoraDTO d = desarrolladoraClient.obtenerPorId(desarrolladoraId);
            if (!d.isActiva()) {
                throw new ReglaNegocioException("La desarrolladora id=" + desarrolladoraId + " esta inactiva");
            }
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("No existe la desarrolladora con id " + desarrolladoraId);
        } catch (FeignException e) {
            throw new ComunicacionException("No se pudo contactar al servicio de Desarrolladoras: " + e.getMessage());
        }
    }

    private void validarCategoria(Long categoriaId) {
        try {
            categoriaClient.obtenerPorId(categoriaId);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("No existe la categoria con id " + categoriaId);
        } catch (FeignException e) {
            throw new ComunicacionException("No se pudo contactar al servicio de Categorias: " + e.getMessage());
        }
    }

    // ---------- utilitarios privados ----------

    private Juego buscarOFallar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el juego con id " + id));
    }

    private void aplicar(JuegoRequestDTO dto, Juego juego) {
        juego.setTitulo(dto.getTitulo());
        juego.setDescripcion(dto.getDescripcion());
        juego.setPrecio(dto.getPrecio());
        juego.setFechaLanzamiento(dto.getFechaLanzamiento());
        juego.setDesarrolladoraId(dto.getDesarrolladoraId());
        juego.setCategoriaId(dto.getCategoriaId());
        juego.setPlataforma(dto.getPlataforma());
        juego.setRequisitosMinimos(dto.getRequisitosMinimos());
        juego.setRequisitosRecomendados(dto.getRequisitosRecomendados());
        juego.setDescuentoPorcentaje(dto.getDescuentoPorcentaje() == null ? 0 : dto.getDescuentoPorcentaje());
    }

    private JuegoResponseDTO aResponseDTO(Juego juego) {
        JuegoResponseDTO dto = new JuegoResponseDTO();
        dto.setId(juego.getId());
        dto.setTitulo(juego.getTitulo());
        dto.setDescripcion(juego.getDescripcion());
        dto.setPrecio(juego.getPrecio());
        dto.setDescuentoPorcentaje(juego.getDescuentoPorcentaje());
        dto.setPrecioFinal(calcularPrecioFinal(juego));
        dto.setFechaLanzamiento(juego.getFechaLanzamiento());
        dto.setDesarrolladoraId(juego.getDesarrolladoraId());
        dto.setCategoriaId(juego.getCategoriaId());
        dto.setPlataforma(juego.getPlataforma());
        dto.setRequisitosMinimos(juego.getRequisitosMinimos());
        dto.setRequisitosRecomendados(juego.getRequisitosRecomendados());
        dto.setActivo(juego.isActivo());

        // Enriquecimiento remoto. Si el servicio remoto falla, no rompemos el
        // listado: dejamos el nombre como "No disponible" y registramos el error.
        dto.setDesarrolladoraNombre(obtenerNombreDesarrolladora(juego.getDesarrolladoraId()));
        dto.setCategoriaNombre(obtenerNombreCategoria(juego.getCategoriaId()));
        return dto;
    }

    private BigDecimal calcularPrecioFinal(Juego juego) {
        int descuento = juego.getDescuentoPorcentaje() == null ? 0 : juego.getDescuentoPorcentaje();
        BigDecimal factor = BigDecimal.valueOf(100 - descuento).divide(BigDecimal.valueOf(100));
        return juego.getPrecio().multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }

    private String obtenerNombreDesarrolladora(Long id) {
        try {
            return desarrolladoraClient.obtenerPorId(id).getNombre();
        } catch (FeignException e) {
            log.warn("No se pudo obtener la desarrolladora id={}: {}", id, e.getMessage());
            return "No disponible";
        }
    }

    private String obtenerNombreCategoria(Long id) {
        try {
            return categoriaClient.obtenerPorId(id).getNombre();
        } catch (FeignException e) {
            log.warn("No se pudo obtener la categoria id={}: {}", id, e.getMessage());
            return "No disponible";
        }
    }
}
