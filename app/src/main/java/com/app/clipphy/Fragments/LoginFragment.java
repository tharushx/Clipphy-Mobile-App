package com.app.clipphy.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.app.clipphy.Activity.HomeActivity;
import com.app.clipphy.R;
import java.util.concurrent.Executor;

public class LoginFragment extends Fragment {

    public LoginFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        EditText emailEditText = view.findViewById(R.id.editTextEmailAddress);
        EditText passwordEditText = view.findViewById(R.id.passwordEditText);
        Button loginButton = view.findViewById(R.id.loginButton);
        ImageView fingerprintButton = view.findViewById(R.id.fingerprintButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                Log.d("LoginFragment", "Login button clicked"); // Log button click

                if (validateLogin(email, password)) {
                    Log.d("LoginFragment", "Login validation successful"); // Log validation success
                    Toast.makeText(getActivity(), "Login Successful!", Toast.LENGTH_SHORT).show();

                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
                    String firstName = sharedPreferences.getString("firstName", null);
                    String lastName = sharedPreferences.getString("lastName", null);

                    Log.d("LoginFragment", "First name: " + firstName + ", Last name: " + lastName); // Log values

                    navigateToHomeActivity(firstName, lastName);
                } else {
                    Log.d("LoginFragment", "Login validation failed"); // Log validation failure
                    Toast.makeText(getActivity(), "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fingerprintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBiometricPrompt();
            }
        });

        return view;
    }

    private boolean validateLogin(String email, String password) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String storedEmail = sharedPreferences.getString("email", null);
        String storedPassword = sharedPreferences.getString("password", null);
        return email.equals(storedEmail) && password.equals(storedPassword);
    }

    private void showBiometricPrompt() {
        BiometricManager biometricManager = BiometricManager.from(getContext());
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS) {
            Executor executor = ContextCompat.getMainExecutor(getContext());
            BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    Toast.makeText(getContext(), "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    Toast.makeText(getContext(), "Authentication succeeded!", Toast.LENGTH_SHORT).show();

                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
                    String firstName = sharedPreferences.getString("firstName", null);
                    String lastName = sharedPreferences.getString("lastName", null);

                    navigateToHomeActivity(firstName, lastName);
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    Toast.makeText(getContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            });

            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Biometric Login")
                    .setSubtitle("Log in using your biometric credential")
                    .setNegativeButtonText("Cancel")
                    .build();

            biometricPrompt.authenticate(promptInfo);
        } else {
            Toast.makeText(getContext(), "Biometric authentication is not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToHomeActivity(String firstName, String lastName) {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.putExtra("firstName", firstName);
        intent.putExtra("lastName", lastName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
