package com.example.finalhw_322392986_322389784_213913312;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalhw_322392986_322389784_213913312.logic_model.Activity;
import com.example.finalhw_322392986_322389784_213913312.logic_model.ActivityAdapter;
import com.example.finalhw_322392986_322389784_213913312.logic_model.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.*;

public class MyMonthlyActivitiesFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView title;
    private Student currentStudent;
    private List<Activity> allActivities = new ArrayList<>();
    private ActivityAdapter adapter;

    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_monthly_act, container, false);

        title = view.findViewById(R.id.titleText);
        recyclerView = view.findViewById(R.id.monthlyActivitiesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchCurrentStudent();

        return view;
    }

    // Fetch current logged-in student data from Firebase
    private void fetchCurrentStudent() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    currentStudent = doc.toObject(Student.class);
                    if (currentStudent != null) {
                        currentStudent.setUid(doc.getId());
                        fetchAllActivities();
                    }
                })
                .addOnFailureListener(e -> Log.e("FETCH_STUDENT", "Failed to fetch student", e));
    }

    // Fetch all activities from Firebase
    private void fetchAllActivities() {
        FirebaseFirestore.getInstance().collection("activities")
                .get()
                .addOnSuccessListener(snapshot -> {
                    allActivities.clear();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Activity activity = doc.toObject(Activity.class);
                        if (activity != null) {
                            activity.setActivityId(doc.getId());
                            allActivities.add(activity);
                        }
                    }
                    setupRecycler();
                })
                .addOnFailureListener(e -> Log.e("FETCH_ACTIVITIES", "Failed to fetch activities", e));
    }

    private void setupRecycler() {
        List<Activity> monthlyActivities = getActivitiesJoinedThisMonth(currentStudent, allActivities);
        adapter = new ActivityAdapter(monthlyActivities, AdapterMode.EDIT);

        // this method is for deleting an activity from the list of joined activities for this month
        // it also deletes the activity from the students registered activities list
        // it uses a listener and displays feedback and an alert dialog to confirm the users actions
        adapter.setOnDeleteClickListener(activity -> {
            String activityId = activity.getActivityId();
            Map<String, Date> joinDates = currentStudent.getJoinedActivityDates();

            if (joinDates != null && joinDates.containsKey(activityId)) {
                Date joinDate = joinDates.get(activityId);
                if (isWithinWeek(joinDate)) { // a call to a method that checks if the activity is being deleted within a week of joining it
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Confirm Deletion")
                            .setMessage("Are you sure you want to delete \"" + activity.getName() + "\"?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                currentStudent.getRegisteredActivityIds().remove(activityId);
                                joinDates.remove(activityId);

                                // Update Firebase with new data (removes the activity from the lists)
                                FirebaseFirestore.getInstance().collection("users")
                                        .document(currentStudent.getUid())
                                        .update(
                                                "registeredActivityIds", currentStudent.getRegisteredActivityIds(),
                                                "joinedActivityDates", currentStudent.getJoinedActivityDates()
                                        )
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(requireContext(), "Activity removed", Toast.LENGTH_SHORT).show();
                                            List<Activity> updatedList = getActivitiesJoinedThisMonth(currentStudent, allActivities);
                                            adapter.updateActivityList(updatedList);
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(requireContext(), "Failed to update student in Firestore", Toast.LENGTH_SHORT).show();
                                            Log.e("FIRESTORE_UPDATE", "Error updating student document", e);
                                        });


                            })
                            .setNegativeButton("No", null)
                            .show();
                } else {
                    Toast.makeText(requireContext(), "You can only remove activities within 7 days of joining", Toast.LENGTH_LONG).show();
                }
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private List<Activity> getActivitiesJoinedThisMonth(Student student, List<Activity> allActivities) {
        List<Activity> result = new ArrayList<>();

        if (student.getRegisteredActivityIds() == null || student.getJoinedActivityDates() == null)
            return result;

        Calendar now = Calendar.getInstance();
        int currentMonth = now.get(Calendar.MONTH);
        int currentYear = now.get(Calendar.YEAR);

        for (Activity activity : allActivities) {
            String activityId = activity.getActivityId();

            if (student.getRegisteredActivityIds().contains(activityId)) {
                Date joinDate = student.getJoinedActivityDates().get(activityId);
                if (joinDate != null) {
                    Calendar joinCal = Calendar.getInstance();
                    joinCal.setTime(joinDate);

                    if (joinCal.get(Calendar.MONTH) == currentMonth &&
                            joinCal.get(Calendar.YEAR) == currentYear) {
                        result.add(activity);
                    }
                }
            }
        }

        return result;
    }

    // this method is used to calculate the time differance between today and the day the activity was joined
    // if the activity was joined more than a week ago the method returns false
    // and if the date of joining the activity is within a week the method returns true
    private boolean isWithinWeek(Date joinDate) {
        Calendar joinCal = Calendar.getInstance();
        joinCal.setTime(joinDate);

        Calendar now = Calendar.getInstance();
        long diffMillis = now.getTimeInMillis() - joinCal.getTimeInMillis(); //calculates the differance in the join date and today's date to check if 7 days has passed
        long daysSinceJoin = diffMillis / (1000 * 60 * 60 * 24);

        return daysSinceJoin <= 7;
    }
}
