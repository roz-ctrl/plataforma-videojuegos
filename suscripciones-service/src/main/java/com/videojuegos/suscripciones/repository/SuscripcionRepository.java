package com.videojuegos.suscripciones.repository;

import com.videojuegos.suscripciones.model.Suscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {

    List<Suscripcion> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioIdAndEstado(Long usuarioId, String estado);
}
