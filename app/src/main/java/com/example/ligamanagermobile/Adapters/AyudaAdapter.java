package com.example.ligamanagermobile.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ligamanagermobile.R;
import com.example.ligamanagermobile.model.Ayuda;

import java.util.List;

public class AyudaAdapter extends RecyclerView.Adapter<AyudaAdapter.AyudaViewHolder> {

    private List<Ayuda> ayudaList;

    public AyudaAdapter(List<Ayuda> ayudaList) {
        this.ayudaList = ayudaList;
    }


    @NonNull
    @Override
    public AyudaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ayuda_item, parent, false);
        return new AyudaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AyudaViewHolder holder, int position) {
        Ayuda ayuda = ayudaList.get(position);
        holder.questionTextView.setText(ayuda.getQuestion());
        holder.answerTextView.setText(ayuda.getAnswer());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.answerTextView.getVisibility() == View.GONE) {
                    holder.answerTextView.setVisibility(View.VISIBLE);
                } else {
                    holder.answerTextView.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return ayudaList.size();
    }

    public static class AyudaViewHolder extends RecyclerView.ViewHolder {
        TextView questionTextView;
        TextView answerTextView;

        public AyudaViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.textViewQuestion);
            answerTextView = itemView.findViewById(R.id.textViewAnswer);
        }
    }
}
