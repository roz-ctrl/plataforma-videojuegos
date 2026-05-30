package com.videojuegos.biblioteca.service;

import com.videojuegos.biblioteca.client.JuegoClient;
import com.videojuegos.biblioteca.client.UsuarioClient;
import com.videojuegos.biblioteca.client.dto.JuegoDTO;
import com.videojuegos.biblioteca.client.dto.UsuarioDTO;
import com.videojuegos.biblioteca.dto.AdquirirJuegoRequestDTO;
import com.videojuegos.biblioteca.dto.EntradaBibliotecaResponseDTO;
import com.videojuegos.biblioteca.exception.ComunicacionException;
import com.videojuegos.biblioteca.exception.RecursoNoEncontradoException;
import com.videojuegos.biblioteca.exception.ReglaNegocioException;
import com.videojuegos.biblioteca.model.EntradaBiblioteca;
import com.videojuegos.biblioteca.repository.BibliotecaRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Logica de negocio de la Biblioteca del usuario.
 * Regla central: un usuario no puede poseer el mismo juego dos veces.
 */
@Service
public class BibliotecaService {

    private static final Logger log = LoggerFactory.getLogger(BibliotecaService.class);

    private final BibliotecaRepository repository;
    private final UsuarioClient usuarioClient;
    private final JuegoClient juegoClient;

    public BibliotecaService(BibliotecaRepository repository, UsuarioClient usuarioClient, JuegoClient juegoClient) {
        this.repository = repository;
        this.usuarioClient = usuarioClient;
        this.juegoClient = juegoClient;
    }

    @Transactional(readOnly = true)
    public List<EntradaBibliotecaResponseDTO> listarPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId).stream()
                .map(this::aResponseDTO)
                .collect(Collectors.toList());
    }

    /** Indica si un usuario posee un juego. Usado por el servicio de Reseñas. */
    @Transactional(readOnly = true)
    public boolean usuarioPoseeJuego(Long usuarioId, Long juegoId) {
        return repository.existsByUsuarioIdAndJuegoId(usuarioId, juegoId);
    }

    @Transactional
    public EntradaBibliotecaResponseDTO adquirir(AdquirirJuegoRequestDTO dto) {
        log.info("Registrando adquisicion: usuario={}, juego={}", dto.getUsuarioId(), dto.getJuegoId());

        validarUsuario(dto.getUsuarioId());
        JuegoDTO juego = obtenerJuego(dto.getJuegoId());

        if (repository.existsByUsuarioIdAndJuegoId(dto.getUsuarioId(), dto.getJuegoId())) {
            throw new ReglaNegocioException("El usuario ya posee el juego '" + juego.getTitulo() + "'");
        }

        EntradaBiblioteca entrada = new EntradaBiblioteca();
        entrada.setUsuarioId(dto.getUsuarioId());
        entrada.setJuegoId(juego.getId());
        entrada.setTituloJuego(juego.getTitulo());
        entrada.setFechaAdquisicion(LocalDateTime.now());
        entrada.setHorasJugadas(0);
        entrada.setInstalado(false);

        EntradaBiblioteca guardada = repository.save(entrada);
        log.info("Juego '{}' agregado a la biblioteca del usuario {}", juego.getTitulo(), dto.getUsuarioId());
        return aResponseDTO(guardada);
    }

    @Transactional
    public EntradaBibliotecaResponseDTO registrarHoras(Long id, int horas) {
        if (horas < 0) {
            throw new ReglaNegocioException("Las horas jugadas no pueden ser negativas");
        }
        EntradaBiblioteca entrada = buscarOFallar(id);
        entrada.setHorasJugadas(entrada.getHorasJugadas() + horas);
        log.info("Entrada {} acumula {} horas jugadas", id, entrada.getHorasJugadas());
        return aResponseDTO(repository.save(entrada));
    }

    @Transactional
    public EntradaBibliotecaResponseDTO cambiarInstalado(Long id, boolean instalado) {
        EntradaBiblioteca entrada = buscarOFallar(id);
        entrada.setInstalado(instalado);
        log.info("Entrada {} instalado={}", id, instalado);
        return aResponseDTO(repository.save(entrada));
    }

    @Transactional
    public void eliminar(Long id) {
        EntradaBiblioteca entrada = buscarOFallar(id);
        repository.delete(entrada);
        log.info("Entrada {} eliminada de la biblioteca", id);
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

    // ---------- utilitarios ----------

    private EntradaBiblioteca buscarOFallar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la entrada de biblioteca con id " + id));
    }

    private EntradaBibliotecaResponseDTO aResponseDTO(EntradaBiblioteca e) {
        EntradaBibliotecaResponseDTO dto = new EntradaBibliotecaResponseDTO();
        dto.setId(e.getId());
        dto.setUsuarioId(e.getUsuarioId());
        dto.setJuegoId(e.getJuegoId());
        dto.setTituloJuego(e.getTituloJuego());
        dto.setFechaAdquisicion(e.getFechaAdquisicion());
        dto.setHorasJugadas(e.getHorasJugadas());
        dto.setInstalado(e.isInstalado());
        return dto;
    }
}
