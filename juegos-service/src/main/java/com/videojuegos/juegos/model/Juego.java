package com.videojuegos.juegos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad JPA que representa un juego del catalogo.
 * desarrolladoraId y categoriaId son referencias logicas a entidades
 * que viven en OTROS microservicios (no son claves foraneas locales).
 */
@Entity
@Table(name = "juegos")
public class Juego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(length = 1000)
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "fecha_lanzamiento")
    private LocalDate fechaLanzamiento;

    /** Referencia logica al microservicio de Desarrolladoras. */
    @Column(name = "desarrolladora_id", nullable = false)
    private Long desarrolladoraId;

    /** Referencia logica al microservicio de Categorias. */
    @Column(name = "categoria_id", nullable = false)
    private Long categoriaId;

    @Column(length = 60)
    private String plataforma;

    @Column(name = "requisitos_minimos", length = 500)
    private String requisitosMinimos;

    @Column(name = "requisitos_recomendados", length = 500)
    private String requisitosRecomendados;

    /** Porcentaje de descuento aplicado (0-100). */
    @Column(name = "descuento_porcentaje", nullable = false)
    private Integer descuentoPorcentaje = 0;

    @Column(nullable = false)
    private boolean activo = true;

    public Juego() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public LocalDate getFechaLanzamiento() {
        return fechaLanzamiento;
    }

    public void setFechaLanzamiento(LocalDate fechaLanzamiento) {
        this.fechaLanzamiento = fechaLanzamiento;
    }

    public Long getDesarrolladoraId() {
        return desarrolladoraId;
    }

    public void setDesarrolladoraId(Long desarrolladoraId) {
        this.desarrolladoraId = desarrolladoraId;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }

    public String getRequisitosMinimos() {
        return requisitosMinimos;
    }

    public void setRequisitosMinimos(String requisitosMinimos) {
        this.requisitosMinimos = requisitosMinimos;
    }

    public String getRequisitosRecomendados() {
        return requisitosRecomendados;
    }

    public void setRequisitosRecomendados(String requisitosRecomendados) {
        this.requisitosRecomendados = requisitosRecomendados;
    }

    public Integer getDescuentoPorcentaje() {
        return descuentoPorcentaje;
    }

    public void setDescuentoPorcentaje(Integer descuentoPorcentaje) {
        this.descuentoPorcentaje = descuentoPorcentaje;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
