package com.example.ligamanagermobile;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.example.ligamanagermobile.model.Jugador;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseAuthManager {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    public FirebaseAuthManager() {
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
    }

    public void signIn(Activity activity, String correo, String contraseña) {
        mAuth.signInWithEmailAndPassword(correo, contraseña)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            signInSuccess(activity);
                        } else {
                            showErrorMessage(activity, "Por favor verifica tu correo electrónico antes de iniciar sesión.");
                        }
                    } else {
                        showErrorMessage(activity, "Error de inicio de sesión. Credenciales incorrectas.");
                    }
                });
    }

    public void crearLiga(Activity activity, String nombreLiga, String descripcion, String numEquipos, String municipio, String accesibilidad) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userID = currentUser.getUid();

            Map<String, Object> nuevaLiga = new HashMap<>();
            nuevaLiga.put("NombreLiga", nombreLiga);
            nuevaLiga.put("Descripción", descripcion);
            nuevaLiga.put("NumEquipos", numEquipos);
            nuevaLiga.put("Municipio", municipio);
            nuevaLiga.put("Accesibilidad", accesibilidad);
            nuevaLiga.put("UsuarioPropietario", userID);

            mFirestore.collection("Ligas")
                    .add(nuevaLiga)
                    .addOnCompleteListener(activity, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity, "Liga creada exitosamente", Toast.LENGTH_SHORT).show();
                        } else {
                            showErrorMessage(activity, "Error al crear la liga");
                        }
                    });
        } else {
            showErrorMessage(activity, "Usuario no autenticado");
        }
    }

    public void crearEquipo(Activity activity, String nombreEquipo, String ligaId, List<Jugador> jugadores, OnEquipoCreatedListener listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String propietarioId = currentUser.getUid();

            Map<String, Object> nuevoEquipo = new HashMap<>();
            nuevoEquipo.put("NombreEquipo", nombreEquipo);
            nuevoEquipo.put("LigaId", ligaId);
            nuevoEquipo.put("PropietarioId", propietarioId);
            nuevoEquipo.put("Puntuacion", 0);

            List<Map<String, String>> jugadoresList = new ArrayList<>();
            for (Jugador jugador : jugadores) {
                Map<String, String> jugadorMap = new HashMap<>();
                jugadorMap.put("Nombre", jugador.getNombre());
                jugadorMap.put("Apellido", jugador.getApellido());
                jugadorMap.put("Posicion", jugador.getPosicion());
                jugadoresList.add(jugadorMap);
            }
            nuevoEquipo.put("Jugadores", jugadoresList);

            mFirestore.collection("Ligas").document(ligaId).collection("Equipos")
                    .add(nuevoEquipo)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            listener.onEquipoCreated();
                        } else {
                            listener.onError("Error al crear el equipo");
                        }
                    });
        } else {
            listener.onError("Usuario no autenticado");
        }
    }

    private void signInSuccess(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish(); // Cierra la actividad de inicio de sesión
    }

    private void showErrorMessage(Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    public interface OnEquipoCreatedListener {
        void onEquipoCreated();

        void onError(String errorMessage);
    }
}
