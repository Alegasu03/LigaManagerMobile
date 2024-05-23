package com.example.ligamanagermobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ligamanagermobile.model.Equipo;
import com.example.ligamanagermobile.ui.EquipoAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DetallesLigaActivity extends AppCompatActivity {

    private static final String TAG = "DetallesLigaActivity";

    private TextView textViewNombreLiga;
    private ImageButton btnAgregarEquipo;

    private FirebaseFirestore db;
    private EquipoAdapter equipoAdapter;
    private List<Equipo> equipos;
    private boolean userCanAddTeam = true;
    private String ligaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtils.setupFullscreen(this);
        setContentView(R.layout.activity_detalles_liga);

        textViewNombreLiga = findViewById(R.id.textViewNombreLiga);
        RecyclerView recyclerViewClasificacion = findViewById(R.id.recyclerViewClasificacion);
        btnAgregarEquipo = findViewById(R.id.btnAgregarEquipo);

        recyclerViewClasificacion.setLayoutManager(new LinearLayoutManager(this));

        equipos = new ArrayList<>();
        equipoAdapter = new EquipoAdapter(equipos);
        recyclerViewClasificacion.setAdapter(equipoAdapter);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        if (intent != null) {
            ligaId = intent.getStringExtra("ligaId");
            if (ligaId != null) {
                loadLigaDetailsAndClasificacion(ligaId);
            } else {
                Toast.makeText(this, "Error al obtener la ID de la liga", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Error al obtener la ID de la liga", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnAgregarEquipo.setVisibility(View.VISIBLE);
        btnAgregarEquipo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarEquipo();
            }
        });
    }

    private void loadLigaDetailsAndClasificacion(String ligaId) {
        CollectionReference ligasRef = db.collection("Ligas");

        ligasRef.document(ligaId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    String nombreLiga = document.getString("NombreLiga");
                    textViewNombreLiga.setText(nombreLiga);

                    int maxEquipos = Integer.parseInt(document.getString("NumEquipos"));

                    // Load clasificacion
                    db.collection("Equipos")
                            .whereEqualTo("ligaId", ligaId)
                            .get()
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    equipos.clear();
                                    for (DocumentSnapshot document1 : task1.getResult()) {
                                        String nombreEquipo = document1.getString("nombreEquipo");
                                        Long puntuacionLong = document1.getLong("puntuacion");
                                        int puntuacion = (puntuacionLong != null) ? puntuacionLong.intValue() : 0;

                                        Equipo equipo = new Equipo(nombreEquipo, ligaId, puntuacion, null);
                                        equipos.add(equipo);
                                    }

                                    equipoAdapter.notifyDataSetChanged();

                                    boolean userCanAddTeam = equipos.size() < maxEquipos;
                                    btnAgregarEquipo.setEnabled(userCanAddTeam);

                                    // Cargar equipos de Firestore
                                    loadEquiposFromFirestore(ligaId);
                                } else {
                                    Log.e(TAG, "Error al cargar la clasificación", task1.getException());
                                }
                            });

                } else {
                    Toast.makeText(DetallesLigaActivity.this, "No se encontró la liga en la base de datos", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(DetallesLigaActivity.this, "Error al cargar detalles de la liga", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void agregarEquipo() {
        if (userCanAddTeam) {
            Intent intent = new Intent(DetallesLigaActivity.this, AgregarEquipoActivity.class);
            intent.putExtra("LIGA_ID", ligaId);
            startActivity(intent);
        } else {
            Toast.makeText(this, "No se pueden agregar más equipos a esta liga", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para cargar equipos desde Firestore
    private void loadEquiposFromFirestore(String ligaId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Obtener la referencia de la colección "Equipos" dentro del documento de la liga
        CollectionReference equiposRef = db.collection("Ligas").document(ligaId).collection("Equipos");

        equiposRef.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w(TAG, "Error al obtener equipos de la liga", e);
                return;
            }

            if (snapshots != null) {
                equipos.clear();
                for (DocumentSnapshot equipoDoc : snapshots.getDocuments()) {
                    String nombreEquipo = equipoDoc.getString("NombreEquipo");
                    long puntuacion = equipoDoc.getLong("Puntuacion");

                    // Crea un objeto Equipo con el nombre y la puntuación
                    Equipo equipo = new Equipo(nombreEquipo, ligaId, (int) puntuacion, null);
                    equipos.add(equipo);
                }

                // Notifica al adaptador que los datos han cambiado
                equipoAdapter.notifyDataSetChanged();
            }
        });
    }
}
