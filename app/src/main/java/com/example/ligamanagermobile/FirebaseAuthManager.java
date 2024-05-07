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
import com.google.firebase.firestore.QuerySnapshot;

public class FirebaseAuthManager {

    public void signIn(Activity activity, String correo, String contraseña) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(correo, contraseña)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            // Usuario autenticado y correo verificado
                            signInSuccess(activity);
                        } else {
                            // Usuario autenticado pero correo no verificado
                            Toast.makeText(activity, "Por favor verifica tu correo electrónico antes de iniciar sesión.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Fallo al iniciar sesión usando FirebaseAuth, verificar en Firestore en caso necesario
                        // Aquí podrías realizar una consulta a Firestore si lo prefieres
                        Toast.makeText(activity, "Error de inicio de sesión. Credenciales incorrectas.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void signInSuccess(Activity activity) {
        // Redirigir a la actividad principal
        activity.startActivity(new Intent(activity, MainActivity.class));
        activity.finish(); // Cierra la actividad de inicio de sesión
    }
}
