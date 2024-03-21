package com.example.ligamanagermobile;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Eliminar Barra de Titulo
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();


        setContentView(R.layout.activity_login);
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

    }

}