package com.example.ligamanagermobile.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ligamanagermobile.R;
import com.example.ligamanagermobile.model.Equipo;

import java.util.List;

public class EquipoAdapter extends RecyclerView.Adapter<EquipoAdapter.EquipoViewHolder> {

    private List<Equipo> equipos;

    public EquipoAdapter(List<Equipo> equipos) {
        this.equipos = equipos;
    }

    @NonNull
    @Override
    public EquipoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_equipo, parent, false);
        return new EquipoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EquipoViewHolder holder, int position) {
        Equipo equipo = equipos.get(position);
        holder.bind(equipo);
    }

    @Override
    public int getItemCount() {
        return equipos.size();
    }

    static class EquipoViewHolder extends RecyclerView.ViewHolder {

        private TextView nombreTextView;
        private TextView puntosTextView;

        public EquipoViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.nombreTextView);
            puntosTextView = itemView.findViewById(R.id.puntosTextView);
        }

        public void bind(Equipo equipo) {
            nombreTextView.setText(equipo.getNombreEquipo());
            puntosTextView.setText(String.valueOf(equipo.getPuntuacion()));
        }
    }
}
