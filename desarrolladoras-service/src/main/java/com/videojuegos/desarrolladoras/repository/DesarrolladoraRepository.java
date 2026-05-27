package com.videojuegos.desarrolladoras.repository;

import com.videojuegos.desarrolladoras.model.Desarrolladora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesarrolladoraRepository extends JpaRepository<Desarrolladora, Long> {

    boolean existsByNombre(String nombre);
}
