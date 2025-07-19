package com.example.finalhw_322392986_322389784_213913312;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalhw_322392986_322389784_213913312.logic_model.Activity;
import com.example.finalhw_322392986_322389784_213913312.logic_model.ActivityAdapter;
import com.example.finalhw_322392986_322389784_213913312.logic_model.Guide;
import com.example.finalhw_322392986_322389784_213913312.logic_model.Student;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GuidesActivitiesStudents extends Fragment {

    private RecyclerView recyclerView;
    private ActivityAdapter adapter;

    private List<Activity> allActivities;
    private List<Student> allStudents;
    private Guide currentGuide;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guides_activities_students, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewGuideActivities);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        currentGuide = DummyDataProvider.getDummyGuide(); // Replace with actual guide from login if needed
        allActivities = DummyDataProvider.getDummyActivities(); // All activities
        allStudents = DummyDataProvider.getDummyStudents();     // All students

        List<Activity> filtered = getActivitiesForCurrentGuide();

        adapter = new ActivityAdapter(filtered, AdapterMode.GUIDE);

        // Handle clicks to open student rating fragment
        adapter.setOnRateClickListener(activity -> {
            List<Student> joined = getStudentsRegisteredForActivity(activity.getActivityId());

            RateStudentsFragment fragment = RateStudentsFragment.newInstance(activity.getActivityId(), joined);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(adapter);
        return view;
    }

    /**
     * Filters and returns activities that are:
     * - Assigned to the currently logged-in guide
     * - Have already started (start date <= current date)
     */
    private List<Activity> getActivitiesForCurrentGuide() {
        List<Activity> result = new ArrayList<>();
        Date now = new Date();

        for (Activity activity : allActivities) {
            if (activity.getGuideId().equals(currentGuide.getUid()) &&
                    activity.getStartDate() != null &&
                    !activity.getStartDate().after(now)) {
                result.add(activity);
            }
        }

        return result;
    }

    /**
     * Returns the list of students who are registered to a given activity ID.
     */
    private List<Student> getStudentsRegisteredForActivity(String activityId) {
        List<Student> result = new ArrayList<>();

        for (Student student : allStudents) {
            if (student.getRegisteredActivityIds() != null &&
                    student.getRegisteredActivityIds().contains(activityId)) {
                result.add(student);
            }
        }

        return result;
    }
}
