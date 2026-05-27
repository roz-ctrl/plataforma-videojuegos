package com.videojuegos.desarrolladoras.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO de entrada para crear/actualizar una desarrolladora.
 */
public class DesarrolladoraRequestDTO {

    @NotBlank(message = "El nombre de la desarrolladora es obligatorio")
    @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
    private String nombre;

    @Size(max = 80, message = "El pais no puede superar 80 caracteres")
    private String paisOrigen;

    @Size(max = 150, message = "El sitio web no puede superar 150 caracteres")
    private String sitioWeb;

    @PastOrPresent(message = "La fecha de fundacion no puede ser futura")
    private LocalDate fechaFundacion;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPaisOrigen() {
        return paisOrigen;
    }

    public void setPaisOrigen(String paisOrigen) {
        this.paisOrigen = paisOrigen;
    }

    public String getSitioWeb() {
        return sitioWeb;
    }

    public void setSitioWeb(String sitioWeb) {
        this.sitioWeb = sitioWeb;
    }

    public LocalDate getFechaFundacion() {
        return fechaFundacion;
    }

    public void setFechaFundacion(LocalDate fechaFundacion) {
        this.fechaFundacion = fechaFundacion;
    }
}
