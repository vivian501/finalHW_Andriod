package com.example.finalhw_322392986_322389784_213913312;

import static android.view.View.GONE;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalhw_322392986_322389784_213913312.logic_model.Activity;
import com.example.finalhw_322392986_322389784_213913312.logic_model.ActivityAdapter;
import com.example.finalhw_322392986_322389784_213913312.logic_model.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class JoinActivitiesFragment extends Fragment {
    private RecyclerView recyclerView;
    private Button filterBtn, resetBtn, saveBtn, cancelBtn, createAct;
    private List<Activity> allActivities; //for dummy data
    private List<Activity> relevantActivities = new ArrayList<>(); // the relevant activities that will be displayed for each user

    private List<Activity> selectedActivities = new ArrayList<>(); // save all selected activities
    private Set<String> selectedDomains = new HashSet<>(); // to check if all three domains were selected
    private Student currentStudent; // to check the users info to display only relevant activities
    private Map<String, Date> selectedJoinDates = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activities_browser_join, container, false);

        filterBtn = view.findViewById(R.id.filterBtn);
        resetBtn = view.findViewById(R.id.resetBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);
        saveBtn = view.findViewById(R.id.saveBtn);
        createAct = view.findViewById(R.id.createActivityBtn);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        selectedActivities = new ArrayList<>();
        selectedJoinDates = new HashMap<>();
        selectedDomains = new HashSet<>();

        // HIDE activity creation button for students
        createAct.setVisibility(GONE);

        // Load activities and student data
        fetchActivitiesFromFirestore();

        // Filter button logic
        filterBtn.setOnClickListener(v -> {
            FilterJoinDialogFragment filterDialog = new FilterJoinDialogFragment();
            filterDialog.setFilterListener(domain -> filterActivities(domain));
            filterDialog.show(getParentFragmentManager(), "FilterDialog");
        });

        // Reset button logic
        resetBtn.setOnClickListener(v -> {
            recyclerView.setAdapter(new ActivityAdapter(relevantActivities, AdapterMode.JOIN));
        });

        cancelBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Cancel Registration")
                    .setMessage("Are you sure you want to cancel registration and return to the home screen?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        selectedActivities.clear();
                        selectedJoinDates.clear();
                        selectedDomains.clear();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        // Save button logic
        saveBtn.setOnClickListener(v -> {
            if (selectedDomains.contains("Science") &&
                    selectedDomains.contains("Social") &&
                    selectedDomains.contains("Creativity")) {

                new AlertDialog.Builder(requireContext())
                        .setTitle("Save Registration")
                        .setMessage("Are you sure you are done?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            String studentId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            if (currentStudent.getRegisteredActivityIds() == null) {
                                currentStudent.setRegisteredActivityIds(new ArrayList<>());
                            }

                            if (currentStudent.getJoinedActivityDates() == null) {
                                currentStudent.setJoinedActivityDates(new HashMap<>());
                            }

                            List<String> registeredIds = currentStudent.getRegisteredActivityIds();
                            Map<String, Date> joinedDates = currentStudent.getJoinedActivityDates();

                            for (Activity activity : selectedActivities) {
                                String activityId = activity.getActivityId();
                                Date joinDate = selectedJoinDates.get(activityId);

                                if (!registeredIds.contains(activityId)) {
                                    registeredIds.add(activityId);
                                    joinedDates.put(activityId, joinDate);
                                }

                                // Update activity: add student to joined list
                                db.collection("activities")
                                        .document(activityId)
                                        .update("joinedStudentsIds", com.google.firebase.firestore.FieldValue.arrayUnion(studentId));

                                // Optional: Also write a join record to student_activity_joins
                                Map<String, Object> joinData = new HashMap<>();
                                joinData.put("studentId", studentId);
                                joinData.put("activityId", activityId);
                                db.collection("student_activity_joins")
                                        .document(studentId + "_" + activityId)
                                        .set(joinData);
                            }

                            // Update student document
                            Map<String, Object> studentUpdates = new HashMap<>();
                            studentUpdates.put("registeredActivityIds", registeredIds);
                            studentUpdates.put("joinedActivityDates", joinedDates);

                            db.collection("users")
                                    .document(studentId)
                                    .update(studentUpdates)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(requireContext(), "Registration saved!", Toast.LENGTH_SHORT).show();
                                        requireActivity().getSupportFragmentManager().popBackStack();
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(requireContext(), "Failed to save student: " + e.getMessage(), Toast.LENGTH_LONG).show());

                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                Toast.makeText(requireContext(), "You must select one activity from each domain", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void fetchActivitiesFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Step 1: Fetch current student
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    currentStudent = documentSnapshot.toObject(Student.class);

                    if (currentStudent == null) {
                        Toast.makeText(getContext(), "Failed to load student data", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Step 2: Fetch activities
                    db.collection("activities")
                            .get()
                            .addOnSuccessListener(querySnapshot -> {
                                allActivities = new ArrayList<>();
                                relevantActivities = new ArrayList<>();

                                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                    Activity activity = doc.toObject(Activity.class);
                                    if (activity != null) {
                                        activity.setActivityId(doc.getId());
                                        allActivities.add(activity);

                                        if (isActivityRelevantToStudent(activity, currentStudent)) {
                                            relevantActivities.add(activity);
                                        }
                                    }
                                }

                                setupAdapterWithJoinListener();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(getContext(), "Failed to fetch activities", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to fetch user", Toast.LENGTH_SHORT).show());
    }

    private void setupAdapterWithJoinListener() {
        ActivityAdapter adapter = new ActivityAdapter(relevantActivities, AdapterMode.JOIN);

        adapter.setOnJoinClickListener(activity -> {
            if (selectedActivities.contains(activity)) {
                Toast.makeText(requireContext(), "You already joined this activity", Toast.LENGTH_SHORT).show();
            } else {
                selectedActivities.add(activity);
                selectedDomains.add(activity.getDomain()); // track domain coverage
                Toast.makeText(requireContext(), "Joined " + activity.getName(), Toast.LENGTH_SHORT).show();

                if (currentStudent.getJoinedActivityDates() == null) {
                    currentStudent.setJoinedActivityDates(new HashMap<>());
                }

                Date joinDate = new Date();
                currentStudent.getJoinedActivityDates().put(activity.getActivityId(), joinDate);
                selectedJoinDates.put(activity.getActivityId(), joinDate);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    // Domain filter on top of relevantActivities only
    private void filterActivities(String domain) {
        List<Activity> filtered = new ArrayList<>();

        for (Activity activity : relevantActivities) {
            boolean match = true;
            if (domain != null && !activity.getDomain().equalsIgnoreCase(domain)) {
                match = false;
            }
            if (match) {
                filtered.add(activity);
            }
        }

        ActivityAdapter filteredAdapter = new ActivityAdapter(filtered, AdapterMode.JOIN);
        filteredAdapter.setOnJoinClickListener(activity -> {
            if (selectedActivities.contains(activity)) {
                Toast.makeText(requireContext(), "You already joined this activity", Toast.LENGTH_SHORT).show();
            } else {
                selectedActivities.add(activity);
                selectedDomains.add(activity.getDomain());

                Toast.makeText(requireContext(), "Joined " + activity.getName(), Toast.LENGTH_SHORT).show();

                if (currentStudent.getJoinedActivityDates() == null) {
                    currentStudent.setJoinedActivityDates(new HashMap<>());
                }

                Date joinDate = new Date();
                currentStudent.getJoinedActivityDates().put(activity.getActivityId(), joinDate);
                selectedJoinDates.put(activity.getActivityId(), joinDate);
            }
        });

        recyclerView.setAdapter(filteredAdapter);
    }

    // Check if activity is relevant to student
    private boolean isActivityRelevantToStudent(Activity activity, Student student) {
        try {
            Date today = new Date();
            if (activity.getEndDate() != null && activity.getEndDate().before(today)) {
                return false;
            }

            int studentAge = student.getAge();
            if (studentAge < activity.getMinAge() || studentAge > activity.getMaxAge()) {
                return false;
            }

            List<String> activityDays = activity.getDays();
            List<String> studentFreeDays = student.getFreeDays();

            if (activityDays == null || activityDays.isEmpty() ||
                    studentFreeDays == null || studentFreeDays.isEmpty()) {
                return false;
            }

            for (String day : activityDays) {
                if (studentFreeDays.contains(day)) {
                    return true;
                }
            }

            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
