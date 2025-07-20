package com.example.finalhw_322392986_322389784_213913312;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalhw_322392986_322389784_213913312.logic_model.Student;
import com.example.finalhw_322392986_322389784_213913312.logic_model.StudentRatingAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParentsRateStudentsFragment extends Fragment {

    private static final String TAG = "RateStudentsFragment";

    private RecyclerView recyclerView;
    private Button saveButton;

    private String activityId;
    private List<Student> registeredStudents;

    private StudentRatingAdapter adapter;

    // Temp map to hold unsaved changes until Save is clicked
    private final Map<String, Pair<Integer, String>> ratingChanges = new HashMap<>();

    public static ParentsRateStudentsFragment newInstance(String activityId, List<Student> students) {
        ParentsRateStudentsFragment fragment = new ParentsRateStudentsFragment();
        Bundle args = new Bundle();
        args.putString("activityId", activityId);
        args.putSerializable("students", (Serializable) students);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_rating, container, false);

        recyclerView = view.findViewById(R.id.studentRecycler);
        saveButton = view.findViewById(R.id.saveAllButton); // Add a button with this ID to your layout

        if (getArguments() != null) {
            activityId = getArguments().getString("activityId");
            registeredStudents = (List<Student>) getArguments().getSerializable("students");
        }

        Log.d(TAG, "Loaded activityId: " + activityId);
        Log.d(TAG, "Number of students received: " + (registeredStudents != null ? registeredStudents.size() : 0));

        adapter = new StudentRatingAdapter(requireContext(), registeredStudents, activityId, (student, rating, comment) -> {
            if ((rating < 1 || rating > 10) && (comment == null || comment.isEmpty())) {
                Toast.makeText(requireContext(), "Please provide a rating or comment", Toast.LENGTH_SHORT).show();
                return;
            }


            Log.d(TAG, "Rating changed for " + student.getFullName() + " (uid=" + student.getUid() + ") → rating=" + rating + ", comment=" + comment);

            // Save to student object locally for UI
            if (student.getRatingsByActivity() == null) {
                student.setRatingsByActivity(new HashMap<>());
            }
            student.getRatingsByActivity().put(activityId, rating);

            if (student.getComments() == null) {
                student.setComments(new HashMap<>());
            }
            student.getComments().put(activityId, comment);

            // Track pending changes to push to Firestore only if "Save All" is clicked
            ratingChanges.put(student.getUid(), new Pair<>(rating, comment));

            Toast.makeText(requireContext(), "Saved locally for " + student.getFullName(), Toast.LENGTH_SHORT).show();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Handle SAVE to Firestore
        saveButton.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String parentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Step 1: Get the parent's child IDs
            db.collection("users").document(parentUid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (!documentSnapshot.exists()) {
                            Toast.makeText(requireContext(), "Parent info not found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<String> childIds = (List<String>) documentSnapshot.get("childIds");
                        if (childIds == null || childIds.isEmpty()) {
                            Toast.makeText(requireContext(), "No children found for this parent", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Log.d(TAG, "Parent has " + childIds.size() + " children");

                        // Prepare batch update to the activity document
                        Map<String, Object> updates = new HashMap<>();

                        for (Student student : registeredStudents) {
                            String childId = student.getUid();

                            if (childIds.contains(childId) && ratingChanges.containsKey(childId)) {
                                Pair<Integer, String> update = ratingChanges.get(childId);

                                // Update maps inside the activity document:
                                updates.put("comments." + childId, update.second);
                                updates.put("ratings." + childId, update.first);

                                Log.d(TAG, "Prepared update for child " + childId + ": rating=" + update.first + ", comment=" + update.second);
                            }
                        }

                        if (updates.isEmpty()) {
                            Toast.makeText(requireContext(), "No ratings/comments to save", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Update the activity document with the maps
                        db.collection("activities")
                                .document(activityId)
                                .update(updates)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(requireContext(), "Saved ratings/comments to activity", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Activity document updated successfully");
                                    ratingChanges.clear();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to update activity document", e);
                                    Toast.makeText(requireContext(), "Failed to save to activity", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to fetch parent user document", e);
                        Toast.makeText(requireContext(), "Error loading parent data", Toast.LENGTH_SHORT).show();
                    });
        });



        return view;
    }
}
