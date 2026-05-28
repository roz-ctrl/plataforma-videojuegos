package com.videojuegos.juegos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de salida de un juego. Incluye datos enriquecidos obtenidos por Feign
 * desde los microservicios de Desarrolladoras y Categorias (nombre y precio final).
 */
public class JuegoResponseDTO {

    private Long id;
    private String titulo;
    private String descripcion;
    private BigDecimal precio;
    private BigDecimal precioFinal;
    private Integer descuentoPorcentaje;
    private LocalDate fechaLanzamiento;
    private Long desarrolladoraId;
    private String desarrolladoraNombre;
    private Long categoriaId;
    private String categoriaNombre;
    private String plataforma;
    private String requisitosMinimos;
    private String requisitosRecomendados;
    private boolean activo;

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

    public BigDecimal getPrecioFinal() {
        return precioFinal;
    }

    public void setPrecioFinal(BigDecimal precioFinal) {
        this.precioFinal = precioFinal;
    }

    public Integer getDescuentoPorcentaje() {
        return descuentoPorcentaje;
    }

    public void setDescuentoPorcentaje(Integer descuentoPorcentaje) {
        this.descuentoPorcentaje = descuentoPorcentaje;
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

    public String getDesarrolladoraNombre() {
        return desarrolladoraNombre;
    }

    public void setDesarrolladoraNombre(String desarrolladoraNombre) {
        this.desarrolladoraNombre = desarrolladoraNombre;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
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

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
