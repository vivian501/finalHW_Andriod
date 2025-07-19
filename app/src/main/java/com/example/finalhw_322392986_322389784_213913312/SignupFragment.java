package com.example.finalhw_322392986_322389784_213913312;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignupFragment extends Fragment {

   private EditText editTextUsername, editTextEmail, editTextPassword, editTextPasswordConf, kidsEmails;
   private TextView header, studentAgeLabel, freeDaysLabel;
   private Spinner userTypeSpinner, studentAgeSpin;
   private Button signupBtn, cancelBtn;
   private FirebaseAuth firebaseAuth;
   private LinearLayout freeDaysLayout;
   private String[] userTypesArray;

   private CheckBox monday, tuesday, wednesday, thursday, friday, saturday, sunday;

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      return inflater.inflate(R.layout.fragment_signup, container, false);
   }

   @Override
   public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);

      firebaseAuth = FirebaseAuth.getInstance();

      studentAgeLabel = view.findViewById(R.id.studentAgeLabel);
      freeDaysLabel = view.findViewById(R.id.freeDaysLabel);
      freeDaysLayout = view.findViewById(R.id.freeDaysLayout);
      kidsEmails = view.findViewById(R.id.kidsNameEditText);

      // Checkboxes
      monday = view.findViewById(R.id.mondayCheck);
      tuesday = view.findViewById(R.id.tuesdayCheck);
      wednesday = view.findViewById(R.id.wednesdayCheck);
      thursday = view.findViewById(R.id.thursdayCheck);
      friday = view.findViewById(R.id.fridayCheck);
      saturday = view.findViewById(R.id.saturdayCheck);
      sunday = view.findViewById(R.id.sundayCheck);

      // Basic fields
      header = view.findViewById(R.id.signupHeader);
      editTextEmail = view.findViewById(R.id.emailEditText);
      editTextPassword = view.findViewById(R.id.passwordEditText);
      editTextPasswordConf = view.findViewById(R.id.passwordConfEditText);
      editTextUsername = view.findViewById(R.id.userNameEditText);
      signupBtn = view.findViewById(R.id.signUpButton);
      cancelBtn = view.findViewById(R.id.cancelButton);
      userTypeSpinner = view.findViewById(R.id.userTypeSpinner);
      studentAgeSpin = view.findViewById(R.id.studentAgeSpinner);

      userTypesArray = getResources().getStringArray(R.array.userTypes);
      ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, userTypesArray);
      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      userTypeSpinner.setAdapter(adapter);

      ArrayAdapter<String> ageAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.studentAge));
      ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      studentAgeSpin.setAdapter(ageAdapter);

      userTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
            String selectedType = userTypesArray[position];
            if (selectedType.equalsIgnoreCase("Student")) {
               studentAgeSpin.setVisibility(View.VISIBLE);
               studentAgeLabel.setVisibility(View.VISIBLE);
               freeDaysLabel.setVisibility(View.VISIBLE);
               freeDaysLayout.setVisibility(View.VISIBLE);
               kidsEmails.setVisibility(View.GONE);
            } else if (selectedType.equalsIgnoreCase("Parent")) {
               kidsEmails.setVisibility(View.VISIBLE);
               studentAgeSpin.setVisibility(View.GONE);
               studentAgeLabel.setVisibility(View.GONE);
               freeDaysLabel.setVisibility(View.GONE);
               freeDaysLayout.setVisibility(View.GONE);
            } else {
               studentAgeSpin.setVisibility(View.GONE);
               studentAgeLabel.setVisibility(View.GONE);
               freeDaysLabel.setVisibility(View.GONE);
               freeDaysLayout.setVisibility(View.GONE);
               kidsEmails.setVisibility(View.GONE);
            }
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {
            studentAgeSpin.setVisibility(View.GONE);
         }
      });

      signupBtn.setOnClickListener(v -> signUp());
      cancelBtn.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
   }

   private void signUp() {
      String userName = editTextUsername.getText().toString().trim();
      String email = editTextEmail.getText().toString().trim();
      String password = editTextPassword.getText().toString().trim();
      String passwordConf = editTextPasswordConf.getText().toString().trim();
      String userType = userTypeSpinner.getSelectedItem().toString().trim();

      if (userName.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConf.isEmpty() || userType.isEmpty()) {
         Toast.makeText(getContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
         return;
      }

      if (!password.equals(passwordConf)) {
         Toast.makeText(getContext(), "The passwords don't match, try again.", Toast.LENGTH_SHORT).show();
         return;
      }

      List<String> freeDays = new ArrayList<>();
      if (userType.equalsIgnoreCase("Student")) {
         if (monday.isChecked()) freeDays.add("Monday");
         if (tuesday.isChecked()) freeDays.add("Tuesday");
         if (wednesday.isChecked()) freeDays.add("Wednesday");
         if (thursday.isChecked()) freeDays.add("Thursday");
         if (friday.isChecked()) freeDays.add("Friday");
         if (saturday.isChecked()) freeDays.add("Saturday");
         if (sunday.isChecked()) freeDays.add("Sunday");

         if (freeDays.isEmpty()) {
            Toast.makeText(getContext(), "Please select at least one free day", Toast.LENGTH_SHORT).show();
            return;
         }
      }

      firebaseAuth.createUserWithEmailAndPassword(email, password)
              .addOnCompleteListener(task -> {
                 if (task.isSuccessful()) {
                    String uid = firebaseAuth.getCurrentUser().getUid();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Map<String, Object> userData = new HashMap<>();
                    userData.put("email", email);
                    userData.put("user name", userName);
                    userData.put("userType", userType);

                    //signing up as a student
                    if (userType.equalsIgnoreCase("Student")) {
                       int age = Integer.parseInt(studentAgeSpin.getSelectedItem().toString());
                       userData.put("age", age);
                       userData.put("freeDays", freeDays);
                       db.collection("users").document(uid).set(userData);
                       saveSessionAndNavigate(email, userName, userType);
                    }

                    //sign up when you're a parent
                    else if (userType.equalsIgnoreCase("Parent")) {
                       String childrenRaw = kidsEmails.getText().toString().trim();
                       if (childrenRaw.isEmpty()) {
                          Toast.makeText(getContext(), "Please enter your children's emails", Toast.LENGTH_SHORT).show();
                          return;
                       }

                       String[] childEmails = childrenRaw.split(",");
                       List<String> trimmedChildEmails = new ArrayList<>();
                       for (String emailEntry : childEmails) {
                          trimmedChildEmails.add(emailEntry.trim().toLowerCase());
                       }

                       db.collection("users")
                               .whereEqualTo("userType", "Student")
                               .get()
                               .addOnSuccessListener(snapshot -> {
                                  List<String> childIds = new ArrayList<>();

                                  for (var doc : snapshot.getDocuments()) {
                                     String studentEmail = doc.getString("email");
                                     if (studentEmail != null && trimmedChildEmails.contains(studentEmail.toLowerCase())) {
                                        childIds.add(doc.getId()); // UID of student
                                     }
                                  }

                                  // Update parent data
                                  userData.put("childIds", childIds);
                                  db.collection("users").document(uid)
                                          .set(userData)
                                          .addOnSuccessListener(unused -> saveSessionAndNavigate(email, userName, userType))
                                          .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to save parent data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                               })
                               .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to fetch students: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                    else {
                       db.collection("users").document(uid).set(userData);
                       saveSessionAndNavigate(email, userName, userType);
                    }
                 } else {
                    Toast.makeText(getContext(), "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                 }
              });
   }

   private void saveSessionAndNavigate(String email, String userName, String userType) {
      SharedPreferences prefs = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = prefs.edit();
      editor.putString("email", email);
      editor.putString("user name", userName);
      editor.putString("userType", userType);
      editor.putBoolean("isLoggedIn", true);
      editor.apply();

      Toast.makeText(getContext(), "Signup successful!", Toast.LENGTH_SHORT).show();
      startActivity(new Intent(getActivity(), MainActivity.class));
      requireActivity().finish();
   }
}
