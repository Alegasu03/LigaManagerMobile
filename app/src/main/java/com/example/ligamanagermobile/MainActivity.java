package com.example.ligamanagermobile;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ligamanagermobile.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private TextView textViewUsername;
    private TextView textViewEmail;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Inicializar Firestore
        firestore = FirebaseFirestore.getInstance();

        // Obtener referencia al NavigationView y al TextView del header
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        textViewUsername = headerView.findViewById(R.id.textViewUsername);
        textViewEmail= headerView.findViewById(R.id.textViewEmail);
        // Obtener el usuario actualmente autenticado
        // Dentro del método onCreate de tu MainActivity.java

// Obtener el usuario actualmente autenticado
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userRef = firestore.collection("Usuarios").document(userId);
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Obtener el nombre de usuario y correo electrónico del documento
                        String username = document.getString("Usuario");
                        String email = currentUser.getEmail();
                        if (username != null && !username.isEmpty()) {
                            textViewUsername.setText(username);
                        } else {
                            textViewUsername.setText("Nombre de usuario no disponible");
                        }
                        if (email != null && !email.isEmpty()) {
                            textViewEmail.setText(email);
                        } else {
                            textViewEmail.setText("Correo electrónico no disponible");
                        }
                    } else {
                        textViewUsername.setText("Documento de usuario no encontrado");
                        textViewEmail.setText("Correo electrónico no disponible");
                    }
                } else {
                    textViewUsername.setText("Error al obtener datos: " + task.getException());
                    textViewEmail.setText("Correo electrónico no disponible");
                }
            });
        } else {
            textViewUsername.setText("Usuario no autenticado");
            textViewEmail.setText("Correo electrónico no disponible");
        }


        DrawerLayout drawer = binding.drawerLayout;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}


