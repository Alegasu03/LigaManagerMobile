package com.example.ligamanagermobile;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ligamanagermobile.Adapters.AyudaAdapter;
import com.example.ligamanagermobile.model.Ayuda;

import java.util.ArrayList;
import java.util.List;

public class AyudaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayuda);

        RecyclerView recyclerViewAyuda = findViewById(R.id.recyclerViewAyuda);
        recyclerViewAyuda.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAyuda.setAdapter(new AyudaAdapter(getAyudaList()));
    }

    private List<Ayuda> getAyudaList() {
        List<Ayuda> ayudaList = new ArrayList<>();
        ayudaList.add(new Ayuda("¿Cómo crear una cuenta?", "Para crear una cuenta, haz clic en el botón 'Registrarse' y sigue las instrucciones."));
        ayudaList.add(new Ayuda("¿Cómo restablecer mi contraseña?", "Para restablecer tu contraseña, ve a la pantalla de inicio de sesión y selecciona '¿Olvidaste tu contraseña?'."));
        // Añade más preguntas y respuestas aquí
        return ayudaList;
    }
}
