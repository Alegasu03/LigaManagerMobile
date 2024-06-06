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

public class MisArbitrajesActivity extends AppCompatActivity {

    private static final String TAG = "MisArbitrajesActivity";

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

            ligasRef.whereEqualTo("arbitro", userId)
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

                                    Liga liga = new Liga(nombre, 0, maxEquipos);
                                    liga.setId(idLiga);
                                    ligas.add(liga);
                                    ligaAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            Log.w(TAG, "Error al obtener ligas", task.getException());
                        }
                    });
        }
    }
}
