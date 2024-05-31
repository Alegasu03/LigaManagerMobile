package com.example.ligamanagermobile;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LigaCrear extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liga_crear);
        ActivityUtils.setupFullscreen(this);

        // Instanciar todos los objetos
        EditText ligaNombre = findViewById(R.id.ligaNombre);
        EditText descripcion = findViewById(R.id.descripcion);
        Spinner selectorNumero = findViewById(R.id.selectorNumero);
        Spinner selectorMunicipio = findViewById(R.id.selectorMunicipio);
        RadioGroup radioGroup = findViewById(R.id.radioGroupAccesibilidad);
        RadioButton normal = findViewById(R.id.normal);
        RadioButton discapacidad = findViewById(R.id.discapacidad);
        Button botonCancelar = findViewById(R.id.botonCancelar);
        Button botonCrear = findViewById(R.id.botonCrear);

        //Configuración Spinner N Equipos
        String[] equipos = getResources().getStringArray(R.array.Numero_De_Equipos);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, equipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectorNumero.setAdapter(adapter);

        //Configuración Spinner Municipios
        String[] municipios = getResources().getStringArray(R.array.Municipios_Madrid);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, municipios);
        selectorMunicipio.setAdapter(adapter1);

        // Listener del botón Cancelar
        botonCancelar.setOnClickListener(v -> finish());

        // Listener del botón Crear
        botonCrear.setOnClickListener(v -> {
            String nombreLiga = ligaNombre.getText().toString().trim();
            String desc = descripcion.getText().toString().trim();
            String numEquipos = selectorNumero.getSelectedItem().toString();
            String municipio = selectorMunicipio.getSelectedItem().toString();
            String accesibilidad = normal.isChecked() ? "Normal" : "Discapacidad";

            // Validar campos obligatorios
            if (nombreLiga.isEmpty() || desc.isEmpty()) {
                // Mostrar mensaje de error si los campos están vacíos
                Toast.makeText(LigaCrear.this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show();
            } else {
                // Crear la liga
                FirebaseAuthManager firebaseAuthManager = new FirebaseAuthManager();
                firebaseAuthManager.crearLiga(LigaCrear.this, nombreLiga, desc, numEquipos, municipio, accesibilidad);
                finish();
            }
        });
    }
}
