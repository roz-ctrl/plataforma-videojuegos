package com.videojuegos.suscripciones.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** DTO de entrada para crear/actualizar un plan de suscripcion. */
public class PlanRequestDTO {

    @NotBlank(message = "El nombre del plan es obligatorio")
    @Size(max = 60)
    private String nombre;

    @NotNull(message = "El precio mensual es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private BigDecimal precioMensual;

    @NotNull(message = "La duracion en meses es obligatoria")
    @Min(value = 1, message = "La duracion minima es 1 mes")
    private Integer duracionMeses;

    @Size(max = 255)
    private String descripcion;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecioMensual() {
        return precioMensual;
    }

    public void setPrecioMensual(BigDecimal precioMensual) {
        this.precioMensual = precioMensual;
    }

    public Integer getDuracionMeses() {
        return duracionMeses;
    }

    public void setDuracionMeses(Integer duracionMeses) {
        this.duracionMeses = duracionMeses;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
