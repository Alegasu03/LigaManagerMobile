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

    public void signIn(Activity activity, String correo, String contraseña) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
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
    public void crearLiga(Activity activity, String nombreLiga, String descripcion, String numEquipos, String municipio, boolean esPublica) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userID = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Crear un mapa con los datos de la nueva liga
            Map<String, Object> nuevaLiga = new HashMap<>();
            nuevaLiga.put("NombreLiga", nombreLiga);
            nuevaLiga.put("Descripción", descripcion);
            nuevaLiga.put("NumEquipos", numEquipos);
            nuevaLiga.put("Municipio", municipio);
            nuevaLiga.put("Privacidad", esPublica ? "Pública" : "Privada");
            nuevaLiga.put("UsuarioPropietario", userID); // ID del usuario que la crea

            // Agregar la nueva liga a la colección "Ligas" en Firestore
            db.collection("Ligas")
                    .add(nuevaLiga)
                    .addOnCompleteListener(activity, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity, "Liga creada exitosamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "Error al crear la liga", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(activity, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }
    }
    public void crearEquipo(Activity activity, String nombreEquipo, String ligaId, List<Jugador> jugadores, OnEquipoCreatedListener listener) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Obtener el ID del usuario actualmente autenticado
            String propietarioId = currentUser.getUid();

            // Crear un mapa con los datos del nuevo equipo
            Map<String, Object> nuevoEquipo = new HashMap<>();
            nuevoEquipo.put("NombreEquipo", nombreEquipo);
            nuevoEquipo.put("LigaId", ligaId); // Referencia a la liga
            nuevoEquipo.put("PropietarioId", propietarioId); // ID del propietario del equipo
            nuevoEquipo.put("Puntuacion", 0); // Puntuación inicial

            // Crear una lista de mapas para los jugadores
            List<Map<String, String>> jugadoresList = new ArrayList<>();
            for (Jugador jugador : jugadores) {
                Map<String, String> jugadorMap = new HashMap<>();
                jugadorMap.put("Nombre", jugador.getNombre());
                jugadorMap.put("Apellido", jugador.getApellido());
                jugadorMap.put("Posicion", jugador.getPosicion());
                jugadoresList.add(jugadorMap);
            }
            nuevoEquipo.put("Jugadores", jugadoresList);

            // Agregar el nuevo equipo a la colección de equipos de la liga en Firestore
            db.collection("Ligas").document(ligaId).collection("Equipos")
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

