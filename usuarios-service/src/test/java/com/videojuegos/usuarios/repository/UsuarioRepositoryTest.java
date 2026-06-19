package com.videojuegos.usuarios.repository;

import com.videojuegos.usuarios.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del repositorio UsuarioRepository.
 * @DataJpaTest levanta solo la capa JPA y usa H2 en memoria (perfil "test").
 * Aqui SI se prueba la persistencia real (insertar, buscar), pero sin MySQL.
 */
@DataJpaTest
@ActiveProfiles("test")
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository repository;

    private Usuario nuevoUsuario(String nombre, String email) {
        Usuario u = new Usuario();
        u.setNombreUsuario(nombre);
        u.setEmail(email);
        u.setPassword("pass1234");
        u.setPais("Chile");
        u.setSaldo(new BigDecimal("1000"));
        u.setFechaRegistro(LocalDateTime.now());
        u.setActivo(true);
        return u;
    }

    @Test
    void deberiaGuardarUsuarioYGenerarId() {
        Usuario guardado = repository.save(nuevoUsuario("gabe", "gabe@test.com"));

        assertNotNull(guardado.getId());
        assertEquals("gabe", guardado.getNombreUsuario());
    }

    @Test
    void deberiaEncontrarUsuarioPorEmail() {
        repository.save(nuevoUsuario("shadow", "shadow@test.com"));

        Optional<Usuario> encontrado = repository.findByEmail("shadow@test.com");

        assertTrue(encontrado.isPresent());
        assertEquals("shadow", encontrado.get().getNombreUsuario());
    }

    @Test
    void deberiaConfirmarQueExisteElEmail() {
        repository.save(nuevoUsuario("pixel", "pixel@test.com"));

        assertTrue(repository.existsByEmail("pixel@test.com"));
        assertFalse(repository.existsByEmail("noexiste@test.com"));
    }
}
