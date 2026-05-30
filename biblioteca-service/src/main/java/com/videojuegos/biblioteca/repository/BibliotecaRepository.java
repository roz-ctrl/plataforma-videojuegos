package com.videojuegos.biblioteca.repository;

import com.videojuegos.biblioteca.model.EntradaBiblioteca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BibliotecaRepository extends JpaRepository<EntradaBiblioteca, Long> {

    List<EntradaBiblioteca> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioIdAndJuegoId(Long usuarioId, Long juegoId);
}
