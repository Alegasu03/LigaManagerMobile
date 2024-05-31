package com.example.ligamanagermobile.Adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ligamanagermobile.DetallesLigaActivity;
import com.example.ligamanagermobile.R;
import com.example.ligamanagermobile.model.Liga;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class LigaAdapter extends RecyclerView.Adapter<LigaAdapter.LigaViewHolder> {
    private List<Liga> ligas;
    private Context context;

    public LigaAdapter(List<Liga> ligas, Context context) {
        this.ligas = ligas;
        this.context = context;
    }

    @NonNull
    @Override
    public LigaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_liga, parent, false);
        return new LigaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LigaViewHolder holder, int position) {
        Liga liga = ligas.get(position);
        holder.textViewNombreLiga.setText(liga.getNombre());
        holder.textViewNumEquipos.setText(liga.getNumEquipos() + "/" + liga.getMaxEquipos());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetallesLigaActivity.class);
                intent.putExtra("ligaId", liga.getId()); // Pasar el ID de la liga
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ligas.size();
    }

    public class LigaViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout cardLayout;
        TextView textViewNombreLiga;
        TextView textViewNumEquipos;
        ImageView imageViewInfo; // Can be removed if not used
        TextView textViewDetalles; // TextView for showing details

        public LigaViewHolder(@NonNull View itemView) {
            super(itemView);
            cardLayout = itemView.findViewById(R.id.cardLayout);
            textViewNombreLiga = itemView.findViewById(R.id.textViewNombreLiga);
            textViewNumEquipos = itemView.findViewById(R.id.textViewNumEquipos);
            imageViewInfo = itemView.findViewById(R.id.imageViewInfo); // Can be removed if not used
            textViewDetalles = itemView.findViewById(R.id.textViewDetalles); // Initialize TextView for details

            imageViewInfo.setOnClickListener(v -> {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) imageViewInfo.getLayoutParams();
                layoutParams.startToEnd = textViewNombreLiga.getId(); // Establecer la restricción de inicio al final del textViewNombreLiga
                layoutParams.topToTop = textViewNombreLiga.getId(); // Establecer la restricción superior al textViewNombreLiga
                layoutParams.bottomToBottom = textViewNombreLiga.getId(); // Establecer la restricción inferior al textViewNombreLiga
                imageViewInfo.setLayoutParams(layoutParams);
                Liga ligaClicked = ligas.get(getAdapterPosition());
                if (ligaClicked != null) {
                    if (textViewDetalles.getVisibility() == View.VISIBLE) {
                        textViewDetalles.setVisibility(View.GONE);
                    } else {
                        mostrarDetallesLiga(ligaClicked);
                    }
                }
            });
        }

        public void bind(Liga liga) {
            textViewNombreLiga.setText(liga.getNombre());
            textViewNumEquipos.setText(String.valueOf(liga.getNumEquipos()));
            textViewDetalles.setVisibility(View.GONE); // Hide details initially
        }

        private void mostrarDetallesLiga(Liga liga) {
            // Access Liga information
            String ligaId = liga.getId();

            // Access Firestore and retrieve details
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference ligaRef = db.collection("Ligas").document(ligaId);
            ligaRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // Extract additional details
                    String descripcion = documentSnapshot.getString("Descripción");
                    String accesibilidad = documentSnapshot.getString("Accesibilidad");
                    String usuarioPropietarioId = documentSnapshot.getString("UsuarioPropietario");

                    // Retrieve user's name from Usuarios collection
                    DocumentReference userRef = db.collection("Usuarios").document(usuarioPropietarioId);
                    userRef.get().addOnSuccessListener(userDocument -> {
                        if (userDocument.exists()) {
                            String usuarioPropietarioNombre = userDocument.getString("Usuario");
                            // Display details in the TextView for details
                            String additionalInfo = "Descripción: " + descripcion + "\nAccesibilidad: " + accesibilidad + "\nPropietario: " + usuarioPropietarioNombre;
                            textViewDetalles.setText(additionalInfo);
                            textViewDetalles.setVisibility(View.VISIBLE); // Show details

                            // Ocultar el TextView de detalles nuevamente
                        } else {
                            Log.d(TAG, "El usuario propietario no existe");
                            Toast.makeText(context, "El usuario propietario no existe", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "Error al obtener el usuario propietario", e);
                        Toast.makeText(context, "Error al obtener el usuario propietario", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Log.d(TAG, "El documento de la liga no existe");
                    Toast.makeText(context, "La información adicional no está disponible", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Error al obtener la información adicional de la liga", e);
                Toast.makeText(context, "Error al obtener la información adicional de la liga", Toast.LENGTH_SHORT).show();
            });
        }

    }
}
