package com.example.ligamanagermobile.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ligamanagermobile.R;
import com.example.ligamanagermobile.model.Partido;
import java.util.List;

public class PartidoAdapter extends RecyclerView.Adapter<PartidoAdapter.PartidoViewHolder> {

    private List<Partido> partidos;

    public PartidoAdapter(List<Partido> partidos) {
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

        private TextView equipoLocalTextView;
        private TextView equipoVisitanteTextView;
        private TextView resultadoTextView;

        public PartidoViewHolder(@NonNull View itemView) {
            super(itemView);
            equipoLocalTextView = itemView.findViewById(R.id.equipoLocalTextView);
            equipoVisitanteTextView = itemView.findViewById(R.id.equipoVisitanteTextView);
            resultadoTextView = itemView.findViewById(R.id.resultadoTextView);
        }

        public void bind(Partido partido) {
            equipoLocalTextView.setText(partido.getEquipoLocal());
            equipoVisitanteTextView.setText(partido.getEquipoVisitante());
            resultadoTextView.setText(partido.getResultado());
        }
    }
}
