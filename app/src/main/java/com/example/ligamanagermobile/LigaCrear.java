package com.example.ligamanagermobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ligamanagermobile.ui.futbol.FutbolFragment;


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
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        RadioButton publica = findViewById(R.id.publica);
        RadioButton privada = findViewById(R.id.privada);
        Button botonX = findViewById(R.id.botonX);
        Button botonV = findViewById(R.id.botonV);


        botonX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }




}
