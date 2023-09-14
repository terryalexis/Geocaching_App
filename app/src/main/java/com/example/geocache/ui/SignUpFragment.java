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

import com.example.geocache.R;
import com.example.geocache.databinding.FragmentSignUpBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpFragment extends Fragment {
    FragmentSignUpBinding binding;
    NavController controller;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        controller = NavHostFragment.findNavController(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.createSignUpButtonListener();
    }

    private void createSignUpButtonListener() {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // get input field values
        EditText emailField = binding.createEmailAddress;
        EditText confirmEmailField = binding.confirmEmailAddress;
        EditText passwordField = binding.createPassword;
        EditText confirmPasswordField = binding.confirmPassword;

        // create sign up button listener
        binding.createAccountButton.setOnClickListener((view) -> {
            String email = emailField.getText().toString();
            String confirmEmail = confirmEmailField.getText().toString();
            String password = passwordField.getText().toString();
            String confirmPassword = confirmPasswordField.getText().toString();

            TextView errorMessage = binding.errorMessageSignUp;

            // validate input fields
            if(email.equals("")){
                errorMessage.setText("Email is required");
            } else if(confirmEmail.equals("")) {
                errorMessage.setText("Please confirm the email address");
            } else if(password.equals("")) {
                errorMessage.setText("Password is required");
            } else if(password.length() <= 8) {
                errorMessage.setText("Password must be longer than 8 characters");
            } else if(confirmPassword.equals("")) {
                errorMessage.setText("Please confirm the password");
            } else if(!email.equals(confirmEmail)) {
                errorMessage.setText("The emails do not match. Please reconfirm.");
            } else if(!password.equals(confirmPassword)) {
                errorMessage.setText("The passwords do not match. Please reconfirm.");
            } else {
                // sign up
                errorMessage.setText("");
                auth.createUserWithEmailAndPassword(
                        email,
                        password
                ).addOnCompleteListener((task) -> {
                    if(task.isSuccessful()) {
                        controller.navigate(R.id.action_signUpFragment2_to_signInFragment);
                    } else {
                        String[] errorMessageParts = task.getException().toString().split(": ");
                        errorMessage.setText(errorMessageParts[1]);
                    }
                });
            }
        });
    }
}