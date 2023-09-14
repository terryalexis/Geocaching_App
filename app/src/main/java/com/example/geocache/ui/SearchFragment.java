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
import android.widget.RadioGroup;

import com.example.geocache.MapsActivity;
import com.example.geocache.R;
import com.example.geocache.databinding.FragmentSearchBinding;


public class SearchFragment extends Fragment {
    FragmentSearchBinding binding;
    NavController controller;

    final Integer OPTION_ALL = R.id.allSelector;
    final Integer OPTION_OTHERS = R.id.othersSelector;
    final Integer OPTION_CURRENT_USER = R.id.meSelector;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        controller = NavHostFragment.findNavController(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createSearchButtonListener();
        createBackButtonListener();
    }

    private void createSearchButtonListener() {
        binding.searchButton.setOnClickListener((view) -> {
            String maxDistInputString = binding.maxDistInput.getText().toString();
            Integer geocacheCreatorId = binding.radioGroup.getCheckedRadioButtonId();
            String inputError = getInputErrors(maxDistInputString, geocacheCreatorId);
            if(inputError.equals("")) {
                Integer maxDistInput = Integer.parseInt(maxDistInputString);
                Bundle bundle = new Bundle();
                bundle.putInt("maxDistInput", maxDistInput);
                bundle.putInt("geocacheCreatorId", geocacheCreatorId);

                controller.navigate(R.id.action_searchFragment_to_homeFragment, bundle);
            } else {
                binding.searchWarningText.setText(inputError);
            }

        });
    }

    private String getInputErrors(String maxDistInput, Integer selectedGeocacheCreator) {
        String inputError = "";

        if(maxDistInput.equals("")) {
            inputError = "Max Distance is required";
        } else if (selectedGeocacheCreator == null) {
            inputError = "Created by is required";
        }

        return inputError;
    }

    private void createBackButtonListener() {
        binding.searchBackButton.setOnClickListener((view) -> {
            controller.navigate(R.id.action_searchFragment_to_homeFragment);
        });
    }
}