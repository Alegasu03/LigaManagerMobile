package com.example.ligamanagermobile;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FirebaseAuthManager {

    private FirebaseAuth firebaseAuth;

    public FirebaseAuthManager() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void signIn(Activity activity, String correo, String contraseña) {
        firebaseAuth.signInWithEmailAndPassword(correo, contraseña)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Inicio de sesión exitoso con Firebase Authentication
                            // Realizar una consulta a Firestore para verificar las credenciales
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("names")
                                    .whereEqualTo("correo", correo)
                                    .whereEqualTo("contraseña", contraseña)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                                // Usuario encontrado en Firestore con las mismas credenciales
                                                // Redirigir a la actividad principal
                                                activity.startActivity(new Intent(activity, MainActivity.class));
                                                activity.finish(); // Cierra la actividad de inicio de sesión
                                            } else {
                                                // No se encontró un usuario con las mismas credenciales en Firestore
                                                // Mostrar un mensaje de error
                                                Toast.makeText(activity, "Error de inicio de sesión. Credenciales incorrectas.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            // Si el inicio de sesión con Firebase Authentication falla, mostrar un mensaje de error
                            Toast.makeText(activity, "Error de inicio de sesión con Firebase. Verifique su correo y contraseña.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
