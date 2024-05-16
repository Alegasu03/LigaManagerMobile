package com.example.ligamanagermobile.ui.baloncesto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ligamanagermobile.databinding.FragmentBaloncestoBinding;

public class BaloncestoFragment extends Fragment {

    private FragmentBaloncestoBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        BaloncestoViewModel baloncestoViewModel;
        baloncestoViewModel = new ViewModelProvider(this).get(BaloncestoViewModel.class);

        binding = FragmentBaloncestoBinding.inflate(inflater, container, false);


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}