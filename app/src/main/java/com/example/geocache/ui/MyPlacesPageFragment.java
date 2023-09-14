package com.example.geocache.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.geocache.R;
import com.example.geocache.databinding.FragmentHomeBinding;
import com.example.geocache.databinding.FragmentMyPlacesPageBinding;
import com.google.firebase.auth.FirebaseAuth;


public class MyPlacesPageFragment extends Fragment {
    FragmentMyPlacesPageBinding binding;
    NavController controller;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyPlacesPageBinding.inflate(inflater, container, false);
        controller = NavHostFragment.findNavController(this);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createBackButtonListener();
        createAddButtonListener();
    }

    private void createBackButtonListener() {
        binding.MyPlacesBackButton.setOnClickListener((view) -> {
            controller.navigate(R.id.action_myPlacesPageFragment_to_homeFragment);
        });
    }

    private void createAddButtonListener() {
        binding.addLocation.setOnClickListener((view) -> {
            controller.navigate(R.id.action_myPlacesPageFragment_to_createGeocacheFragment);
        });
    }
}