package com.app.clipphy.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.clipphy.Activity.MainActivity;
import com.app.clipphy.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {

    private FirebaseFirestore db;

    public RegisterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register, container, false);


        db = FirebaseFirestore.getInstance();

        EditText emailEditText = view.findViewById(R.id.editTextEmailAddress);
        EditText passwordEditText = view.findViewById(R.id.passwordEditText);
        EditText firstNameEditText = view.findViewById(R.id.firstNameEditText);
        EditText lastNameEditText = view.findViewById(R.id.lastNameEditText);
        Button registerButton = view.findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();

                if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                    Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    saveLoginDetails(email, password, firstName, lastName);
                    saveUserDataToFirestore(firstName, lastName, email, password);
                    ((MainActivity) getActivity()).loadFragment(new LoginFragment());
                }
            }
        });

        return view;
    }

    private void saveLoginDetails(String email, String password, String firstName, String lastName) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.putString("firstName", firstName);
        editor.putString("lastName", lastName);
        editor.apply();

        Log.d("RegisterFragment", "Saved First name: " + firstName + ", Last name: " + lastName); // Log saved values

    }

    private void saveUserDataToFirestore(String firstName, String lastName, String email, String password) {
        Map<String, Object> user = new HashMap<>();
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("email", email);
        user.put("password", password);

        db.collection("users")
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getActivity(), "User registered successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error registering user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
