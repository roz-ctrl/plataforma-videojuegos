package com.videojuegos.suscripciones.service;

import com.videojuegos.suscripciones.client.UsuarioClient;
import com.videojuegos.suscripciones.client.dto.UsuarioDTO;
import com.videojuegos.suscripciones.dto.SuscripcionRequestDTO;
import com.videojuegos.suscripciones.dto.SuscripcionResponseDTO;
import com.videojuegos.suscripciones.exception.ComunicacionException;
import com.videojuegos.suscripciones.exception.RecursoNoEncontradoException;
import com.videojuegos.suscripciones.exception.ReglaNegocioException;
import com.videojuegos.suscripciones.model.Plan;
import com.videojuegos.suscripciones.model.Suscripcion;
import com.videojuegos.suscripciones.repository.PlanRepository;
import com.videojuegos.suscripciones.repository.SuscripcionRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Logica de negocio de las Suscripciones de usuarios.
 * Reglas:
 *  - El usuario debe existir y estar activo (validado via Feign).
 *  - Un usuario no puede tener dos suscripciones ACTIVAS a la vez.
 *  - La fecha de fin se calcula segun la duracion del plan.
 */
@Service
public class SuscripcionService {

    private static final Logger log = LoggerFactory.getLogger(SuscripcionService.class);
    private static final String ESTADO_ACTIVA = "ACTIVA";
    private static final String ESTADO_CANCELADA = "CANCELADA";

    private final SuscripcionRepository suscripcionRepository;
    private final PlanRepository planRepository;
    private final UsuarioClient usuarioClient;

    public SuscripcionService(SuscripcionRepository suscripcionRepository,
                              PlanRepository planRepository,
                              UsuarioClient usuarioClient) {
        this.suscripcionRepository = suscripcionRepository;
        this.planRepository = planRepository;
        this.usuarioClient = usuarioClient;
    }

    @Transactional(readOnly = true)
    public List<SuscripcionResponseDTO> listarPorUsuario(Long usuarioId) {
        return suscripcionRepository.findByUsuarioId(usuarioId).stream()
                .map(this::aResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SuscripcionResponseDTO obtenerPorId(Long id) {
        return aResponseDTO(buscarOFallar(id));
    }

    @Transactional
    public SuscripcionResponseDTO suscribir(SuscripcionRequestDTO dto) {
        log.info("Suscribiendo usuario {} al plan {}", dto.getUsuarioId(), dto.getPlanId());

        validarUsuario(dto.getUsuarioId());

        Plan plan = planRepository.findById(dto.getPlanId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el plan con id " + dto.getPlanId()));
        if (!plan.isActivo()) {
            throw new ReglaNegocioException("El plan '" + plan.getNombre() + "' no esta disponible");
        }

        // Regla: una sola suscripcion activa por usuario.
        if (suscripcionRepository.existsByUsuarioIdAndEstado(dto.getUsuarioId(), ESTADO_ACTIVA)) {
            throw new ReglaNegocioException("El usuario ya tiene una suscripcion activa");
        }

        LocalDate inicio = LocalDate.now();
        Suscripcion suscripcion = new Suscripcion();
        suscripcion.setUsuarioId(dto.getUsuarioId());
        suscripcion.setPlan(plan);
        suscripcion.setFechaInicio(inicio);
        suscripcion.setFechaFin(inicio.plusMonths(plan.getDuracionMeses()));
        suscripcion.setEstado(ESTADO_ACTIVA);
        suscripcion.setRenovacionAutomatica(dto.isRenovacionAutomatica());

        Suscripcion guardada = suscripcionRepository.save(suscripcion);
        log.info("Suscripcion {} creada, vigente hasta {}", guardada.getId(), guardada.getFechaFin());
        return aResponseDTO(guardada);
    }

    @Transactional
    public SuscripcionResponseDTO cancelar(Long id) {
        Suscripcion suscripcion = buscarOFallar(id);
        if (ESTADO_CANCELADA.equals(suscripcion.getEstado())) {
            throw new ReglaNegocioException("La suscripcion ya esta cancelada");
        }
        suscripcion.setEstado(ESTADO_CANCELADA);
        suscripcion.setRenovacionAutomatica(false);
        log.info("Suscripcion {} cancelada", id);
        return aResponseDTO(suscripcionRepository.save(suscripcion));
    }

    // ---------- comunicacion remota ----------

    private void validarUsuario(Long usuarioId) {
        try {
            UsuarioDTO usuario = usuarioClient.obtenerPorId(usuarioId);
            if (!usuario.isActivo()) {
                throw new ReglaNegocioException("El usuario " + usuarioId + " esta inactivo");
            }
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("No existe el usuario con id " + usuarioId);
        } catch (FeignException e) {
            throw new ComunicacionException("No se pudo contactar al servicio de Usuarios: " + e.getMessage());
        }
    }

    // ---------- utilitarios ----------

    private Suscripcion buscarOFallar(Long id) {
        return suscripcionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la suscripcion con id " + id));
    }

    private SuscripcionResponseDTO aResponseDTO(Suscripcion s) {
        SuscripcionResponseDTO dto = new SuscripcionResponseDTO();
        dto.setId(s.getId());
        dto.setUsuarioId(s.getUsuarioId());
        dto.setPlanId(s.getPlan().getId());
        dto.setPlanNombre(s.getPlan().getNombre());
        dto.setFechaInicio(s.getFechaInicio());
        dto.setFechaFin(s.getFechaFin());
        dto.setEstado(s.getEstado());
        dto.setRenovacionAutomatica(s.isRenovacionAutomatica());
        return dto;
    }
}
