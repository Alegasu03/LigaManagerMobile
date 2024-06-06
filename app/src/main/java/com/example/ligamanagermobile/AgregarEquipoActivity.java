package com.example.ligamanagermobile;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ligamanagermobile.model.Jugador;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AgregarEquipoActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText editTextNombreEquipo;
    private ImageButton buttonAgregarJugador;
    private List<Jugador> jugadores;
    private int delanterosCount = 0;
    private int centrocampistasCount = 0;
    private int defensasCount = 0;
    private int porterosCount = 0;
    private int MAX_JUGADORES = 11;
    private String ligaId;
    private int MAX_DELANTEROS = 3;
    private int MAX_CENTROCAMPISTAS = 4;
    private int MAX_DEFENSAS = 3;
    private int MAX_PORTEROS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtils.setupFullscreen(this);

        setContentView(R.layout.activity_agregar_equipo);

        db = FirebaseFirestore.getInstance();

        // Obtener ID de la liga y accesibilidad desde el intent
        ligaId = getIntent().getStringExtra("LIGA_ID");
        if (ligaId == null) {
            Toast.makeText(this, "Error al obtener el ID de la liga", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        obtenerAccesibilidadLiga(ligaId); // Llama a esta función para cargar la accesibilidad y ajustar los límites

        editTextNombreEquipo = findViewById(R.id.editTextNombreEquipo);
        ImageButton buttonCrearEquipo = findViewById(R.id.buttonCrearEquipo);
        buttonCrearEquipo.setBackground(null);
        buttonAgregarJugador = findViewById(R.id.buttonAgregarJugador);
     // Desactivar el botón hasta que la accesibilidad sea cargada

        jugadores = new ArrayList<>();

        buttonCrearEquipo.setOnClickListener(v -> guardarEquipo());
        buttonAgregarJugador.setOnClickListener(v -> mostrarDialogAgregarJugador());
        buttonAgregarJugador.setBackground(null);

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

        if (jugadores.size() < MAX_JUGADORES) {
            Toast.makeText(this, "El equipo debe tener al menos " + MAX_JUGADORES + " jugadores", Toast.LENGTH_SHORT).show();
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
        ImageButton buttonAgregarJugador = dialogView.findViewById(R.id.buttonAgregarJugador);
        buttonAgregarJugador.setBackground(null);
        AlertDialog dialog = builder.create();

        buttonAgregarJugador.setOnClickListener(v -> {
            String nombre = editTextNombre.getText().toString().trim();
            String apellido = editTextApellido.getText().toString().trim();

            if (nombre.isEmpty() || apellido.isEmpty()) {
                Toast.makeText(AgregarEquipoActivity.this, "Debes ingresar nombre y apellido del jugador", Toast.LENGTH_SHORT).show();
                return;
            }

            int radioButtonId = radioGroupPosiciones.getCheckedRadioButtonId();
            String posicion = "";
            if (radioButtonId == R.id.radioButtonDelantero) {
                if (delanterosCount >= MAX_DELANTEROS) {
                    Toast.makeText(AgregarEquipoActivity.this, "No puedes agregar más delanteros", Toast.LENGTH_SHORT).show();
                    return;
                }
                posicion = "Delantero";
            } else if (radioButtonId == R.id.radioButtonCentrocampista) {
                if (centrocampistasCount >= MAX_CENTROCAMPISTAS) {
                    Toast.makeText(AgregarEquipoActivity.this, "No puedes agregar más centrocampistas", Toast.LENGTH_SHORT).show();
                    return;
                }
                posicion = "Centrocampista";
            } else if (radioButtonId == R.id.radioButtonDefensa) {
                if (defensasCount >= MAX_DEFENSAS) {
                    Toast.makeText(AgregarEquipoActivity.this, "No puedes agregar más defensas", Toast.LENGTH_SHORT).show();
                    return;
                }
                posicion = "Defensa";
            } else if (radioButtonId == R.id.radioButtonPortero) {
                if (porterosCount >= MAX_PORTEROS) {
                    Toast.makeText(AgregarEquipoActivity.this, "No puedes agregar más porteros", Toast.LENGTH_SHORT).show();
                    return;
                }
                posicion = "Portero";
            }

            agregarJugador(nombre, apellido, posicion);
            dialog.dismiss();

            // Si se alcanza el número máximo de jugadores, se deshabilita el botón de agregar jugador
            if (jugadores.size() == MAX_JUGADORES) {
                buttonAgregarJugador.setEnabled(false);
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
            case 0:
                return findViewById(R.id.forward1);
            case 1:
                return findViewById(R.id.forward2);
            case 2:
                return findViewById(R.id.forward3);
        }
        return null;
    }

    private ImageView getCentrocampistaImageView(int index) {
        switch (index) {
            case 0:
                return findViewById(R.id.midfield1);
            case 1:
                return findViewById(R.id.midfield2);
            case 2:
                return findViewById(R.id.midfield3);
            case 3:
                return findViewById(R.id.midfield4);
        }
        return null;
    }


    private ImageView getDefensaImageView(int index) {
        switch (index) {
            case 0:
                return findViewById(R.id.defense1);
            case 1:
                return findViewById(R.id.defense2);
            case 2:
                return findViewById(R.id.defense3);
        }
        return null;
    }

    private void obtenerAccesibilidadLiga(String ligaId) {
        Log.d(TAG, "Obteniendo accesibilidad para la liga ID: " + ligaId);

        // Obtener la accesibilidad para el documento con el ID proporcionado
        db.collection("Ligas").document(ligaId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String accesibilidad = document.getString("Accesibilidad");
                            Log.d(TAG, "Accesibilidad obtenida: " + accesibilidad);


                            // Aplicar ajustes de accesibilidad
                            if ("Discapacidad".equals(accesibilidad)) {
                                MAX_JUGADORES = 7;
                                MAX_DELANTEROS = 2;
                                MAX_CENTROCAMPISTAS = 2;
                                MAX_DEFENSAS = 2;
                                MAX_PORTEROS = 1;
                            } else {
                                MAX_JUGADORES = 11;
                                MAX_DELANTEROS = 3;
                                MAX_CENTROCAMPISTAS = 4;
                                MAX_DEFENSAS = 3;
                                MAX_PORTEROS = 1;
                            }
                        } else {
                            Log.d(TAG, "El documento no existe");
                            Toast.makeText(this, "Error al obtener la accesibilidad de la liga", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Error al obtener el documento: ", task.getException());
                        Toast.makeText(this, "Error al obtener la accesibilidad de la liga", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}



