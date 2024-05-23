package com.example.ligamanagermobile;

import static com.example.ligamanagermobile.R.layout.activity_liga_crear;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ligamanagermobile.ui.futbol.FutbolFragment;

import org.checkerframework.checker.units.qual.N;


public class LigaCrear extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_liga_crear);
        ActivityUtils.setupFullscreen(this);


        // Instanciar todos los objetos
        EditText ligaNombre = findViewById(R.id.ligaNombre);
        EditText descripcion = findViewById(R.id.descripcion);
        Spinner selectorNumero = findViewById(R.id.selectorNumero);
        Spinner selectorMunicipio = findViewById(R.id.selectorMunicipio);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        RadioButton publica = findViewById(R.id.publica);
        RadioButton privada = findViewById(R.id.privada);
        Button botonX = findViewById(R.id.botonX);
        Button botonV = findViewById(R.id.botonV);

        //Configuración Spinner N Equipos
        String[] equipos= getResources().getStringArray(R.array.Numero_De_Equipos);
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, equipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectorNumero.setAdapter(adapter);
        //Confg spinner municpios
        String[]municpios= getResources().getStringArray(R.array.Municipios_Madrid);
        ArrayAdapter<String> adapter1= new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, municpios);
        selectorMunicipio.setAdapter(adapter1);

        botonX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        botonV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombreLiga = ligaNombre.getText().toString();
                String desc = descripcion.getText().toString();
                String numEquipos = selectorNumero.getSelectedItem().toString();
                String municipio = selectorMunicipio.getSelectedItem().toString();
                boolean esPublica = publica.isChecked(); // Suponiendo que "publica" es tu RadioButton

                FirebaseAuthManager firebaseAuthManager = new FirebaseAuthManager();
                firebaseAuthManager.crearLiga(LigaCrear.this, nombreLiga, desc, numEquipos, municipio, esPublica);
                finish();            }
        });




    }




}
