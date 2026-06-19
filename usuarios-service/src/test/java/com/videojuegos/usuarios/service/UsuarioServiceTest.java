package com.videojuegos.usuarios.service;

import com.videojuegos.usuarios.dto.UsuarioRequestDTO;
import com.videojuegos.usuarios.dto.UsuarioResponseDTO;
import com.videojuegos.usuarios.exception.RecursoNoEncontradoException;
import com.videojuegos.usuarios.exception.ReglaNegocioException;
import com.videojuegos.usuarios.model.Usuario;
import com.videojuegos.usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de UsuarioService.
 * Se simula (mock) el UsuarioRepository, por lo que NO se conecta a MySQL.
 * Se usa el patron AAA (Arrange - Act - Assert).
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    // ---------- utilitario para construir un usuario de prueba ----------
    private Usuario nuevoUsuario(Long id, String nombre, String email, BigDecimal saldo) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setNombreUsuario(nombre);
        u.setEmail(email);
        u.setPassword("pass1234");
        u.setPais("Chile");
        u.setSaldo(saldo);
        u.setFechaRegistro(LocalDateTime.now());
        u.setActivo(true);
        return u;
    }

    @Test
    void deberiaCrearUsuarioCuandoEmailYNombreSonUnicos() {
        // Arrange
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setNombreUsuario("nuevo_jugador");
        dto.setEmail("nuevo@steamclone.com");
        dto.setPassword("clave123");
        dto.setPais("Chile");

        when(usuarioRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByNombreUsuario(dto.getNombreUsuario())).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(10L);
            return u;
        });

        // Act
        UsuarioResponseDTO resultado = usuarioService.crear(dto);

        // Assert
        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals("nuevo@steamclone.com", resultado.getEmail());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void deberiaLanzarReglaNegocioCuandoEmailYaExiste() {
        // Arrange
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setNombreUsuario("otro");
        dto.setEmail("repetido@steamclone.com");
        dto.setPassword("clave123");
        when(usuarioRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        // Act + Assert
        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class,
                () -> usuarioService.crear(dto));
        assertTrue(ex.getMessage().contains("email"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void deberiaRetornarUsuarioCuandoExiste() {
        // Arrange
        Usuario usuario = nuevoUsuario(1L, "gabe", "gabe@steamclone.com", new BigDecimal("5000"));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act
        UsuarioResponseDTO resultado = usuarioService.obtenerPorId(1L);

        // Assert
        assertEquals(1L, resultado.getId());
        assertEquals("gabe", resultado.getNombreUsuario());
        verify(usuarioRepository).findById(1L);
    }

    @Test
    void deberiaLanzarRecursoNoEncontradoCuandoUsuarioNoExiste() {
        // Arrange
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RecursoNoEncontradoException.class, () -> usuarioService.obtenerPorId(99L));
        verify(usuarioRepository).findById(99L);
    }

    @Test
    void deberiaListarTodosLosUsuarios() {
        // Arrange
        when(usuarioRepository.findAll()).thenReturn(List.of(
                nuevoUsuario(1L, "gabe", "gabe@steamclone.com", BigDecimal.ZERO),
                nuevoUsuario(2L, "shadow", "shadow@steamclone.com", BigDecimal.ZERO)
        ));

        // Act
        List<UsuarioResponseDTO> resultado = usuarioService.listarTodos();

        // Assert
        assertEquals(2, resultado.size());
        verify(usuarioRepository).findAll();
    }

    @Test
    void deberiaRecargarSaldoCorrectamente() {
        // Arrange
        Usuario usuario = nuevoUsuario(1L, "gabe", "gabe@steamclone.com", new BigDecimal("1000"));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        UsuarioResponseDTO resultado = usuarioService.recargarSaldo(1L, new BigDecimal("5000"));

        // Assert
        assertEquals(new BigDecimal("6000"), resultado.getSaldo());
    }

    @Test
    void deberiaLanzarReglaNegocioCuandoSaldoEsInsuficiente() {
        // Arrange
        Usuario usuario = nuevoUsuario(1L, "gabe", "gabe@steamclone.com", new BigDecimal("1000"));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act + Assert: intenta debitar mas de lo que tiene
        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class,
                () -> usuarioService.debitarSaldo(1L, new BigDecimal("5000")));
        assertTrue(ex.getMessage().toLowerCase().contains("saldo"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void deberiaEliminarUsuarioExistente() {
        // Arrange
        Usuario usuario = nuevoUsuario(1L, "gabe", "gabe@steamclone.com", BigDecimal.ZERO);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act
        usuarioService.eliminar(1L);

        // Assert
        verify(usuarioRepository).delete(usuario);
    }

    @Test
    void deberiaActualizarUsuario() {
        Usuario usuario = nuevoUsuario(1L, "gabe", "gabe@steamclone.com", BigDecimal.ZERO);
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setNombreUsuario("gabe2");
        dto.setEmail("gabe@steamclone.com"); // mismo email: no revisa unicidad
        dto.setPassword("nueva123");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResponseDTO r = usuarioService.actualizar(1L, dto);

        assertEquals("gabe2", r.getNombreUsuario());
    }

    @Test
    void deberiaDebitarSaldoCorrectamente() {
        Usuario usuario = nuevoUsuario(1L, "gabe", "gabe@steamclone.com", new BigDecimal("5000"));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResponseDTO r = usuarioService.debitarSaldo(1L, new BigDecimal("2000"));

        assertEquals(0, new BigDecimal("3000").compareTo(r.getSaldo()));
    }

    @Test
    void deberiaLanzarReglaNegocioAlRecargarMontoNegativo() {
        assertThrows(ReglaNegocioException.class,
                () -> usuarioService.recargarSaldo(1L, new BigDecimal("-100")));
    }
}
