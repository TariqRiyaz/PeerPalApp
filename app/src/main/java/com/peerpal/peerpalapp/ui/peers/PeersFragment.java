package com.peerpal.peerpalapp.ui.peers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.peerpal.peerpalapp.databinding.FragmentPeersBinding;

public class PeersFragment extends Fragment {

    private FragmentPeersBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PeersViewModel peersViewModel =
                new ViewModelProvider(this).get(PeersViewModel.class);

        binding = FragmentPeersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}