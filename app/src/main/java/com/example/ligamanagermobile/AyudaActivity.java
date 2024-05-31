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
        ayudaList.add(new Ayuda("¿Cómo creo una liga?", "Para crear una liga debes dirigirte al apartado de fútbol, pulsar el botón Crear Una Liga y seleccionar las distintas ocpiones que desees."));
        ayudaList.add(new Ayuda("¿Cómo puedo ver mis ligas?", "Dirigete al apartado 'Mis Ligas', ahí podrás gestionar tus ligas. Puedes borrarlas deslizando tu liga"));
        ayudaList.add(new Ayuda("¿Cómo creo un equipo?", "Para crear un equipo debes drigirte a la liga que desees y añadir tu equipo jugador a jugador"));
        ayudaList.add(new Ayuda("¿Cómo participo en una liga? ¿Cómo puedo ver mis participaciones?", "Para participar en una liga debes crear un equipo dentro de esta, para ello ve al apartado de fútobl y busca una liga que se adapte a tus características. " +
                "Para ver tus participaciones dispones de un botón en la pantalla principal"));
        ayudaList.add(new Ayuda("¿Cómo se arbitra un partido?","Para arbitrar un partido primero de todo debes solicitar el estado de árbitro, una vez concedido este estado, debes elegir la liga que arbitrar y elegir uno de los partidos disponibles"));
        return ayudaList;
    }
}
