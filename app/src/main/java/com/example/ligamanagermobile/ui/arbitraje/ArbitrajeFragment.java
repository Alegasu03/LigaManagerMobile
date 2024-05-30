package com.example.ligamanagermobile.ui.arbitraje;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ligamanagermobile.databinding.FragmentArbitrajeBinding;

public class ArbitrajeFragment extends Fragment {

    private FragmentArbitrajeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ArbitrajeViewModel arbitrajeViewModel =
                new ViewModelProvider(this).get(ArbitrajeViewModel.class);

        binding = FragmentArbitrajeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
