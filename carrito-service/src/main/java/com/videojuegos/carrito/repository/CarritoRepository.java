package com.videojuegos.carrito.repository;

import com.videojuegos.carrito.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    /** Devuelve el carrito ABIERTO de un usuario, si existe. */
    Optional<Carrito> findByUsuarioIdAndEstado(Long usuarioId, String estado);
}
