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
import android.widget.EditText;
import android.widget.TextView;

import com.example.geocache.MapsActivity;
import com.example.geocache.R;
import com.example.geocache.databinding.FragmentSignInBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SignInFragment extends Fragment {
    FragmentSignInBinding binding;
    NavController controller;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        controller = NavHostFragment.findNavController(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.createSignUpButtonListener();
        this.createSignInButtonListener();
    }

    private void createSignInButtonListener() {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // get input field values
        EditText emailField = binding.EmailAddress;
        EditText passwordField = binding.Password;

        binding.SignInButton.setOnClickListener((view) -> {
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();

            // Check if email and/or password is empty
            if(email.equals("") || password.equals("")) {
                TextView errorMessage = binding.errorMessage;
                errorMessage.setText("Invalid email or password");
            } else {
                // sign in
                auth.signInWithEmailAndPassword(
                        email,
                        password
                ).addOnCompleteListener((task) -> {
                    if(task.isSuccessful()) {
                        controller.navigate(R.id.action_signInFragment_to_homeFragment);
                    } else {
                        // return error
                        TextView errorMessage = binding.errorMessage;
                        String[] errorMessageParts = task.getException().toString().split(": ");
                        errorMessage.setText(errorMessageParts[1]);
                    }
                });
            }
        });
    }

    private void createSignUpButtonListener() {
        binding.SignUpButton.setOnClickListener((view) -> {
            controller.navigate(R.id.action_signInFragment_to_signUpFragment2);
        });
    }
}