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
import com.example.ligamanagermobile.FirebaseAuthManager;
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
                checkIfPartidosGenerated(ligaId);
            }
        }

        return root;
    }

    private void checkIfPartidosGenerated(String ligaId) {
        db.collection("Ligas").document(ligaId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.contains("partidosGenerados") && documentSnapshot.getBoolean("partidosGenerados")) {
                loadPartidos(ligaId);
            } else {
                loadEquipos(ligaId);
            }
        }).addOnFailureListener(e -> {
            // Manejar el error si no se puede verificar el estado de los partidos
        });
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
                // Manejar error si no se pueden cargar los equipos
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

        // Guardar los emparejamientos en Firestore
        FirebaseAuthManager firebaseAuthManager = new FirebaseAuthManager();
        firebaseAuthManager.guardarPartidosEnLiga(ligaId, partidos, new FirebaseAuthManager.OnPartidosSavedListener() {
            @Override
            public void onPartidosSaved() {
                // Actualizar el indicador de que los partidos han sido generados
                db.collection("Ligas").document(ligaId).update("partidosGenerados", true)
                        .addOnSuccessListener(aVoid -> {
                            // Mostrar los emparejamientos en el RecyclerView después de guardar
                            partidosAdapter = new PartidosAdapter(partidos);
                            recyclerViewPartidos.setAdapter(partidosAdapter);
                        });
            }

            @Override
            public void onError(String errorMessage) {
                // Manejar el error de guardar partidos
            }
        });
    }

    private void loadPartidos(String ligaId) {
        db.collection("Ligas").document(ligaId).collection("Partidos")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            partidos.clear();
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                Partido partido = document.toObject(Partido.class);
                                partidos.add(partido);
                            }
                            partidosAdapter = new PartidosAdapter(partidos);
                            recyclerViewPartidos.setAdapter(partidosAdapter);
                        }
                    } else {
                        // Manejar el error si no se pueden cargar los partidos
                    }
                });
    }
}
