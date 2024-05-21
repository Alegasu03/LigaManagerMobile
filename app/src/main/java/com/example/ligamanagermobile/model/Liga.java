package com.example.ligamanagermobile.model;

public class Liga {
    private String id;
    private String nombre;
    private int numEquipos;
    private int maxEquipos;
    private String usuarioPropietario;

    public Liga(String nombre, int numEquipos, int maxEquipos) {
        this.nombre = nombre;
        this.numEquipos = numEquipos;
        this.maxEquipos = maxEquipos;
    }
    public Liga() {
        // Requerido por Firebase para deserializar los datos
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getNumEquipos() {
        return numEquipos;
    }

    public int getMaxEquipos() {
        return maxEquipos;
    }

}
