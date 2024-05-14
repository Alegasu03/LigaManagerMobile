package com.example.ligamanagermobile.ui.futbol;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ligamanagermobile.LigaCrear;
import com.example.ligamanagermobile.R;
import com.example.ligamanagermobile.databinding.FragmentFutbolBinding;

public class FutbolFragment extends Fragment {

    private FragmentFutbolBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FutbolViewModel futbolViewModel =
                new ViewModelProvider(this).get(FutbolViewModel.class);

        binding = FragmentFutbolBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        AppCompatImageButton btnCreaLiga= root.findViewById(R.id.imageButtonCreaLiga);
        btnCreaLiga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(requireContext(), LigaCrear.class);
            startActivity(intent);

            }
        });





        return root;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}