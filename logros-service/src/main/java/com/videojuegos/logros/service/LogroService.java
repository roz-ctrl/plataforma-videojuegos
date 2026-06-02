package com.videojuegos.logros.service;

import com.videojuegos.logros.client.JuegoClient;
import com.videojuegos.logros.client.UsuarioClient;
import com.videojuegos.logros.dto.DesbloquearRequestDTO;
import com.videojuegos.logros.dto.LogroDesbloqueadoResponseDTO;
import com.videojuegos.logros.dto.LogroRequestDTO;
import com.videojuegos.logros.dto.LogroResponseDTO;
import com.videojuegos.logros.exception.ComunicacionException;
import com.videojuegos.logros.exception.RecursoNoEncontradoException;
import com.videojuegos.logros.exception.ReglaNegocioException;
import com.videojuegos.logros.model.Logro;
import com.videojuegos.logros.model.LogroDesbloqueado;
import com.videojuegos.logros.repository.LogroDesbloqueadoRepository;
import com.videojuegos.logros.repository.LogroRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Logica de negocio del microservicio de Logros.
 * Maneja la definicion de logros por juego y el desbloqueo por usuario.
 */
@Service
public class LogroService {

    private static final Logger log = LoggerFactory.getLogger(LogroService.class);

    private final LogroRepository logroRepository;
    private final LogroDesbloqueadoRepository desbloqueadoRepository;
    private final UsuarioClient usuarioClient;
    private final JuegoClient juegoClient;

    public LogroService(LogroRepository logroRepository,
                        LogroDesbloqueadoRepository desbloqueadoRepository,
                        UsuarioClient usuarioClient, JuegoClient juegoClient) {
        this.logroRepository = logroRepository;
        this.desbloqueadoRepository = desbloqueadoRepository;
        this.usuarioClient = usuarioClient;
        this.juegoClient = juegoClient;
    }

    // ---------- definicion de logros ----------

    @Transactional(readOnly = true)
    public List<LogroResponseDTO> listarPorJuego(Long juegoId) {
        return logroRepository.findByJuegoId(juegoId).stream()
                .map(this::aLogroResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public LogroResponseDTO crearLogro(LogroRequestDTO dto) {
        log.info("Creando logro '{}' para el juego {}", dto.getNombre(), dto.getJuegoId());
        validarJuego(dto.getJuegoId());

        Logro logro = new Logro();
        logro.setJuegoId(dto.getJuegoId());
        logro.setNombre(dto.getNombre());
        logro.setDescripcion(dto.getDescripcion());
        logro.setPuntos(dto.getPuntos());
        return aLogroResponse(logroRepository.save(logro));
    }

    @Transactional
    public void eliminarLogro(Long id) {
        Logro logro = logroRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el logro con id " + id));
        logroRepository.delete(logro);
        log.info("Logro id={} eliminado", id);
    }

    // ---------- desbloqueo por usuario ----------

    @Transactional(readOnly = true)
    public List<LogroDesbloqueadoResponseDTO> listarDesbloqueadosPorUsuario(Long usuarioId) {
        return desbloqueadoRepository.findByUsuarioId(usuarioId).stream()
                .map(this::aDesbloqueadoResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public LogroDesbloqueadoResponseDTO desbloquear(DesbloquearRequestDTO dto) {
        log.info("Usuario {} desbloquea logro {}", dto.getUsuarioId(), dto.getLogroId());
        validarUsuario(dto.getUsuarioId());

        Logro logro = logroRepository.findById(dto.getLogroId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el logro con id " + dto.getLogroId()));

        // Regla: un logro no se puede desbloquear dos veces para el mismo usuario.
        if (desbloqueadoRepository.existsByUsuarioIdAndLogroId(dto.getUsuarioId(), dto.getLogroId())) {
            throw new ReglaNegocioException("El usuario ya desbloqueo el logro '" + logro.getNombre() + "'");
        }

        LogroDesbloqueado desbloqueado = new LogroDesbloqueado();
        desbloqueado.setUsuarioId(dto.getUsuarioId());
        desbloqueado.setLogro(logro);
        desbloqueado.setFechaDesbloqueo(LocalDateTime.now());

        LogroDesbloqueado guardado = desbloqueadoRepository.save(desbloqueado);
        log.info("Logro '{}' desbloqueado por usuario {} (+{} puntos)",
                logro.getNombre(), dto.getUsuarioId(), logro.getPuntos());
        return aDesbloqueadoResponse(guardado);
    }

    // ---------- comunicacion remota ----------

    private void validarUsuario(Long usuarioId) {
        try {
            usuarioClient.obtenerPorId(usuarioId);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("No existe el usuario con id " + usuarioId);
        } catch (FeignException e) {
            throw new ComunicacionException("No se pudo contactar al servicio de Usuarios: " + e.getMessage());
        }
    }

    private void validarJuego(Long juegoId) {
        try {
            juegoClient.obtenerPorId(juegoId);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("No existe el juego con id " + juegoId);
        } catch (FeignException e) {
            throw new ComunicacionException("No se pudo contactar al servicio de Juegos: " + e.getMessage());
        }
    }

    // ---------- utilitarios ----------

    private LogroResponseDTO aLogroResponse(Logro logro) {
        LogroResponseDTO dto = new LogroResponseDTO();
        dto.setId(logro.getId());
        dto.setJuegoId(logro.getJuegoId());
        dto.setNombre(logro.getNombre());
        dto.setDescripcion(logro.getDescripcion());
        dto.setPuntos(logro.getPuntos());
        return dto;
    }

    private LogroDesbloqueadoResponseDTO aDesbloqueadoResponse(LogroDesbloqueado d) {
        LogroDesbloqueadoResponseDTO dto = new LogroDesbloqueadoResponseDTO();
        dto.setId(d.getId());
        dto.setUsuarioId(d.getUsuarioId());
        dto.setLogroId(d.getLogro().getId());
        dto.setLogroNombre(d.getLogro().getNombre());
        dto.setPuntos(d.getLogro().getPuntos());
        dto.setFechaDesbloqueo(d.getFechaDesbloqueo());
        return dto;
    }
}
