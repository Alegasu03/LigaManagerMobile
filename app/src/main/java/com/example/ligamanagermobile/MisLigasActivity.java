package com.example.ligamanagermobile;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ligamanagermobile.model.Liga;
import com.example.ligamanagermobile.ui.LigaAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MisLigasActivity extends AppCompatActivity {

    private LigaAdapter ligaAdapter;
    private List<Liga> ligas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtils.setupFullscreen(this);

        setContentView(R.layout.activity_mis_ligas);

        RecyclerView recyclerViewLigas = findViewById(R.id.recyclerViewLigas);
        recyclerViewLigas.setLayoutManager(new LinearLayoutManager(this));
        ligas = new ArrayList<>();
        ligaAdapter = new LigaAdapter(ligas,this);
        recyclerViewLigas.setAdapter(ligaAdapter);

        loadLigasFromFirestore();
    }

    private void loadLigasFromFirestore() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference ligasRef = db.collection("Ligas");

            ligasRef.whereEqualTo("UsuarioPropietario", userId)
                    .addSnapshotListener((snapshots, e) -> {
                        if (e != null) {
                            Log.w(TAG, "Error al obtener ligas", e);
                            return;
                        }

                        ligas.clear();
                        if (snapshots != null) {
                            for (DocumentSnapshot doc : snapshots.getDocuments()) {
                                String nombre = doc.getString("NombreLiga");
                                String maxEquiposString = doc.getString("NumEquipos");
                                int maxEquipos;
                                String idLiga = doc.getId(); // Obtener el ID del documento

                                if (maxEquiposString != null && !maxEquiposString.isEmpty()) {
                                    maxEquipos = Integer.parseInt(maxEquiposString);
                                } else {
                                    maxEquipos = 0;
                                }

                                // Obtener la cantidad actual de equipos para esta liga
                                CollectionReference equiposRef = db.collection("Ligas").document(idLiga).collection("Equipos");
                                equiposRef.get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        int numEquipos = task.getResult().size();

                                        // Verificar si se pueden crear más equipos
                                        if (numEquipos < maxEquipos) {
                                            Liga liga = new Liga(nombre, numEquipos, maxEquipos);
                                            liga.setId(idLiga); // Asignar el ID al objeto Liga
                                            ligas.add(liga);
                                            ligaAdapter.notifyDataSetChanged();
                                        } else {
                                            // Puedes manejar el caso cuando se alcanza el límite de equipos aquí
                                            Log.d(TAG, "Ya se alcanzó el límite de equipos para la liga " + nombre);
                                        }
                                    } else {
                                        Log.d(TAG, "Error al obtener equipos para la liga " + nombre, task.getException());
                                    }
                                });
                            }
                        }
                    });
        }
    }


}
