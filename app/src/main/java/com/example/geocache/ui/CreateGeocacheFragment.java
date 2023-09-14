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
import com.example.geocache.databinding.FragmentCreateGeocacheBinding;
import com.example.geocache.databinding.FragmentHomeBinding;
import com.example.geocache.models.Geocache;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class CreateGeocacheFragment extends Fragment {
    FragmentCreateGeocacheBinding binding;
    NavController controller;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCreateGeocacheBinding.inflate(inflater, container, false);
        controller = NavHostFragment.findNavController(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createBackButtonListener();
        createSaveButtonListener();
    }

    private void createBackButtonListener() {
        binding.createGeocacheBackButton.setOnClickListener((view) -> {
            controller.navigate(R.id.action_createGeocacheFragment_to_myPlacesPageFragment);
        });
    }

    private void createSaveButtonListener() {
        binding.saveButton.setOnClickListener((view) -> {
            String latitudeInput = binding.latitudeInput.getText().toString();
            String longitudeInput = binding.longitudeInput.getText().toString();
            String errorMessage = getValidationError(latitudeInput, longitudeInput);
            if(errorMessage.equals("")) {
                try {
                    this.disableSaveButton();
                    saveGeocache(latitudeInput, longitudeInput);
                    controller.navigate(R.id.action_createGeocacheFragment_to_myPlacesPageFragment);
                } catch(Exception e) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.createGeocacheFragment, new MyPlacesPageFragment())
                            .setReorderingAllowed(true)
                            .commit();
                }
            } else {
                binding.warningText.setText(errorMessage);
            }
        });
    }

    private String getValidationError(String latitude, String longitude) {
        String errorMessage = "";

        Float parsedLatitude = Float.parseFloat(latitude.equals("") ? "0.0" : latitude);
        Float parsedLongitude = Float.parseFloat(longitude.equals("") ? "0.0" : longitude);

        if(latitude.equals("")) {
            errorMessage = "Latitude cannot be empty";
        } else if(longitude.equals("")) {
            errorMessage = "Longitude cannot be empty";
        } else if(parsedLatitude < -90f || parsedLatitude > 90f) {
            errorMessage = "Latitude must be between -90 and 90 degrees";
        } else if(parsedLongitude < -180f || parsedLongitude > 180f) {
            errorMessage = "Longitude must be between -180 and 180 degrees";
        }

        return errorMessage;
    }

    private void disableSaveButton() {
        binding.saveButton.setEnabled(false);
    }

    private void saveGeocache(String latitude, String longitude) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Float parsedLatitude = Float.parseFloat(latitude);
        Float parsedLongitude = Float.parseFloat(longitude);

        Geocache geocache = new Geocache(
                parsedLatitude,
                parsedLongitude,
                ""
        );
        db.collection("geocaches").add(geocache).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                // set the geocache id
                db.collection("geocaches").document(documentReference.getId()).update("id", documentReference.getId());
            }
        });
    }
}