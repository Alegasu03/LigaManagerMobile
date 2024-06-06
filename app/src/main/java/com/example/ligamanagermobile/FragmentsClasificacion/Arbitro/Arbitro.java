package com.example.ligamanagermobile.FragmentsClasificacion.Arbitro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ligamanagermobile.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Arbitro extends Fragment {
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Button btnArbitrarLiga;
    private String ligaId;
    private String userId;

    public static Arbitro newInstance(String ligaId) {
        Arbitro fragment = new Arbitro();
        Bundle args = new Bundle();
        args.putString("ligaId", ligaId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_arbitro, container, false);

        btnArbitrarLiga = root.findViewById(R.id.btnArbitrarLiga);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();

        if (getArguments() != null) {
            ligaId = getArguments().getString("ligaId");
        }

        verificarEstadoArbitro();

        btnArbitrarLiga.setOnClickListener(v -> verificarYComprobarPropietario(ligaId));

        return root;
    }

    private void verificarEstadoArbitro() {
        DocumentReference ligaRef = db.collection("Ligas").document(ligaId);
        ligaRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("arbitro") && documentSnapshot.getString("arbitro") != null) {
                    btnArbitrarLiga.setEnabled(false);
                    Toast.makeText(getContext(), "La liga ya tiene un árbitro", Toast.LENGTH_SHORT).show();
                } else {
                    btnArbitrarLiga.setEnabled(true);
                }
            } else {
                Toast.makeText(getContext(), "La liga no existe", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Error al verificar la liga", Toast.LENGTH_SHORT).show();
        });
    }

    private void verificarYComprobarPropietario(String ligaId) {
        DocumentReference ligaRef = db.collection("Ligas").document(ligaId);
        ligaRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (!documentSnapshot.contains("arbitro") || documentSnapshot.getString("arbitro") == null) {
                    // Verificar si el usuario actual es el propietario de la liga (UsuarioPropietario)
                    if (!documentSnapshot.getString("UsuarioPropietario").equals(userId)) {
                        asignarArbitroALiga(ligaRef);
                    } else {
                        Toast.makeText(getContext(), "El Propietario de la liga no puede ser árbitro", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "La liga ya tiene un árbitro", Toast.LENGTH_SHORT).show();
                    btnArbitrarLiga.setEnabled(false);
                }
            } else {
                Toast.makeText(getContext(), "La liga no existe", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Error al verificar la liga", Toast.LENGTH_SHORT).show();
        });
    }

    private void asignarArbitroALiga(DocumentReference ligaRef) {
        ligaRef.update("arbitro", userId)
                .addOnSuccessListener(aVoid -> {
                    btnArbitrarLiga.setEnabled(false);
                    Toast.makeText(getContext(), "Te has asignado como árbitro de la liga", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al asignar árbitro", Toast.LENGTH_SHORT).show();
                });
    }
}
