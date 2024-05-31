package com.example.ligamanagermobile;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ligamanagermobile.model.Liga;
import com.example.ligamanagermobile.Adapters.LigaAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MisParticipaciones extends AppCompatActivity {

    private static final String TAG = "MisParticipacionesActivity";

    private LigaAdapter ligaAdapter;
    private List<Liga> ligas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtils.setupFullscreen(this);

        setContentView(R.layout.activity_mis_participaciones);

        RecyclerView recyclerViewLigas = findViewById(R.id.recyclerViewLigas);
        recyclerViewLigas.setLayoutManager(new LinearLayoutManager(this));
        ligas = new ArrayList<>();
        ligaAdapter = new LigaAdapter(ligas, this);
        recyclerViewLigas.setAdapter(ligaAdapter);

        // Cargar las ligas desde Firestore
        loadLigasFromFirestore();
    }

    private void loadLigasFromFirestore() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference ligasRef = db.collection("Ligas");

            ligasRef.whereNotEqualTo("UsuarioPropietario", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            ligas.clear();
                            if (task.getResult() != null) {
                                for (DocumentSnapshot doc : task.getResult()) {
                                    String nombre = doc.getString("NombreLiga");
                                    String maxEquiposString = doc.getString("NumEquipos");
                                    int maxEquipos;
                                    String idLiga = doc.getId();

                                    if (maxEquiposString != null && !maxEquiposString.isEmpty()) {
                                        maxEquipos = Integer.parseInt(maxEquiposString);
                                    } else {
                                        maxEquipos = 0;
                                    }

                                    // Verificar si el usuario tiene un equipo en esta liga
                                    CollectionReference equiposRef = db.collection("Ligas").document(idLiga).collection("Equipos");
                                    equiposRef.whereEqualTo("PropietarioId", userId)
                                            .get()
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful() && !task1.getResult().isEmpty()) {
                                                    int numEquipos = task1.getResult().size();

                                                    Liga liga = new Liga(nombre, numEquipos, maxEquipos);
                                                    liga.setId(idLiga);
                                                    ligas.add(liga);
                                                    ligaAdapter.notifyDataSetChanged();
                                                } else if (!task1.isSuccessful()) {
                                                    Log.d(TAG, "Error al obtener equipos para la liga " + nombre, task1.getException());
                                                }
                                            });
                                }
                            }
                        } else {
                            Log.w(TAG, "Error al obtener ligas", task.getException());
                        }
                    });
        }
    }
}
