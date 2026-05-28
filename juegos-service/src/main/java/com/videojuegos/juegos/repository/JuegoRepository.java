package com.videojuegos.juegos.repository;

import com.videojuegos.juegos.model.Juego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JuegoRepository extends JpaRepository<Juego, Long> {

    List<Juego> findByCategoriaId(Long categoriaId);

    List<Juego> findByDesarrolladoraId(Long desarrolladoraId);
}
