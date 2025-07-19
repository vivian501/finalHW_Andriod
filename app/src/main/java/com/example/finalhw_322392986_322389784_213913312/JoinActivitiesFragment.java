package com.example.finalhw_322392986_322389784_213913312;

import android.app.AlertDialog;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoinActivitiesFragment extends Fragment {
    private RecyclerView recyclerView;
    private Button filterBtn, resetBtn, saveBtn, cancelBtn;
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
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load dummy student and all activities (replace with Firebase logic later)
        currentStudent = DummyDataProvider.getDummyStudent();
        allActivities = DummyDataProvider.getDummyActivities();

        //  Filter relevant activities
        relevantActivities = new ArrayList<>();
        for (Activity activity : allActivities) {
            if (isActivityRelevantToStudent(activity, currentStudent)) {
                relevantActivities.add(activity);
            }
        }

        // Initial adapter setup with join listener
        ActivityAdapter adapter = new ActivityAdapter(relevantActivities, AdapterMode.JOIN);
        adapter.setOnJoinClickListener(activity -> {
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
        recyclerView.setAdapter(adapter);

        // Filter button logic
        filterBtn.setOnClickListener(v -> {
            FilterJoinDialogFragment filterDialog = new FilterJoinDialogFragment();
            filterDialog.setFilterListener((domain) -> filterActivities(domain));
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
                                    // Save join date from temporary map
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
            Date endDate = activity.getEndDate();
            Date today = new Date(); // Current date

            // Check if the activity has already ended
            if (endDate.before(today)) {
                return false;
            }

            String ageRange = activity.getAgeRange(); // e.g., "12–15"
            String[] parts = ageRange.split("[-–]");
            int minAge = Integer.parseInt(parts[0].trim());
            int maxAge = Integer.parseInt(parts[1].trim());

            int studentAge = student.getAge();
            if (studentAge < minAge || studentAge > maxAge) {
                return false;
            }


            String daysString = activity.getDays();
            String[] activityDaysArray = daysString.split(",");
            List<String> activityDays = new ArrayList<>();
            for (String day : activityDaysArray) {
                activityDays.add(day.trim());
            }

            List<String> studentFreeDays = student.getFreeDays();
            for (String day : activityDays) {
                if (!studentFreeDays.contains(day)) {
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
