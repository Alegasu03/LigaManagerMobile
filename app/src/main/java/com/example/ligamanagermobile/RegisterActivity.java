package com.example.ligamanagermobile;

import androidx.appcompat.app.AppCompatActivity;

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

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtils.setupFullscreen(this);
        setContentView(R.layout.activity_register);

        // Inicialización de vistas
        Spinner spinnerEdad = findViewById(R.id.spinnerAge);
        TextView textView = findViewById(R.id.textView);
        EditText editTextUsername = findViewById(R.id.editTextUsername);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        EditText editTextCorreo = findViewById(R.id.editTextCorreo);
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
            buttonRegister.setEnabled(isChecked && !TextUtils.isEmpty(editTextUsername.getText().toString())
                    && !TextUtils.isEmpty(editTextPassword.getText().toString())
                    && !TextUtils.isEmpty(editTextCorreo.getText().toString()));
        });

        // Listener del botón "Crear una cuenta" para realizar la acción de registro
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxTerms.isChecked() && !TextUtils.isEmpty(editTextUsername.getText().toString())
                        && !TextUtils.isEmpty(editTextPassword.getText().toString())
                        && !TextUtils.isEmpty(editTextCorreo.getText().toString())) {
                }
            }
        });
    }
}
