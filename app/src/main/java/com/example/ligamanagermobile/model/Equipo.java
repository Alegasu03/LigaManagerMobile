package com.example.ligamanagermobile.model;

public class Equipo {
    private String nombre;
    private String ligaId; // Referencia a la liga a la que pertenece
    private int puntos;

    public Equipo() {
        // Constructor vacío requerido por Firestore
    }

    public Equipo(String nombre, String ligaId, int puntos) {
        this.nombre = nombre;
        this.ligaId = ligaId;
        this.puntos = puntos;
    }

    // Getters y setters

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getLigaId() {
        return ligaId;
    }

    public void setLigaId(String ligaId) {
        this.ligaId = ligaId;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }
}
