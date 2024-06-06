package com.example.ligamanagermobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.ligamanagermobile.FragmentsClasificacion.Arbitro.Arbitro;
import com.example.ligamanagermobile.FragmentsClasificacion.Clasificacion.Clasificacion;
import com.example.ligamanagermobile.FragmentsClasificacion.Partidos.Partidos;
import com.example.ligamanagermobile.model.Equipo;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DetallesLigaActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String ligaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtils.setupFullscreen(this);
        setContentView(R.layout.activity_detalles_liga);

        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        // Inicializar Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Obtener instancia de autenticación de Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // ID del propietario actual
            String currentUserPropietarioId = currentUser.getUid(); // Obtener el ID del usuario actual
        }

        List<Equipo> equipos = new ArrayList<>();

        Intent intent = getIntent();
        if (intent != null) {
            ligaId = intent.getStringExtra("ligaId");
            if (ligaId != null) {
                setupViewPager(viewPager);
                tabLayout.setupWithViewPager(viewPager);
                loadLigaDetails(ligaId);
            } else {
                Toast.makeText(this, "Error al obtener la ID de la liga", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Error al obtener la ID de la liga", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return Clasificacion.newInstance(ligaId);
                    case 1:
                        return Arbitro.newInstance(ligaId);
                    case 2:
                        return Partidos.newInstance(ligaId);
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 3; // Número total de fragmentos
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "Clasificación";
                    case 1:
                        return "Árbitro";
                    case 2:
                        return "Partidos";
                    default:
                        return null;
                }
            }
        };

        viewPager.setAdapter(adapter);
    }

    private void loadLigaDetails(String ligaId) {
        CollectionReference ligasRef = db.collection("Ligas");

        ligasRef.document(ligaId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    String nombreLiga = document.getString("NombreLiga");
                    // Puedes hacer algo con el nombre de la liga si lo necesitas
                } else {
                    Toast.makeText(DetallesLigaActivity.this, "No se encontró la liga en la base de datos", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(DetallesLigaActivity.this, "Error al cargar detalles de la liga", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
