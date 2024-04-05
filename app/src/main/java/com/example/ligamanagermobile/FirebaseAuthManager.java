package com.example.ligamanagermobile;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FirebaseAuthManager {

    public void signIn(Activity activity, String correo, String contraseña) {
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
                            // Llamar al método signInSuccess para continuar con el flujo de la aplicación
                            signInSuccess(activity);
                        } else {
                            // No se encontró un usuario con las mismas credenciales en Firestore
                            // Mostrar un mensaje de error
                            Toast.makeText(activity, "Error de inicio de sesión. Credenciales incorrectas.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signInSuccess(Activity activity) {
        // Redirigir a la actividad principal
        activity.startActivity(new Intent(activity, MainActivity.class));
        activity.finish(); // Cierra la actividad de inicio de sesión
    }
}
