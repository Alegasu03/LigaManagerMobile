package com.example.ligamanagermobile.ui.tenis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ligamanagermobile.databinding.FragmentTenisBinding;

public class TenisFragment extends Fragment {

    private FragmentTenisBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TenisViewModel tenisViewModel =
                new ViewModelProvider(this).get(TenisViewModel.class);

        binding = FragmentTenisBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textTenis;
        tenisViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
