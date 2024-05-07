package com.example.ligamanagermobile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private String mUsername;
    private String mEmail;
    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtils.setupFullscreen(this);
        setContentView(R.layout.activity_register);

        // Inicializar Firestore
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Inicialización de vistas
        Spinner spinnerEdad = findViewById(R.id.spinnerAge);
        TextView textView = findViewById(R.id.textView);
        EditText editTextCorreo = findViewById(R.id.editTextCorreo);
        EditText editTextContraseña = findViewById(R.id.editTextPassword);
        EditText editTextUsuario = findViewById(R.id.editTextUsername);
        Button buttonRegister = findViewById(R.id.buttonRegister);
        CheckBox checkBoxTerms = findViewById(R.id.checkBoxTerms);

        // Configuración del Spinner
        String[] edad = getResources().getStringArray(R.array.Edad);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, edad);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEdad.setAdapter(adapter);

        // Establecer color en el texto del TextView
        String textoCompleto = "Registrate\ncon tu cuenta manager";
        Spannable spannable = new SpannableString(textoCompleto);
        int posicionSaltoLinea = textoCompleto.indexOf('\n');
        spannable.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, posicionSaltoLinea, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.WHITE), posicionSaltoLinea + 1, textoCompleto.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannable);

        // Listener del CheckBox para habilitar/deshabilitar el botón "Crear una cuenta"
        checkBoxTerms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            buttonRegister.setEnabled(isChecked && !TextUtils.isEmpty(editTextCorreo.getText().toString())
                    && !TextUtils.isEmpty(editTextContraseña.getText().toString()));
        });

        // Listener del botón "Crear una cuenta" para realizar el registro
        buttonRegister.setOnClickListener(v -> {
            String email = editTextCorreo.getText().toString();
            String password = editTextContraseña.getText().toString();
            String username = editTextUsuario.getText().toString();

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username)) {
                registerUser(email, password, username);
            } else {
                Toast.makeText(RegisterActivity.this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerUser(String email, String password, String username) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registration successful
                        FirebaseUser user = mAuth.getCurrentUser();
                        // Guardar datos en variables globales
                        mUsername = username;
                        mEmail = email;
                        mPassword = password;
                        // Send verification email
                        sendVerificationEmail(user);
                    } else {
                        // Registration failed
                        Toast.makeText(RegisterActivity.this, "Error al registrar el usuario.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendVerificationEmail(FirebaseUser user) {
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Se ha enviado un correo de verificación", Toast.LENGTH_SHORT).show();
                            // Guardar datos en Firestore después de enviar el correo de verificación
                            saveUserDataToFirestore(user);
                        } else {
                            Toast.makeText(RegisterActivity.this, "No se pudo enviar el correo de verificación", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void saveUserDataToFirestore(FirebaseUser user) {
        String userId = user.getUid();

        Map<String, Object> data = new HashMap<>();
        data.put("Usuario", mUsername);
        data.put("Correo", mEmail);
        data.put("Contraseña", mPassword);  // Agregar la contraseña a los datos a guardar

        firestore.collection("Usuarios").document(userId)
                .set(data)
                .addOnSuccessListener(documentReference -> {
                    // Document added successfully
                    Toast.makeText(RegisterActivity.this, "Usuario registrado correctamente.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Error adding document
                    Toast.makeText(RegisterActivity.this, "Error al registrar el usuario en Firestore.", Toast.LENGTH_SHORT).show();
                });
    }




}
