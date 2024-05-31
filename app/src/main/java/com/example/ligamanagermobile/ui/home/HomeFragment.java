package com.example.ligamanagermobile.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import com.example.ligamanagermobile.AyudaActivity;
import com.example.ligamanagermobile.LoginActivity;
import com.example.ligamanagermobile.MisParticipaciones;
import com.example.ligamanagermobile.R;
import com.example.ligamanagermobile.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflar el diseño del fragmento y obtener la vista raíz
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Configurar la interfaz de usuario
        setupUI(root);

        return root;
    }

    // Método para configurar la interfaz de usuario
    private void setupUI(View root) {
        // Obtener el usuario actual
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Obtener el ID del usuario actual
            String userId = currentUser.getUid();

            // Obtener referencia a Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Buscar el documento del usuario por ID en la colección "Usuarios"
            DocumentReference userRef = db.collection("Usuarios").document(userId);
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Obtener el nombre de usuario desde el campo "Usuario"
                        String username = document.getString("Usuario");
                        // Mostrar el nombre de usuario en el TextView
                        TextView textViewUsername = root.findViewById(R.id.textViewUsername);
                        textViewUsername.setText(username);
                    } else {
                        handleFirestoreError(root, "Usuario no encontrado");
                    }
                } else {
                    handleFirestoreError(root, "Error al obtener datos");
                }
            });
        }

        // Configurar el botón de Mis Participaciones
        AppCompatImageButton botonMisparticipaciones = root.findViewById(R.id.buttonMisParticipaciones);
        botonMisparticipaciones.setBackground(null);
        botonMisparticipaciones.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), MisParticipaciones.class);
            startActivity(intent);
        });

        // Configurar el botón de cerrar sesión
        AppCompatImageButton botonCerrarSesion = root.findViewById(R.id.buttonCerrarSesion);
        botonCerrarSesion.setBackground(null);
        botonCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        AppCompatImageButton botonAyuda = root.findViewById(R.id.buttonAyuda);
        botonAyuda.setBackground(null);
        botonAyuda.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AyudaActivity.class);
            startActivity(intent);
        });
    }

    // Método para manejar errores de Firestore
    private void handleFirestoreError(View root, String errorMessage) {
        TextView textViewUsername = root.findViewById(R.id.textViewUsername);
        textViewUsername.setText(errorMessage);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
