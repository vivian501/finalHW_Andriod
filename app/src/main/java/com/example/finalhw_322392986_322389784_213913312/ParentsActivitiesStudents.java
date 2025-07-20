package com.example.finalhw_322392986_322389784_213913312;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParentsActivitiesStudents extends Fragment {

    private RecyclerView recyclerView;
    private ActivityAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d("DEBUG", "ParentsActivitiesStudents onCreateView triggered");

        View view = inflater.inflate(R.layout.fragment_parents_activites_students, container, false);
        recyclerView = view.findViewById(R.id.recyclerParentsStudentsActivities);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        String parentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("DEBUG", "Current parent UID: " + parentUid);
        fetchActivitiesForChildren(parentUid);

        return view;
    }

    private void fetchActivitiesForChildren(String parentUid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(parentUid).get()
                .addOnSuccessListener(parentDoc -> {
                    List<String> childIds = (List<String>) parentDoc.get("childIds");

                    if (childIds == null || childIds.isEmpty()) {
                        Toast.makeText(getContext(), "No children registered.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    db.collection("activities")
                            .get()
                            .addOnSuccessListener(querySnapshot -> {
                                List<Activity> filtered = new ArrayList<>();
                                Log.d("DEBUG", "Calling setupAdapter with " + filtered.size() + " activities");

                                Map<String, List<Student>> activityToStudentsMap = new HashMap<>();
                                Date now = new Date();

                                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                    Activity activity = doc.toObject(Activity.class);
                                    if (activity == null || activity.getStartDate() == null) continue;

                                    if (!activity.getStartDate().after(now)) {
                                        List<String> joinedStudentIds = activity.getJoinedStudentsIds()                ; // assumes studentIds field exists

                                        if (joinedStudentIds != null) {
                                            Set<String> intersection = new HashSet<>(joinedStudentIds);
                                            intersection.retainAll(childIds);
                                            Log.d("DEBUG", "Parent UID: " + parentUid);
                                            Log.d("DEBUG", "Fetched child IDs: " + childIds);
                                            Log.d("DEBUG", "Fetched " + querySnapshot.size() + " activities");
                                            Log.d("DEBUG", "Calling setupAdapter with " + filtered.size() + " activities");


                                            if (!intersection.isEmpty()) {
                                                activity.setActivityId(doc.getId());
                                                filtered.add(activity);

                                                List<Student> matchedChildren = new ArrayList<>();
                                                for (String childId : intersection) {
                                                    Student child = new Student();
                                                    child.setUid(childId);
                                                    matchedChildren.add(child);
                                                }

                                                activityToStudentsMap.put(activity.getActivityId(), matchedChildren);
                                            }
                                        }
                                    }
                                }

                                if (filtered.isEmpty()) {
                                    Toast.makeText(getContext(), "No activities found for your children.", Toast.LENGTH_SHORT).show();
                                } else {
                                    setupAdapter(filtered, activityToStudentsMap);
                                }
                            });
                })

                .addOnFailureListener(e -> Log.e("PARENT_FETCH", "Failed to fetch parent data", e));

    }

    private void setupAdapter(List<Activity> activities, Map<String, List<Student>> activityToStudentsMap) {
        adapter = new ActivityAdapter(activities, AdapterMode.GUIDE);

        adapter.setOnRateClickListener(activity -> {
            List<Student> joinedStudents = activityToStudentsMap.getOrDefault(activity.getActivityId(), new ArrayList<>());

            RateStudentsFragment fragment = RateStudentsFragment.newInstance(activity.getActivityId(), joinedStudents);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(adapter);
    }
}
