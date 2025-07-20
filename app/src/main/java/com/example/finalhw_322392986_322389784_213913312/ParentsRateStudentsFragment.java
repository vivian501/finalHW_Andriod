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

            // Step 1: Get the parent's document and fetch childIds
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

                        Log.d(TAG, "Child IDs found: " + childIds);

                        // Step 2: Save ratings/comments for each relevant child
                        for (Student student : registeredStudents) {
                            String uid = student.getUid();

                            if (childIds.contains(uid) && ratingChanges.containsKey(uid)) {
                                Pair<Integer, String> update = ratingChanges.get(uid);

                                Map<String, Object> data = new HashMap<>();
                                data.put("rating", update.first);
                                data.put("comment", update.second);

                                db.collection("activities")
                                        .document(activityId)
                                        .collection("ratings")
                                        .document(uid)
                                        .set(data)
                                        .addOnSuccessListener(unused -> {
                                            Log.d(TAG, "Saved to activity/ratings for student: " + uid);
                                            Toast.makeText(requireContext(), "Saved for " + student.getFullName(), Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "Error saving for student: " + uid, e);
                                            Toast.makeText(requireContext(), "Failed to save " + student.getFullName(), Toast.LENGTH_LONG).show();
                                        });
                            }
                        }

                        ratingChanges.clear(); // Clear pending changes
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to fetch parent data", e);
                        Toast.makeText(requireContext(), "Failed to fetch parent info", Toast.LENGTH_LONG).show();
                    });
        });


        return view;
    }
}
