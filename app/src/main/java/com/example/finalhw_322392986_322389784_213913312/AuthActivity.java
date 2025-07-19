package com.example.finalhw_322392986_322389784_213913312;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthActivity extends AppCompatActivity {

    private Button loginBtn, signupBtn;
    private TextView header;
    private Fragment loginF, signupF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        loginBtn = findViewById(R.id.loginButton);
        signupBtn = findViewById(R.id.signupButton);
        header = findViewById(R.id.welcomeTextView);

        loginF = new LoginFragment();
        signupF = new SignupFragment();

        loginBtn.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, loginF)
                    .addToBackStack(null)
                    .commit();
            hideMainComponents();
        });

        signupBtn.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, signupF)
                    .addToBackStack(null)
                    .commit();
            hideMainComponents();
        });

        // Restore components when backstack is empty
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                showMainComponents();
            }
        });

        // Auto-redirect if already logged in and userType exists
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userType = prefs.getString("userType", null);

        if (currentUser != null && userType != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void hideMainComponents() {
        loginBtn.setVisibility(View.GONE);
        signupBtn.setVisibility(View.GONE);
        header.setVisibility(View.GONE);
    }

    private void showMainComponents() {
        loginBtn.setVisibility(View.VISIBLE);
        signupBtn.setVisibility(View.VISIBLE);
        header.setVisibility(View.VISIBLE);
    }
}
