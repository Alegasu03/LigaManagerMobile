package com.example.ligamanagermobile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DetallesLigaActivity extends AppCompatActivity {

    private TextView textViewNombreLiga;
    private RecyclerView recyclerViewClasificacion;
    private Button btnAgregarEquipo;

    private FirebaseFirestore db;
    private EquipoAdapter equipoAdapter;
    private List<Equipo> equipos;
    private int maxEquipos;
    private boolean userCanAddTeam = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_liga);

        textViewNombreLiga = findViewById(R.id.textViewNombreLiga);
        recyclerViewClasificacion = findViewById(R.id.recyclerViewClasificacion);
        btnAgregarEquipo = findViewById(R.id.btnAgregarEquipo);

        recyclerViewClasificacion.setLayoutManager(new LinearLayoutManager(this));

        equipos = new ArrayList<>();
        equipoAdapter = new EquipoAdapter(equipos);
        recyclerViewClasificacion.setAdapter(equipoAdapter);

        db = FirebaseFirestore.getInstance();

        String ligaId = getIntent().getStringExtra("ligaId");
        if (ligaId != null) {
            loadLigaDetails(ligaId);
        } else {
            Toast.makeText(this, "Error al obtener la ID de la liga", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnAgregarEquipo.setVisibility(View.VISIBLE);
        btnAgregarEquipo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userCanAddTeam) {
                    agregarEquipo();
                } else {
                    Toast.makeText(DetallesLigaActivity.this, "Solo puedes agregar un equipo", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadLigaDetails(String ligaId) {
        CollectionReference ligasRef = db.collection("Ligas");

        ligasRef.document(ligaId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    String nombreLiga = document.getString("NombreLiga");
                    textViewNombreLiga.setText(nombreLiga);

                    maxEquipos = Integer.parseInt(document.getString("NumEquipos"));
                    loadClasificacion(ligaId);
                } else {
                    Toast.makeText(DetallesLigaActivity.this, "No se encontró la liga en la base de datos", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(DetallesLigaActivity.this, "Error al cargar detalles de la liga", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadClasificacion(String ligaId) {
        CollectionReference equiposRef = db.collection("Equipos");

        equiposRef.whereEqualTo("ligaId", ligaId).orderBy("puntos", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        equipos.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            Equipo equipo = document.toObject(Equipo.class);
                            if (equipo != null) {
                                equipos.add(equipo);
                            }
                        }
                        equipoAdapter.notifyDataSetChanged();

                        // Verificar si el usuario puede agregar un equipo
                        if (equipos.size() >= maxEquipos) {
                            userCanAddTeam = false;
                            btnAgregarEquipo.setEnabled(false);
                        }
                    } else {
                        Toast.makeText(DetallesLigaActivity.this, "Error al cargar clasificación", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void agregarEquipo() {
        // Aquí implementarías la lógica para agregar un nuevo equipo a la liga
        // Puedes abrir una nueva actividad para la creación de equipos, por ejemplo
        Toast.makeText(this, "Funcionalidad de agregar equipo aún no implementada", Toast.LENGTH_SHORT).show();
    }
}
