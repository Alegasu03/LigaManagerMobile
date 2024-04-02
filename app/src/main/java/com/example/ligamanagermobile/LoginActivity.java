package com.example.ligamanagermobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Eliminar Barra de Titulo
        ActivityUtils.setupFullscreen(this);


        setContentView(R.layout.activity_login);
        //Vinculamos los objetos
        Button btnRegister = findViewById(R.id.buttonRegister);
        TextView textView = findViewById(R.id.textView);
        String textoCompleto = "Inicia Sesión\ncon tu cuenta";
        Spannable spannable = new SpannableString(textoCompleto);
        // Obtener la posición del salto de línea
        int posicionSaltoLinea = textoCompleto.indexOf('\n');

        // Colorear la parte "Inicia Sesión" con un color
        spannable.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, posicionSaltoLinea, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Colorear la parte "con tu cuenta" con otro color
        spannable.setSpan(new ForegroundColorSpan(Color.WHITE), posicionSaltoLinea + 1, textoCompleto.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Establecer el texto en el TextView
        textView.setText(spannable);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent para abrir la nueva actividad
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        }


    }
