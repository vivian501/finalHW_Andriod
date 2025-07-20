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
    private List<Activity> allActivities;
    private List<Activity> relevantActivities = new ArrayList<>(); // the relevant activities that will be displayed for each user

    private List<Activity> selectedActivities = new ArrayList<>(); // save all selected activities
    private Set<String> selectedDomains = new HashSet<>(); // to check if all three domains were selected
    private Student currentStudent;
    private Map<String, Date> selectedJoinDates = new HashMap<>();

    private static final String TAG = "JoinActivitiesFragment"; // <-- debug tag

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

        selectedActivities.clear();
        selectedJoinDates.clear();
        selectedDomains.clear();

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
            setupAdapterWithJoinListener(); // restore all relevant activities
        });

        // Cancel button logic
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

                            if (currentStudent.getRegisteredActivityIds() == null)
                                currentStudent.setRegisteredActivityIds(new ArrayList<>());
                            if (currentStudent.getJoinedActivityDates() == null)
                                currentStudent.setJoinedActivityDates(new HashMap<>());

                            List<String> registeredIds = currentStudent.getRegisteredActivityIds();
                            Map<String, Date> joinedDates = currentStudent.getJoinedActivityDates();

                            for (Activity activity : selectedActivities) {
                                String activityId = activity.getActivityId();
                                Date joinDate = selectedJoinDates.get(activityId);

                                if (!registeredIds.contains(activityId)) {
                                    registeredIds.add(activityId);
                                    joinedDates.put(activityId, joinDate);

                                    db.collection("activities")
                                            .document(activityId)
                                            .update("joinedStudentsIds", com.google.firebase.firestore.FieldValue.arrayUnion(studentId));

                                    Map<String, Object> joinData = new HashMap<>();
                                    joinData.put("studentId", studentId);
                                    joinData.put("activityId", activityId);
                                    db.collection("student_activity_joins")
                                            .document(studentId + "_" + activityId)
                                            .set(joinData);
                                }
                            }

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
                                    .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to save student: " + e.getMessage(), Toast.LENGTH_LONG).show());

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

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    currentStudent = documentSnapshot.toObject(Student.class);
                    if (currentStudent == null) {
                        Toast.makeText(getContext(), "Failed to load student data", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Log.d(TAG, "Student loaded: " + currentStudent.getFullName());

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
                                            Log.d(TAG, "Relevant activity: " + activity.getName());
                                        } else {
                                            Log.d(TAG, "Filtered out: " + activity.getName());
                                        }
                                    }
                                }

                                Log.d(TAG, "Total relevant: " + relevantActivities.size());
                                setupAdapterWithJoinListener();
                            })
                            .addOnFailureListener(e -> Log.e(TAG, "Failed to fetch activities", e));
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to fetch user", e));
    }

    private void setupAdapterWithJoinListener() {
        ActivityAdapter adapter = new ActivityAdapter(relevantActivities, AdapterMode.JOIN);
        adapter.setOnJoinClickListener(activity -> {
            if (selectedActivities.contains(activity) ||
                    (currentStudent.getRegisteredActivityIds() != null &&
                            currentStudent.getRegisteredActivityIds().contains(activity.getActivityId()))) {
                Toast.makeText(requireContext(), "You already joined this activity", Toast.LENGTH_SHORT).show();
            } else {
                selectedActivities.add(activity);
                selectedDomains.add(activity.getDomain());

                Toast.makeText(requireContext(), "Joined " + activity.getName(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Joined activity: " + activity.getName());

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

    // this function filters the relevantActivities list by domain and shows only those
    private void filterActivities(String domain) {
        List<Activity> filtered = new ArrayList<>();
        for (Activity activity : relevantActivities) {
            if (domain == null || activity.getDomain().equalsIgnoreCase(domain)) {
                filtered.add(activity);
            }
        }

        Log.d(TAG, "Filtering by domain: " + domain + " → Found: " + filtered.size());

        ActivityAdapter filteredAdapter = new ActivityAdapter(filtered, AdapterMode.JOIN);
        filteredAdapter.setOnJoinClickListener(activity -> {
            if (selectedActivities.contains(activity) ||
                    (currentStudent.getRegisteredActivityIds() != null &&
                            currentStudent.getRegisteredActivityIds().contains(activity.getActivityId()))) {
                Toast.makeText(requireContext(), "You already joined this activity", Toast.LENGTH_SHORT).show();
            } else {
                selectedActivities.add(activity);
                selectedDomains.add(activity.getDomain());

                Toast.makeText(requireContext(), "Joined " + activity.getName(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Joined (filtered): " + activity.getName());

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

    // check if an activity is suitable for the student based on age, days, and if it's upcoming
    private boolean isActivityRelevantToStudent(Activity activity, Student student) {
        try {
            Date today = new Date();

            if (activity.getEndDate() != null && activity.getEndDate().before(today)) {
                Log.d(TAG, "Rejected due to past end date: " + activity.getName());
                return false;
            }

            int studentAge = student.getAge();
            if (studentAge < activity.getMinAge() || studentAge > activity.getMaxAge()) {
                Log.d(TAG, "Rejected due to age: " + activity.getName());
                return false;
            }

            List<String> activityDays = activity.getDays();
            List<String> studentFreeDays = student.getFreeDays();

            if (activityDays == null || activityDays.isEmpty() || studentFreeDays == null || studentFreeDays.isEmpty()) {
                Log.d(TAG, "Rejected due to empty days: " + activity.getName());
                return false;
            }

            //  Require that ALL activity days are in studentFreeDays!!
            for (String day : activityDays) {
                if (!studentFreeDays.contains(day)) {
                    Log.d(TAG, "Rejected: activity day " + day + " not in student's free days: " + activity.getName());
                    return false;
                }
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
