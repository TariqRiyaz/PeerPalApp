package com.peerpal.peerpalapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.peerpal.peerpalapp.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflating the layout for the fragment using FragmentHomeBinding
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // Getting and returning the root view of the fragment
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        // Destroys this view and sets binding to null
        super.onDestroyView();
        binding = null;
    }
}