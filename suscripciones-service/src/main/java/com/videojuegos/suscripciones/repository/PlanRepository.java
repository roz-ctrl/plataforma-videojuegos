package com.videojuegos.suscripciones.repository;

import com.videojuegos.suscripciones.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    boolean existsByNombre(String nombre);
}
