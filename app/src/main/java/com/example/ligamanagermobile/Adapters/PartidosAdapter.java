package com.example.ligamanagermobile.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ligamanagermobile.R;
import com.example.ligamanagermobile.model.Partido;

import java.util.List;

public class PartidosAdapter extends RecyclerView.Adapter<PartidosAdapter.PartidoViewHolder> {

    private List<Partido> partidos;

    public PartidosAdapter(List<Partido> partidos) {
        this.partidos = partidos;
    }

    @NonNull
    @Override
    public PartidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_partido, parent, false);
        return new PartidoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PartidoViewHolder holder, int position) {
        Partido partido = partidos.get(position);
        holder.bind(partido);
    }

    @Override
    public int getItemCount() {
        return partidos.size();
    }

    static class PartidoViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewEquipoLocal;
        private TextView textViewEquipoVisitante;

        public PartidoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewEquipoLocal = itemView.findViewById(R.id.textViewEquipoLocal);
            textViewEquipoVisitante = itemView.findViewById(R.id.textViewEquipoVisitante);
        }

        public void bind(Partido partido) {
            textViewEquipoLocal.setText(partido.getEquipoLocal());
            textViewEquipoVisitante.setText(partido.getEquipoVisitante());
        }
    }
}

