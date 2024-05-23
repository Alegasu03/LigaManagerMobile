package com.example.ligamanagermobile.model;

import java.util.List;

public class Equipo {
    private String nombreEquipo;
    private String ligaId; // Referencia a la liga a la que pertenece
    private int puntuacion; // Cambié el nombre del campo para que coincida con el de Firestore
    private List<Jugador> jugadores;

    public Equipo(String nombreEquipo, String ligaId, int puntuacion, List<Jugador> jugadores) {
        this.nombreEquipo = nombreEquipo;
        this.ligaId = ligaId;
        this.puntuacion = puntuacion;
        this.jugadores = jugadores;
    }

    public Equipo() {
    }



    public String getNombreEquipo() {
        return nombreEquipo;
    }


    public String getLigaId() {
        return ligaId;
    }

    public void setLigaId(String ligaId) {
        this.ligaId = ligaId;
    }

    public int getPuntuacion() {
        return puntuacion;
    }



    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public void setJugadores(List<Jugador> jugadores) {
        this.jugadores = jugadores;
    }
}
