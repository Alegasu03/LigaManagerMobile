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
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MisLigasActivity extends AppCompatActivity {

    private RecyclerView recyclerViewLigas;
    private LigaAdapter ligaAdapter;
    private List<Liga> ligas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_ligas);

        recyclerViewLigas = findViewById(R.id.recyclerViewLigas);
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
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                return;
                            }

                            ligas.clear();
                            if (snapshots != null) {
                                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                                    String nombre = doc.getString("NombreLiga");
                                    String maxEquiposString = doc.getString("NumEquipos");
                                    int maxEquipos = 0;
                                    // Obtener el ID del documento
                                    String idLiga = doc.getId();
                                    if (maxEquiposString != null && !maxEquiposString.isEmpty()) {
                                        maxEquipos = Integer.parseInt(maxEquiposString);
                                    }

                                    // Obtener el número actual de equipos para esta liga
                                    CollectionReference equiposRef = db.collection("Equipos");
                                    int finalMaxEquipos = maxEquipos;
                                    equiposRef.whereEqualTo("ligaId", idLiga).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                int numEquipos = task.getResult().size();

                                                // Verificar si se pueden crear más equipos
                                                if (numEquipos < finalMaxEquipos) {
                                                    Liga liga = new Liga(nombre, numEquipos, finalMaxEquipos);
                                                    liga.setId(idLiga); // Asignar el ID al objeto Liga
                                                    ligas.add(liga);
                                                    ligaAdapter.notifyDataSetChanged();
                                                } else {
                                                    // Muestra un mensaje o toma otra acción si ya se alcanzó el límite de equipos
                                                    Log.d(TAG, "Ya se alcanzó el límite de equipos para la liga " + nombre);
                                                }
                                            } else {
                                                Log.d(TAG, "Error obteniendo documentos: ", task.getException());
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
        }
    }

}
