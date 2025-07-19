package com.example.finalhw_322392986_322389784_213913312;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MyMonthlyActivitiesFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView title;
    private Student currentStudent;
    private List<Activity> allActivities;

    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_monthly_act, container, false);

        title = view.findViewById(R.id.titleText);
        recyclerView = view.findViewById(R.id.monthlyActivitiesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        currentStudent = DummyDataProvider.getDummyStudent(); // Replace with real logic (firebase)
        allActivities = DummyDataProvider.getDummyActivities(); // Replace with real logic (firebase)

        List<Activity> monthlyActivities = getActivitiesJoinedThisMonth(currentStudent, allActivities);

        ActivityAdapter adapter = new ActivityAdapter(monthlyActivities, AdapterMode.EDIT);

        // this method is for deleting an activity from the list of joined activities for this month
        //it also deletes the activity from the students registered activities list
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
                                Toast.makeText(requireContext(), "Activity removed", Toast.LENGTH_SHORT).show();

                                // Refresh list
                                List<Activity> updatedList = getActivitiesJoinedThisMonth(currentStudent, allActivities);
                                adapter.updateActivityList(updatedList);
                            })
                            .setNegativeButton("No", null)
                            .show();
                } else {
                    Toast.makeText(requireContext(), "You can only remove activities within 7 days of joining", Toast.LENGTH_LONG).show();
                }
            }
        });


        recyclerView.setAdapter(adapter);
        return view;
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
