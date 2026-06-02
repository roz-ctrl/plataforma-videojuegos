package com.videojuegos.resenas.dto;

/** DTO de salida con el resumen de calificaciones de un juego. */
public class ResumenJuegoDTO {

    private Long juegoId;
    private long totalResenas;
    private double promedioCalificacion;
    private long totalRecomiendan;

    public ResumenJuegoDTO(Long juegoId, long totalResenas, double promedioCalificacion, long totalRecomiendan) {
        this.juegoId = juegoId;
        this.totalResenas = totalResenas;
        this.promedioCalificacion = promedioCalificacion;
        this.totalRecomiendan = totalRecomiendan;
    }

    public Long getJuegoId() {
        return juegoId;
    }

    public long getTotalResenas() {
        return totalResenas;
    }

    public double getPromedioCalificacion() {
        return promedioCalificacion;
    }

    public long getTotalRecomiendan() {
        return totalRecomiendan;
    }
}
