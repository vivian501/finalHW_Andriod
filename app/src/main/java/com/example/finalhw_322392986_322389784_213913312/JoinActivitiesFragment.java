package com.example.finalhw_322392986_322389784_213913312;

import static android.view.View.GONE;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoinActivitiesFragment extends Fragment {
    private RecyclerView recyclerView;
    private Button filterBtn, resetBtn, saveBtn, cancelBtn, createAct;
    private List<Activity> allActivities; //for dummy data
    private List<Activity> relevantActivities = new ArrayList<>(); // the relevant activities that will be displayed for each user

    private Map<String, Activity> selectedActivitiesPerDomain = new HashMap<>(); // to save the selected activities and make sure at least one activity of each domain is selected
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

        selectedActivitiesPerDomain = new HashMap<>();
        selectedJoinDates = new HashMap<>();

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
                        selectedActivitiesPerDomain.clear();
                        selectedJoinDates.clear();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        saveBtn.setOnClickListener(v -> {
            if (selectedActivitiesPerDomain.containsKey("Science") &&
                    selectedActivitiesPerDomain.containsKey("Social") &&
                    selectedActivitiesPerDomain.containsKey("Creativity")) {

                new AlertDialog.Builder(requireContext())
                        .setTitle("Save Registration")
                        .setMessage("Are you sure you are done?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            if (currentStudent.getRegisteredActivityIds() == null) {
                                currentStudent.setRegisteredActivityIds(new ArrayList<>());
                            }

                            if (currentStudent.getJoinedActivityDates() == null) {
                                currentStudent.setJoinedActivityDates(new HashMap<>());
                            }

                            List<String> registeredIds = currentStudent.getRegisteredActivityIds();
                            Map<String, Date> joinedDates = currentStudent.getJoinedActivityDates();

                            for (Activity activity : selectedActivitiesPerDomain.values()) {
                                String activityId = activity.getActivityId();
                                if (!registeredIds.contains(activityId)) {
                                    registeredIds.add(activityId);
                                    joinedDates.put(activityId, selectedJoinDates.get(activityId));
                                }
                            }

                            requireActivity().getSupportFragmentManager().popBackStack();
                            Toast.makeText(requireContext(), "Registration saved!", Toast.LENGTH_SHORT).show();
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
            if (selectedActivitiesPerDomain.containsValue(activity)) {
                Toast.makeText(requireContext(), "You already joined this activity", Toast.LENGTH_SHORT).show();
            } else {
                selectedActivitiesPerDomain.put(activity.getDomain(), activity);
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



    //  Domain filter on top of relevantActivities only
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
            if (selectedActivitiesPerDomain.containsValue(activity)) {
                Toast.makeText(requireContext(), "You already joined this activity", Toast.LENGTH_SHORT).show();
            } else {
                selectedActivitiesPerDomain.put(activity.getDomain(), activity);
                Toast.makeText(requireContext(), "Joined " + activity.getName(), Toast.LENGTH_SHORT).show();
                // this part saves the date of joining the activity
                if (currentStudent.getJoinedActivityDates() == null) { // if the map is not initialized
                    currentStudent.setJoinedActivityDates(new HashMap<>()); // initialize it
                }
                Date joinDate = new Date();
                currentStudent.getJoinedActivityDates().put(activity.getActivityId(), joinDate);
                // Store it in temporary map for saving later
                selectedJoinDates.put(activity.getActivityId(), joinDate);
            }
        });
        recyclerView.setAdapter(filteredAdapter);
    }

    //  check if activity is relevant to student
    private boolean isActivityRelevantToStudent(Activity activity, Student student) {
        try {
            Date today = new Date();
            if (activity.getEndDate() != null && activity.getEndDate().before(today)) {
                Log.d("FILTER", "Skipped: ended already → " + activity.getName());
                return false;
            }

            int studentAge = student.getAge();
            if (studentAge < activity.getMinAge() || studentAge > activity.getMaxAge()) {
                Log.d("FILTER", "Skipped: age not in range → " + activity.getName());
                return false;
            }

            List<String> activityDays = activity.getDays();
            List<String> studentFreeDays = student.getFreeDays();

            if (activityDays == null || activityDays.isEmpty()) {
                Log.d("FILTER", "Skipped: activity days empty → " + activity.getName());
                return false;
            }

            if (studentFreeDays == null || studentFreeDays.isEmpty()) {
                Log.d("FILTER", "Skipped: student free days empty → " + student.getFullName());
                return false;
            }

            for (String day : activityDays) {
                if (studentFreeDays.contains(day)) {
                    return true;
                }
            }

            Log.d("FILTER", "Skipped: no matching days → " + activity.getName());
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("FILTER", "Error: " + e.getMessage());
            return false;
        }
    }



}
