package com.example.finalhw_322392986_322389784_213913312;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginFragment extends Fragment {
    private TextView header;
    private EditText editTextPassword, editTextEmail;
    private Button cancelBtn, loginBtn;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        header = view.findViewById(R.id.loginHeader);
        editTextEmail = view.findViewById(R.id.editTextEmailAddress);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        loginBtn = view.findViewById(R.id.LoginBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);

        firebaseAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(v -> login());
        cancelBtn.setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed()
        );
    }

    public void login() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showSnackbar("All fields must be filled");
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String uid = firebaseAuth.getCurrentUser().getUid();
                Log.d("FIREBASE", "User UID: " + uid);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(uid).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String userType = documentSnapshot.getString("userType");

                                // Save to SharedPreferences
                                SharedPreferences prefs = requireActivity().getSharedPreferences("UserSession", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("email", email);
                                editor.putString("userType", userType);
                                editor.putBoolean("isLoggedIn", true);
                                editor.apply();

                                showSnackbar("Login successful!");
                                startActivity(new Intent(getContext(), MainActivity.class));
                                requireActivity().finish();
                            } else {
                                showSnackbar("User data not found in Firestore");
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("FIREBASE", "Login failed", e);
                            showSnackbar("Failed to load user data: " + e.getMessage());
                        });
            } else {
                showSnackbar("Login failed: " + task.getException().getMessage());
            }
        });
    }
    private void showSnackbar(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        }
    }

}
