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
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtils.setupFullscreen(this);
        setContentView(R.layout.activity_register);

        // Inicializar Firestore
        firestore = FirebaseFirestore.getInstance();

        // Inicialización de vistas
        Spinner spinnerEdad = findViewById(R.id.spinnerAge);
        TextView textView = findViewById(R.id.textView);
        EditText editTextCorreo = findViewById(R.id.editTextCorreo);
        EditText editTextContraseña = findViewById(R.id.editTextPassword);
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
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxTerms.isChecked() && !TextUtils.isEmpty(editTextCorreo.getText().toString())
                        && !TextUtils.isEmpty(editTextContraseña.getText().toString())) {
                    // Obtener los datos del usuario
                    String correo = editTextCorreo.getText().toString().trim();
                    String contraseña = editTextContraseña.getText().toString().trim();

                    // Crear un mapa con los datos
                    Map<String, Object> data = new HashMap<>();
                    data.put("correo", correo);
                    data.put("contraseña", contraseña);

                    // Insertar los datos en Firestore
                    insertData("names", data);
                } else {
                    Toast.makeText(RegisterActivity.this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Método para insertar los datos del usuario en Firestore
    private void insertData(String collection, Map<String, Object> data) {
        // Obtener una referencia a la colección en Firestore
        DocumentReference docRef = firestore.collection(collection).document();

        // Insertar los datos en Firestore
        docRef.set(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Registro exitoso
                            Toast.makeText(RegisterActivity.this, "Registro exitoso.", Toast.LENGTH_SHORT).show();
                            // Te envía al inicio de sesión
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            // Error en el registro
                            Toast.makeText(RegisterActivity.this, "Error al registrar. Inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
