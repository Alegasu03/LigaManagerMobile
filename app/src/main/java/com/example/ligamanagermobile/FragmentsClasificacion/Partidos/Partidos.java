package com.example.ligamanagermobile.FragmentsClasificacion.Partidos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ligamanagermobile.Adapters.PartidosAdapter;
import com.example.ligamanagermobile.R;
import com.example.ligamanagermobile.model.Partido;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Partidos extends Fragment {

    private List<Partido> partidos;
    private RecyclerView recyclerViewPartidos;
    private PartidosAdapter partidosAdapter;
    private FirebaseFirestore db;
    private String ligaId;

    public static Partidos newInstance(String ligaId) {
        Partidos fragment = new Partidos();
        Bundle args = new Bundle();
        args.putString("ligaId", ligaId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_partidos, container, false);

        recyclerViewPartidos = root.findViewById(R.id.recyclerViewPartidos);
        recyclerViewPartidos.setLayoutManager(new LinearLayoutManager(getContext()));
        partidos = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        Bundle bundle = getArguments();
        if (bundle != null) {
            ligaId = bundle.getString("ligaId");
            if (ligaId != null) {
                loadEquipos(ligaId);
            }
        }

        return root;
    }

    private void loadEquipos(String ligaId) {
        CollectionReference equiposRef = db.collection("Ligas").document(ligaId).collection("Equipos");

        equiposRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    List<String> nombresEquipos = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        String nombreEquipo = document.getString("NombreEquipo");
                        nombresEquipos.add(nombreEquipo);
                    }
                    generateMatches(nombresEquipos);
                }
            } else {
                // Manejar el error
            }
        });
    }

    private void generateMatches(List<String> nombresEquipos) {
        partidos.clear();

        // Generar los emparejamientos
        for (int i = 0; i < nombresEquipos.size() - 1; i++) {
            for (int j = i + 1; j < nombresEquipos.size(); j++) {
                String local = nombresEquipos.get(i);
                String visitante = nombresEquipos.get(j);
                Partido partido = new Partido(local, visitante);
                partidos.add(partido);
            }
        }

        // Mostrar los emparejamientos en el RecyclerView
        partidosAdapter = new PartidosAdapter(partidos);
        recyclerViewPartidos.setAdapter(partidosAdapter);
    }
}
