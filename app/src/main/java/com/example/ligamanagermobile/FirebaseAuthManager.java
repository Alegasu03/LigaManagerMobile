package com.example.ligamanagermobile;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
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

    private void signInSuccess(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish(); // Cierra la actividad de inicio de sesión
    }

    private void showErrorMessage(Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }
}

