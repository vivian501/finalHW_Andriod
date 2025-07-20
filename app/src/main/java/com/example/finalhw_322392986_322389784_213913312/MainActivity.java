package com.example.finalhw_322392986_322389784_213913312;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private Button logoutBtn;
    private Toolbar toolbar;
    private ImageView logo;
    private SharedPreferences prefs;
    private String userTypeFromPrefs;
    private String userTypeFromFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        logoutBtn = findViewById(R.id.logoutButton);
        logo = findViewById(R.id.logo);
        prefs = getSharedPreferences("UserSession", MODE_PRIVATE);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            redirectToAuth();
            return;
        }

        // Step 1: Try to get userType from SharedPreferences
        userTypeFromPrefs = prefs.getString("userType", null);
        if (userTypeFromPrefs != null) {
            loadHomeFragment(userTypeFromPrefs); // preload immediately
        }

        // Step 2: Verify and sync with Firestore
        FirebaseFirestore.getInstance().collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        userTypeFromFirestore = doc.getString("userType");
                        if (userTypeFromFirestore != null) {
                            if (!userTypeFromFirestore.equals(userTypeFromPrefs)) {
                                // Update SharedPreferences if values differ or are missing
                                prefs.edit().putString("userType", userTypeFromFirestore).apply();
                                loadHomeFragment(userTypeFromFirestore); // reload if needed
                            }
                        } else {
                            showSnackbar("User type not found in Firestore.");
                            logout();
                        }
                    } else {
                        showSnackbar("User document not found.");
                        logout();
                    }
                })
                .addOnFailureListener(e -> {
                    showSnackbar("Failed to fetch user type from Firestore.");

                    logout();
                });

        logoutBtn.setOnClickListener(v -> logout());
    }

    private void loadHomeFragment(String userType) {
        Fragment fragment;
        switch (userType) {
            case "Student":
                fragment = new StudentHomeFragment();
                break;
            case "Parent":
                fragment = new ActivityParentHomeFragment();
                break;
            case "Activity guide":
                fragment = new ActivityGuideHomeFragment();
                break;
            case "Coordinator":
                fragment = new CoordinatorHomeFragment();
                break;
            default:
                showSnackbar("Unknown role: " + userType);
                logout();
                return;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        prefs.edit().clear().apply();
        redirectToAuth();
    }

    private void redirectToAuth() {
        startActivity(new Intent(this, AuthActivity.class));
        finish();
    }
    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

}
