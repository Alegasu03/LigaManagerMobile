package com.example.ligamanagermobile.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ligamanagermobile.MisLigasActivity;
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
// Obtener la instancia de FirebaseAuth para obtener el usuario actual
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
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
                        // Documento no encontrado, manejar el error
                        TextView textViewUsername = root.findViewById(R.id.textViewUsername);
                        textViewUsername.setText("Usuario no encontrado");
                    }
                } else {
                    // Error al obtener el documento, manejar el error
                    TextView textViewUsername = root.findViewById(R.id.textViewUsername);
                    textViewUsername.setText("Error al obtener datos");
                }
            });
        }
            Button botonMisLigas = root.findViewById(R.id.buttonMisLigas);
            Button botonMisparticipaciones = root.findViewById(R.id.buttonMisParticipaciones);
            botonMisLigas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Iniciar MisLigasActivity cuando se hace clic en el botón de Mis Ligas
                    Intent intent = new Intent(requireContext(), MisLigasActivity.class);
                    startActivity(intent);
                }
            });
            botonMisparticipaciones.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Iniciar MisParticipaciones cuando se hace clic en el botón de Mis Participaciones
                    Intent intent = new Intent(requireContext(), MisParticipaciones.class);
                    startActivity(intent);
                }
            });

            return root;
        }

        @Override
        public void onDestroyView () {
            super.onDestroyView();
            binding = null;
        }
    }
