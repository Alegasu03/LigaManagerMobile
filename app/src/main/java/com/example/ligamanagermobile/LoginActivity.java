package com.example.ligamanagermobile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText correoText;
    private EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Eliminar Barra de Título
        ActivityUtils.setupFullscreen(this);

        // Inicializar vistas
        initViews();

        // Establecer texto coloreado en el TextView
        TextView textView = findViewById(R.id.textView);
        String textoCompleto = "Inicia Sesión\ncon tu cuenta";
        Spannable spannable = new SpannableString(textoCompleto);
        int posicionSaltoLinea = textoCompleto.indexOf('\n');
        spannable.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, posicionSaltoLinea, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.WHITE), posicionSaltoLinea + 1, textoCompleto.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannable);
    }

    private void initViews() {
        correoText = findViewById(R.id.editTextUsername);
        passwordText = findViewById(R.id.editTextPassword);
        Button btnRegister = findViewById(R.id.buttonRegister);
        Button btnLogin = findViewById(R.id.buttonLogin);
        Button btnAdmin = findViewById(R.id.ButtonAdmin);

        btnRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        View.OnClickListener loginClickListener = v -> {
            String correo = correoText.getText().toString().trim();
            String contraseña = passwordText.getText().toString().trim();
            if (!TextUtils.isEmpty(correo) && !TextUtils.isEmpty(contraseña)) {
                signIn(correo, contraseña);
            } else {
                Toast.makeText(LoginActivity.this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
            }
        };

        btnLogin.setOnClickListener(loginClickListener);

        // Configurar el clic del botón de administrador con el correo y contraseña predefinidos
        btnAdmin.setOnClickListener(v -> signIn("alegasu03@gmail.com", "123456"));
    }


    private void signIn(String correo, String contraseña) {
        FirebaseAuthManager firebaseAuthManager = new FirebaseAuthManager();
        firebaseAuthManager.signIn(LoginActivity.this, correo, contraseña);
        // Aquí puedes agregar el código para cerrar la actividad de inicio de sesión si el inicio de sesión es exitoso
    }
}
