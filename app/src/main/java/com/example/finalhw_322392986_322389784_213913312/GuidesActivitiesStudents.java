package com.example.finalhw_322392986_322389784_213913312;

import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

        //getting id of the guide using this page :
        String guideId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fetchActivitiesForGuide(guideId);

        return view;
    }
    private void setupAdapter(List<Activity> activities) {
        adapter = new ActivityAdapter(activities, AdapterMode.GUIDE);

        adapter.setOnRateClickListener(activity -> {
            fetchStudentsForActivity(activity.getActivityId(), joinedStudents -> {
                RateStudentsFragment fragment = RateStudentsFragment.newInstance(activity.getActivityId(), joinedStudents);

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            });
        });

        recyclerView.setAdapter(adapter);
    }
    private void fetchStudentsForActivity(String activityId, java.util.function.Consumer<List<Student>> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Student> result = new ArrayList<>();

        // Get all students from "users" collection
        db.collection("users")
                .whereEqualTo("userType", "Student")
                .get()
                .addOnSuccessListener(snapshot -> {
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Student student = doc.toObject(Student.class);
                        if (student != null &&
                                student.getRegisteredActivityIds() != null &&
                                student.getRegisteredActivityIds().contains(activityId)) {
                            result.add(student);
                        }
                    }
                    callback.accept(result);

                })
                .addOnFailureListener(e ->
                        Log.e("FETCH_STUDENTS", "Failed to load students", e));
    }



    private void fetchActivitiesForGuide(String guideId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("activities")
                .whereEqualTo("guideId", guideId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Activity> filtered = new ArrayList<>();
                    Date now = new Date();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Activity activity = doc.toObject(Activity.class);
                        if (activity != null && activity.getStartDate() != null && !activity.getStartDate().after(now)) {
                            activity.setActivityId(doc.getId()); // Save UID
                            filtered.add(activity);
                        }
                    }

                    setupAdapter(filtered);
                })
                .addOnFailureListener(e ->
                        Log.e("GUIDE_FETCH", "Failed to fetch guide activities", e));
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
