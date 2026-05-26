package com.videojuegos.usuarios.service;

import com.videojuegos.usuarios.dto.UsuarioRequestDTO;
import com.videojuegos.usuarios.dto.UsuarioResponseDTO;
import com.videojuegos.usuarios.exception.RecursoNoEncontradoException;
import com.videojuegos.usuarios.exception.ReglaNegocioException;
import com.videojuegos.usuarios.model.Usuario;
import com.videojuegos.usuarios.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Capa de logica de negocio del microservicio de Usuarios.
 * Aqui se concentran las reglas del dominio; el controller solo orquesta.
 */
@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        log.debug("Listando todos los usuarios");
        return usuarioRepository.findAll().stream()
                .map(this::aResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPorId(Long id) {
        Usuario usuario = buscarOFallar(id);
        return aResponseDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO crear(UsuarioRequestDTO dto) {
        log.info("Registrando nuevo usuario: {}", dto.getNombreUsuario());

        // Regla de negocio: el email y el nombre de usuario deben ser unicos.
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ReglaNegocioException("Ya existe un usuario con el email " + dto.getEmail());
        }
        if (usuarioRepository.existsByNombreUsuario(dto.getNombreUsuario())) {
            throw new ReglaNegocioException("El nombre de usuario '" + dto.getNombreUsuario() + "' ya esta en uso");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(dto.getNombreUsuario());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(dto.getPassword());
        usuario.setPais(dto.getPais());
        usuario.setSaldo(BigDecimal.ZERO);
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setActivo(true);

        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario registrado con id={}", guardado.getId());
        return aResponseDTO(guardado);
    }

    @Transactional
    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = buscarOFallar(id);

        // Si cambia el email, validar que no choque con otro usuario.
        if (!usuario.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ReglaNegocioException("Ya existe un usuario con el email " + dto.getEmail());
        }

        usuario.setNombreUsuario(dto.getNombreUsuario());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(dto.getPassword());
        usuario.setPais(dto.getPais());

        Usuario actualizado = usuarioRepository.save(usuario);
        log.info("Usuario id={} actualizado", id);
        return aResponseDTO(actualizado);
    }

    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = buscarOFallar(id);
        usuarioRepository.delete(usuario);
        log.info("Usuario id={} eliminado", id);
    }

    /**
     * Recarga saldo en la billetera del usuario.
     * Regla: el monto a recargar debe ser positivo.
     */
    @Transactional
    public UsuarioResponseDTO recargarSaldo(Long id, BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaNegocioException("El monto a recargar debe ser mayor que cero");
        }
        Usuario usuario = buscarOFallar(id);
        usuario.setSaldo(usuario.getSaldo().add(monto));
        log.info("Usuario id={} recargo {}. Nuevo saldo={}", id, monto, usuario.getSaldo());
        return aResponseDTO(usuarioRepository.save(usuario));
    }

    /**
     * Descuenta saldo de la billetera (consumido por el microservicio de Pagos).
     * Regla: el usuario debe tener saldo suficiente.
     */
    @Transactional
    public UsuarioResponseDTO debitarSaldo(Long id, BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaNegocioException("El monto a debitar debe ser mayor que cero");
        }
        Usuario usuario = buscarOFallar(id);
        if (usuario.getSaldo().compareTo(monto) < 0) {
            throw new ReglaNegocioException("Saldo insuficiente. Saldo actual: " + usuario.getSaldo());
        }
        usuario.setSaldo(usuario.getSaldo().subtract(monto));
        log.info("Usuario id={} debito {}. Nuevo saldo={}", id, monto, usuario.getSaldo());
        return aResponseDTO(usuarioRepository.save(usuario));
    }

    // ---------- utilitarios privados ----------

    private Usuario buscarOFallar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario con id " + id));
    }

    private UsuarioResponseDTO aResponseDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNombreUsuario(usuario.getNombreUsuario());
        dto.setEmail(usuario.getEmail());
        dto.setPais(usuario.getPais());
        dto.setSaldo(usuario.getSaldo());
        dto.setFechaRegistro(usuario.getFechaRegistro());
        dto.setActivo(usuario.isActivo());
        return dto;
    }
}
