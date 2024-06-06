package com.example.ligamanagermobile.ui.misligas;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ligamanagermobile.R;
import com.example.ligamanagermobile.model.Liga;
import com.example.ligamanagermobile.Adapters.LigaAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MisLigasFragment extends Fragment {

    private static final String TAG = "MisLigasFragment";

    private LigaAdapter ligaAdapter;
    private List<Liga> ligas;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_misligas, container, false);

        RecyclerView recyclerViewLigas = root.findViewById(R.id.recyclerViewLigas);
        recyclerViewLigas.setLayoutManager(new LinearLayoutManager(getContext()));
        ligas = new ArrayList<>();
        ligaAdapter = new LigaAdapter(ligas, getContext());
        recyclerViewLigas.setAdapter(ligaAdapter);

        // Configurar ItemTouchHelper
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAbsoluteAdapterPosition();
                int toPosition = target.getAbsoluteAdapterPosition();
                ligaAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAbsoluteAdapterPosition();
                Liga ligaToDelete = ligas.get(position);

                // Mostrar un cuadro de diálogo de confirmación
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setMessage("¿Estás seguro de que deseas eliminar esta liga?")
                        .setTitle("Eliminar Liga");

                // Agregar botón de confirmación
                builder.setPositiveButton("Eliminar", (dialog, which) -> {
                    // Eliminar la liga
                    ligas.remove(position);
                    ligaAdapter.notifyItemRemoved(position);

                    // Eliminar la liga de Firestore
                    deleteLiga(ligaToDelete.getId());
                });

                // Agregar botón de cancelar
                builder.setNegativeButton("Cancelar", (dialog, which) -> {
                    // Cancelar la acción de eliminación, no hacer nada
                    ligaAdapter.notifyItemChanged(position); // Para refrescar el estado visual del elemento
                });

                // Mostrar el cuadro de diálogo
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        });

        itemTouchHelper.attachToRecyclerView(recyclerViewLigas);

        // Cargar las ligas desde Firestore
        loadLigasFromFirestore();

        return root;
    }

    private void loadLigasFromFirestore() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference ligasRef = db.collection("Ligas");

            ligasRef.whereEqualTo("UsuarioPropietario", userId)
                    .addSnapshotListener((snapshots, e) -> {
                        if (e != null) {
                            Log.w(TAG, "Error al obtener ligas", e);
                            return;
                        }

                        ligas.clear();
                        if (snapshots != null) {
                            for (DocumentSnapshot doc : snapshots.getDocuments()) {
                                String nombre = doc.getString("NombreLiga");
                                String maxEquiposString = doc.getString("NumEquipos");
                                int maxEquipos;
                                String idLiga = doc.getId();

                                if (maxEquiposString != null && !maxEquiposString.isEmpty()) {
                                    maxEquipos = Integer.parseInt(maxEquiposString);
                                } else {
                                    maxEquipos = 0;
                                }

                                CollectionReference equiposRef = db.collection("Ligas").document(idLiga).collection("Equipos");
                                equiposRef.get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        int numEquipos = task.getResult().size();

                                        // Crear objeto Liga y agregarlo a la lista
                                        Liga liga = new Liga(nombre, numEquipos, maxEquipos);
                                        liga.setId(idLiga);
                                        ligas.add(liga);
                                        ligaAdapter.notifyDataSetChanged();
                                    } else {
                                        Log.d(TAG, "Error al obtener equipos para la liga " + nombre, task.getException());
                                    }
                                });
                            }
                        }
                    });
        }
    }

    private void deleteLiga(String ligaId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Ligas").document(ligaId).delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Liga eliminada de Firestore"))
                .addOnFailureListener(e -> Log.e(TAG, "Error al eliminar la liga de Firestore", e));
    }
}
