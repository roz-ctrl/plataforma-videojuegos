package com.videojuegos.resenas.service;

import com.videojuegos.resenas.client.BibliotecaClient;
import com.videojuegos.resenas.client.JuegoClient;
import com.videojuegos.resenas.client.UsuarioClient;
import com.videojuegos.resenas.client.dto.JuegoDTO;
import com.videojuegos.resenas.client.dto.UsuarioDTO;
import com.videojuegos.resenas.dto.ResenaRequestDTO;
import com.videojuegos.resenas.dto.ResenaResponseDTO;
import com.videojuegos.resenas.dto.ResumenJuegoDTO;
import com.videojuegos.resenas.exception.ComunicacionException;
import com.videojuegos.resenas.exception.RecursoNoEncontradoException;
import com.videojuegos.resenas.exception.ReglaNegocioException;
import com.videojuegos.resenas.model.Resena;
import com.videojuegos.resenas.repository.ResenaRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Logica de negocio del microservicio de Resenas.
 *
 * Reglas principales:
 *  - Solo se puede resenar un juego que el usuario POSEE (se consulta a
 *    biblioteca-service via Feign). Esta es la regla central del dominio.
 *  - Un usuario solo puede dejar una resena por juego.
 *  - La calificacion debe estar entre 1 y 5 (validado en el DTO).
 */
@Service
public class ResenaService {

    private static final Logger log = LoggerFactory.getLogger(ResenaService.class);

    private final ResenaRepository repository;
    private final UsuarioClient usuarioClient;
    private final JuegoClient juegoClient;
    private final BibliotecaClient bibliotecaClient;

    public ResenaService(ResenaRepository repository, UsuarioClient usuarioClient,
                         JuegoClient juegoClient, BibliotecaClient bibliotecaClient) {
        this.repository = repository;
        this.usuarioClient = usuarioClient;
        this.juegoClient = juegoClient;
        this.bibliotecaClient = bibliotecaClient;
    }

    @Transactional(readOnly = true)
    public List<ResenaResponseDTO> listarPorJuego(Long juegoId) {
        return repository.findByJuegoId(juegoId).stream()
                .map(this::aResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ResumenJuegoDTO resumenPorJuego(Long juegoId) {
        List<Resena> resenas = repository.findByJuegoId(juegoId);
        long total = resenas.size();
        double promedio = resenas.stream().mapToInt(Resena::getCalificacion).average().orElse(0.0);
        long recomiendan = resenas.stream().filter(Resena::isRecomendado).count();
        return new ResumenJuegoDTO(juegoId, total, Math.round(promedio * 10.0) / 10.0, recomiendan);
    }

    @Transactional
    public ResenaResponseDTO crear(ResenaRequestDTO dto) {
        log.info("Creando resena: usuario={}, juego={}, calificacion={}",
                dto.getUsuarioId(), dto.getJuegoId(), dto.getCalificacion());

        validarUsuario(dto.getUsuarioId());
        obtenerJuego(dto.getJuegoId()); // valida existencia del juego

        // Regla central: solo se resena lo que se posee.
        if (!usuarioPoseeJuego(dto.getUsuarioId(), dto.getJuegoId())) {
            throw new ReglaNegocioException(
                    "No puede resenar un juego que no posee en su biblioteca");
        }

        // Regla: una sola resena por usuario y juego.
        if (repository.existsByUsuarioIdAndJuegoId(dto.getUsuarioId(), dto.getJuegoId())) {
            throw new ReglaNegocioException("El usuario ya dejo una resena para este juego");
        }

        Resena resena = new Resena();
        resena.setUsuarioId(dto.getUsuarioId());
        resena.setJuegoId(dto.getJuegoId());
        resena.setCalificacion(dto.getCalificacion());
        resena.setComentario(dto.getComentario());
        resena.setRecomendado(Boolean.TRUE.equals(dto.getRecomendado()));
        resena.setFecha(LocalDateTime.now());

        Resena guardada = repository.save(resena);
        log.info("Resena {} creada para el juego {}", guardada.getId(), dto.getJuegoId());
        return aResponseDTO(guardada);
    }

    @Transactional
    public ResenaResponseDTO actualizar(Long id, ResenaRequestDTO dto) {
        Resena resena = buscarOFallar(id);
        resena.setCalificacion(dto.getCalificacion());
        resena.setComentario(dto.getComentario());
        resena.setRecomendado(Boolean.TRUE.equals(dto.getRecomendado()));
        log.info("Resena {} actualizada", id);
        return aResponseDTO(repository.save(resena));
    }

    @Transactional
    public void eliminar(Long id) {
        Resena resena = buscarOFallar(id);
        repository.delete(resena);
        log.info("Resena {} eliminada", id);
    }

    // ---------- comunicacion remota ----------

    private void validarUsuario(Long usuarioId) {
        try {
            UsuarioDTO usuario = usuarioClient.obtenerPorId(usuarioId);
            if (!usuario.isActivo()) {
                throw new ReglaNegocioException("El usuario " + usuarioId + " esta inactivo");
            }
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("No existe el usuario con id " + usuarioId);
        } catch (FeignException e) {
            throw new ComunicacionException("No se pudo contactar al servicio de Usuarios: " + e.getMessage());
        }
    }

    private JuegoDTO obtenerJuego(Long juegoId) {
        try {
            return juegoClient.obtenerPorId(juegoId);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("No existe el juego con id " + juegoId);
        } catch (FeignException e) {
            throw new ComunicacionException("No se pudo contactar al servicio de Juegos: " + e.getMessage());
        }
    }

    private boolean usuarioPoseeJuego(Long usuarioId, Long juegoId) {
        try {
            return Boolean.TRUE.equals(bibliotecaClient.usuarioPoseeJuego(usuarioId, juegoId));
        } catch (FeignException e) {
            throw new ComunicacionException("No se pudo contactar al servicio de Biblioteca: " + e.getMessage());
        }
    }

    // ---------- utilitarios ----------

    private Resena buscarOFallar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la resena con id " + id));
    }

    private ResenaResponseDTO aResponseDTO(Resena resena) {
        ResenaResponseDTO dto = new ResenaResponseDTO();
        dto.setId(resena.getId());
        dto.setUsuarioId(resena.getUsuarioId());
        dto.setJuegoId(resena.getJuegoId());
        dto.setCalificacion(resena.getCalificacion());
        dto.setComentario(resena.getComentario());
        dto.setRecomendado(resena.isRecomendado());
        dto.setFecha(resena.getFecha());

        // Enriquecimiento remoto: nombre de usuario y titulo del juego.
        dto.setNombreUsuario(obtenerNombreUsuario(resena.getUsuarioId()));
        dto.setTituloJuego(obtenerTituloJuego(resena.getJuegoId()));
        return dto;
    }

    private String obtenerNombreUsuario(Long usuarioId) {
        try {
            return usuarioClient.obtenerPorId(usuarioId).getNombreUsuario();
        } catch (FeignException e) {
            log.warn("No se pudo obtener el usuario id={}: {}", usuarioId, e.getMessage());
            return "Usuario desconocido";
        }
    }

    private String obtenerTituloJuego(Long juegoId) {
        try {
            return juegoClient.obtenerPorId(juegoId).getTitulo();
        } catch (FeignException e) {
            log.warn("No se pudo obtener el juego id={}: {}", juegoId, e.getMessage());
            return "Juego desconocido";
        }
    }
}
