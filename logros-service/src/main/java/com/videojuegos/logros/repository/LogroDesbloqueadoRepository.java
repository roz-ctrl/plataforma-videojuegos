package com.videojuegos.logros.repository;

import com.videojuegos.logros.model.LogroDesbloqueado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogroDesbloqueadoRepository extends JpaRepository<LogroDesbloqueado, Long> {

    List<LogroDesbloqueado> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioIdAndLogroId(Long usuarioId, Long logroId);
}
