package com.example.ligamanagermobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ligamanagermobile.model.Jugador;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AgregarEquipoActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText editTextNombreEquipo;
    private Button buttonAgregarJugador;
    private List<Jugador> jugadores;
    private int delanterosCount = 0;
    private int centrocampistasCount = 0;
    private int defensasCount = 0;
    private int porterosCount = 0;
    private static final int MAX_JUGADORES = 11;
    private String ligaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtils.setupFullscreen(this);

        setContentView(R.layout.activity_agregar_equipo);

        db = FirebaseFirestore.getInstance();

        ligaId = getIntent().getStringExtra("LIGA_ID");
        if (ligaId == null) {
            Toast.makeText(this, "Error al obtener el ID de la liga", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        editTextNombreEquipo = findViewById(R.id.editTextNombreEquipo);
        Button buttonCrearEquipo = findViewById(R.id.buttonCrearEquipo);
        buttonAgregarJugador = findViewById(R.id.buttonAgregarJugador);

        jugadores = new ArrayList<>();

        buttonCrearEquipo.setOnClickListener(v -> guardarEquipo());

        buttonAgregarJugador.setOnClickListener(v -> {
            if (jugadores.size() >= MAX_JUGADORES) {
                Toast.makeText(AgregarEquipoActivity.this, "Ya has alcanzado el máximo de jugadores", Toast.LENGTH_SHORT).show();
                return;
            }
            mostrarDialogAgregarJugador();
        });
    }

    private void guardarEquipo() {
        String nombreEquipo = editTextNombreEquipo.getText().toString().trim();

        if (nombreEquipo.isEmpty()) {
            Toast.makeText(this, "El nombre del equipo no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        if (jugadores.isEmpty()) {
            Toast.makeText(this, "El equipo debe tener al menos un jugador", Toast.LENGTH_SHORT).show();
            return;
        }
        if (jugadores.size() < 11) {
            Toast.makeText(this, "El equipo debe tener al menos 11 jugadores", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuthManager authManager = new FirebaseAuthManager(); // Instancia de FirebaseAuthManager
        authManager.crearEquipo(this, nombreEquipo, ligaId, jugadores, new FirebaseAuthManager.OnEquipoCreatedListener() {
            @Override
            public void onEquipoCreated() {
                Toast.makeText(AgregarEquipoActivity.this, "Equipo agregado correctamente", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(AgregarEquipoActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void mostrarDialogAgregarJugador() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_agregar_jugador, null);
        builder.setView(dialogView);

        EditText editTextNombre = dialogView.findViewById(R.id.editTextNombre);
        EditText editTextApellido = dialogView.findViewById(R.id.editTextApellido);
        RadioGroup radioGroupPosiciones = dialogView.findViewById(R.id.radioGroupPosiciones);
        Button buttonAgregarJugador = dialogView.findViewById(R.id.buttonAgregarJugador);

        AlertDialog dialog = builder.create();

        buttonAgregarJugador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = editTextNombre.getText().toString().trim();
                String apellido = editTextApellido.getText().toString().trim();

                if (nombre.isEmpty() || apellido.isEmpty()) {
                    Toast.makeText(AgregarEquipoActivity.this, "Debes ingresar nombre y apellido del jugador", Toast.LENGTH_SHORT).show();
                    return;
                }

                int radioButtonId = radioGroupPosiciones.getCheckedRadioButtonId();
                String posicion = "";
                if (radioButtonId == R.id.radioButtonDelantero) {
                    if (delanterosCount >= 3) {
                        Toast.makeText(AgregarEquipoActivity.this, "No puedes agregar más delanteros", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    posicion = "Delantero";
                } else if (radioButtonId == R.id.radioButtonCentrocampista) {
                    if (centrocampistasCount >= 4) {
                        Toast.makeText(AgregarEquipoActivity.this, "No puedes agregar más centrocampistas", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    posicion = "Centrocampista";
                } else if (radioButtonId == R.id.radioButtonDefensa) {
                    if (defensasCount >= 3) {
                        Toast.makeText(AgregarEquipoActivity.this, "No puedes agregar más defensas", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    posicion = "Defensa";
                } else if (radioButtonId == R.id.radioButtonPortero) {
                    if (porterosCount >= 1) {
                        Toast.makeText(AgregarEquipoActivity.this, "No puedes agregar más porteros", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    posicion = "Portero";
                }

                agregarJugador(nombre, apellido, posicion);
                dialog.dismiss();

                // Si se alcanza el número máximo de jugadores (11), se deshabilita el botón de agregar jugador
                if (jugadores.size() == 11) {
                    buttonAgregarJugador.setEnabled(false);
                }
            }
        });

        dialog.show();
    }

    private void agregarJugador(String nombre, String apellido, String posicion) {
        Jugador jugador = new Jugador(nombre, apellido, posicion);
        jugadores.add(jugador);

        ImageView imageView = null;

        switch (posicion) {
            case "Delantero":
                imageView = getDelanteroImageView(delanterosCount);
                delanterosCount++;
                break;
            case "Centrocampista":
                imageView = getCentrocampistaImageView(centrocampistasCount);
                centrocampistasCount++;
                break;
            case "Defensa":
                imageView = getDefensaImageView(defensasCount);
                defensasCount++;
                break;
            case "Portero":
                imageView = findViewById(R.id.goalkeeper);
                porterosCount++;
                break;
        }

        if (imageView != null) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.player_icon); // Asegúrate de tener este recurso
        }
    }

    private ImageView getDelanteroImageView(int index) {
        switch (index) {
            case 0: return findViewById(R.id.forward1);
            case 1: return findViewById(R.id.forward2);
            case 2: return findViewById(R.id.forward3);
        }
        return null;
    }

    private ImageView getCentrocampistaImageView(int index) {
        switch (index) {
            case 0: return findViewById(R.id.midfield1);
            case 1: return findViewById(R.id.midfield2);
            case 2: return findViewById(R.id.midfield3);
            case 3: return findViewById(R.id.midfield4);
        }
        return null;
    }

    private ImageView getDefensaImageView(int index) {
        switch (index) {
            case 0: return findViewById(R.id.defense1);
            case 1: return findViewById(R.id.defense2);
            case 2: return findViewById(R.id.defense3);
        }
        return null;
    }
}
