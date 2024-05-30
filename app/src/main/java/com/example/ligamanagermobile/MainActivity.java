package com.example.ligamanagermobile;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.ligamanagermobile.databinding.ActivityMainBinding;
import com.example.ligamanagermobile.ui.arbitraje.ArbitrajeFragment;
import com.example.ligamanagermobile.ui.futbol.FutbolFragment;
import com.example.ligamanagermobile.ui.home.HomeFragment;
import com.example.ligamanagermobile.ui.misligas.MisLigasFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Inicializar Firestore
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Obtener referencia al BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                int id = item.getItemId();
                if (id == R.id.navigation_home) {
                    selectedFragment = new HomeFragment();
                } else if (id == R.id.navigation_arbitraje) {
                    selectedFragment = new ArbitrajeFragment();
                } else if (id == R.id.navigation_futbol) {
                    selectedFragment = new FutbolFragment();
                } else if (id == R.id.navigation_misligas) {
                    selectedFragment = new MisLigasFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }

                return true;
            }
        });

        // Set default selection
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }




    }
}
