package com.example.ligamanagermobile.FragmentsClasificacion.Clasificacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ligamanagermobile.Adapters.EquipoAdapter;
import com.example.ligamanagermobile.AgregarEquipoActivity;
import com.example.ligamanagermobile.R;
import com.example.ligamanagermobile.model.Equipo;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Clasificacion extends Fragment {

    private FirebaseFirestore db;
    private EquipoAdapter equipoAdapter;
    private List<Equipo> equipos;
    private boolean userCanAddTeam = true;
    private String ligaId;
    private TextView textViewNombreLiga;
    private String ownerId;
    private String currentUserPropietarioId;
    private int maxEquipos;

    public static Clasificacion newInstance(String ligaId) {
        Clasificacion fragment = new Clasificacion();
        Bundle args = new Bundle();
        args.putString("ligaId", ligaId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_clasificacion, container, false);

        textViewNombreLiga = root.findViewById(R.id.textViewNombreLiga);
        RecyclerView recyclerViewClasificacion = root.findViewById(R.id.recyclerViewClasificacion);
        recyclerViewClasificacion.setLayoutManager(new LinearLayoutManager(getContext()));

        equipos = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserPropietarioId = currentUser.getUid();
        }

        equipoAdapter = new EquipoAdapter(equipos, currentUserPropietarioId);
        recyclerViewClasificacion.setAdapter(equipoAdapter);

        Bundle bundle = getArguments();
        if (bundle != null) {
            ligaId = bundle.getString("ligaId");
            if (ligaId != null) {
                loadClasificacion(ligaId);
            }
        }

        getLeagueOwner();

        AppCompatImageButton btnAgregarEquipo = root.findViewById(R.id.btnAgregarEquipo);
        btnAgregarEquipo.setBackground(null);
        btnAgregarEquipo.setOnClickListener(v -> verificarYAgregarEquipo());

        return root;
    }

    private void loadClasificacion(String ligaId) {
        CollectionReference equiposRef = db.collection("Ligas").document(ligaId).collection("Equipos");

        equiposRef.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                return;
            }

            if (snapshots != null) {
                equipos.clear();
                for (DocumentSnapshot equipoDoc : snapshots.getDocuments()) {
                    String nombreEquipo = equipoDoc.getString("NombreEquipo");
                    long puntuacion = equipoDoc.getLong("Puntuacion");
                    String propietarioID = equipoDoc.getString("PropietarioId");

                    Equipo equipo = new Equipo(nombreEquipo, ligaId, (int) puntuacion, propietarioID);
                    equipos.add(equipo);
                }

                db.collection("Ligas").document(ligaId).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String nombreLiga = document.getString("NombreLiga");

                            textViewNombreLiga.setText(nombreLiga);

                            String maxEquiposString = document.getString("NumEquipos");
                            if (maxEquiposString != null && !maxEquiposString.isEmpty()) {
                                maxEquipos = Integer.parseInt(maxEquiposString);
                            } else {
                                maxEquipos = Integer.MAX_VALUE; // Set a very high limit if not specified
                            }
                        } else {
                            Toast.makeText(getContext(), "No se encontró la liga en la base de datos", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error al cargar detalles de la liga", Toast.LENGTH_SHORT).show();
                    }
                });

                Collections.sort(equipos, Comparator.comparingInt(Equipo::getPuntuacion).reversed());
                equipoAdapter.notifyDataSetChanged();
            }
        });
    }

    private void verificarYAgregarEquipo() {
        boolean isOwner = currentUserPropietarioId.equals(ownerId);
        boolean userHasTeam = hasTeam(currentUserPropietarioId, ligaId);

        if (equipos.size() >= maxEquipos) {
            Toast.makeText(getContext(), "No se pueden agregar más equipos a esta liga", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isOwner || !userHasTeam) {
            Intent intent = new Intent(getContext(), AgregarEquipoActivity.class);
            intent.putExtra("LIGA_ID", ligaId);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Ya eres propietario de un equipo en esta liga", Toast.LENGTH_SHORT).show();
        }
    }

    private void getLeagueOwner() {
        CollectionReference ligasRef = db.collection("Ligas");

        ligasRef.document(ligaId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    ownerId = document.getString("PropietarioId");
                }
            } else {
                Toast.makeText(getContext(), "Error al obtener el propietario de la liga", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean hasTeam(String userId, String leagueId) {
        CollectionReference equiposRef = db.collection("Ligas").document(leagueId).collection("Equipos");

        try {
            Task<QuerySnapshot> querySnapshotTask = equiposRef.whereEqualTo("PropietarioId", userId).get();
            QuerySnapshot querySnapshot = Tasks.await(querySnapshotTask);
            return !querySnapshot.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}