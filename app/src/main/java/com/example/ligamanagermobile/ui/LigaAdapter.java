package com.example.ligamanagermobile.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ligamanagermobile.DetallesLigaActivity;
import com.example.ligamanagermobile.R;
import com.example.ligamanagermobile.model.Liga;

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

    static class LigaViewHolder extends RecyclerView.ViewHolder {

        TextView textViewNombreLiga;
        TextView textViewNumEquipos;

        public LigaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombreLiga = itemView.findViewById(R.id.textViewNombreLiga);
            textViewNumEquipos = itemView.findViewById(R.id.textViewNumEquipos);
        }
    }
}
