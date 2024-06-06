package com.example.ligamanagermobile;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ligamanagermobile.Adapters.LigaAdapter;
import com.example.ligamanagermobile.model.Liga;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Buscador_Ligas extends AppCompatActivity {

    private LigaAdapter ligaAdapter;
    private List<Liga> ligas;
    private Spinner spinnerMunicipio;
    private Button btnBuscar;
    private ImageView imageViewInfo;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtils.setupFullscreen(this);
        setContentView(R.layout.activity_buscador_ligas);

        db = FirebaseFirestore.getInstance();

        RecyclerView recyclerViewLigas = findViewById(R.id.recyclerViewLigas);
        recyclerViewLigas.setLayoutManager(new LinearLayoutManager(this));
        ligas = new ArrayList<>();
        ligaAdapter = new LigaAdapter(ligas, this);
        recyclerViewLigas.setAdapter(ligaAdapter);

        spinnerMunicipio = findViewById(R.id.spinnerMunicipio);
        btnBuscar = findViewById(R.id.btnBuscar);

        // Lista de municipios predefinidos
        List<String> municipios = new ArrayList<>();
        municipios.add("Selecciona un municipio");
        municipios.add("Fuenlabrada");
        municipios.add("Leganes");
        municipios.add("Getafe");
        municipios.add("Mostoles");
        municipios.add("Alcorcon");
        municipios.add("Pinto");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, municipios);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMunicipio.setAdapter(adapter);

        btnBuscar.setOnClickListener(v -> loadLigasFromFirestore());



        loadLigasFromFirestore();
    }

    private void loadLigasFromFirestore() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            CollectionReference ligasRef = db.collection("Ligas");

            String municipioSeleccionado = spinnerMunicipio.getSelectedItem().toString();



            ligasRef.whereNotEqualTo("UsuarioPropietario", userId)
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
                                String ligaMunicipio = doc.getString("Municipio"); // Obtener el municipio

                                if (maxEquiposString != null && !maxEquiposString.isEmpty()) {
                                    maxEquipos = Integer.parseInt(maxEquiposString);
                                } else {
                                    maxEquipos = 0;
                                }

                                // Aplicar filtro por municipio
                                if (municipioSeleccionado.equalsIgnoreCase(ligaMunicipio)) {
                                    // Obtener la cantidad actual de equipos para esta liga
                                    CollectionReference equiposRef = db.collection("Ligas").document(idLiga).collection("Equipos");
                                    equiposRef.get().addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            int numEquipos = task.getResult().size();

                                            Liga liga = new Liga(nombre, numEquipos, maxEquipos);
                                            liga.setId(idLiga); // Asignar el ID al objeto Liga
                                            ligas.add(liga);
                                            ligaAdapter.notifyDataSetChanged();
                                        } else {
                                            Log.d(TAG, "Error al obtener equipos para la liga " + nombre, task.getException());
                                        }
                                    });
                                }
                            }
                        }

                  
                    });
        }
    }


}


