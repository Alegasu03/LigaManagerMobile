package com.example.ligamanagermobile.ui.arbitraje;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ligamanagermobile.MisArbitrajesActivity;
import com.example.ligamanagermobile.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ArbitrajeFragment extends Fragment {
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Button btnSolicitarArbitro;
    private Button btnMisArbitrajes;

    public static ArbitrajeFragment newInstance() {
        return new ArbitrajeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_arbitraje, container, false);

        btnSolicitarArbitro = root.findViewById(R.id.btnArbitro);
        btnMisArbitrajes = root.findViewById(R.id.btnMisArbitrajes);

        if (btnSolicitarArbitro == null || btnMisArbitrajes == null) {
            throw new NullPointerException("Uno de los botones es null. Verifica los IDs en el layout.");
        }

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        verificarEstadoArbitro();

        btnSolicitarArbitro.setOnClickListener(v -> mostrarDialogo());
        btnMisArbitrajes.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MisArbitrajesActivity.class);
            startActivity(intent);
        });

        return root;
    }

    private void verificarEstadoArbitro() {
        String userId = auth.getCurrentUser().getUid();

        db.collection("Usuarios").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.getBoolean("EsArbitro") != null) {
                boolean esArbitro = documentSnapshot.getBoolean("EsArbitro");
                if (esArbitro) {
                    btnSolicitarArbitro.setEnabled(false);
                    Toast.makeText(getContext(), "Ya eres árbitro", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Error al verificar estado de árbitro", Toast.LENGTH_SHORT).show());
    }

    private void mostrarDialogo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Ingrese su contraseña");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Aceptar", (dialog, which) -> verificarContraseña(input.getText().toString()));
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void verificarContraseña(String contraseña) {
        String correo = auth.getCurrentUser().getEmail();

        auth.signInWithEmailAndPassword(correo, contraseña).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                actualizarUsuario();
            } else {
                Toast.makeText(getContext(), "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarUsuario() {
        String userId = auth.getCurrentUser().getUid();

        db.collection("Usuarios").document(userId)
                .update("EsArbitro", true)
                .addOnSuccessListener(aVoid -> {
                    btnSolicitarArbitro.setEnabled(false);
                    Toast.makeText(getContext(), "Ahora eres árbitro", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show());
    }
}
