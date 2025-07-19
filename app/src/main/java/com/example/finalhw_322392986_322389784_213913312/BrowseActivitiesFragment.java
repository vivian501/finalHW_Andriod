package com.example.finalhw_322392986_322389784_213913312;

import static android.view.View.GONE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalhw_322392986_322389784_213913312.logic_model.Activity;
import com.example.finalhw_322392986_322389784_213913312.logic_model.ActivityAdapter;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;


public class BrowseActivitiesFragment extends Fragment {
    private RecyclerView recyclerView;
    private Button filterBtn, resetBtn, saveBtn, cancelBtn ,createActivityBtn;
    private List<Activity> allActivities; //for dummy data

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activities_browser_join, container, false);

        filterBtn = view.findViewById(R.id.filterBtn);
        resetBtn = view.findViewById(R.id.resetBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);
        saveBtn = view.findViewById(R.id.saveBtn);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        createActivityBtn = view.findViewById(R.id.createActivityBtn);

        // Hiding certain buttons for them to only appear for some of the users not all
        createActivityBtn.setVisibility(GONE);
        saveBtn.setVisibility(GONE);
        cancelBtn.setVisibility(GONE);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userType = documentSnapshot.getString("userType");
                        if ("Coordinator".equalsIgnoreCase(userType)) {
                            createActivityBtn.setVisibility(View.VISIBLE);
                            createActivityBtn.setOnClickListener(v -> {
                                Fragment fragment = new CreateNewActivityFragment(); // this is your activity creation fragment
                                requireActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.fragment_container, fragment) // change to your actual container ID
                                        .addToBackStack(null)
                                        .commit();
                            });
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to fetch user role", Toast.LENGTH_SHORT).show()
                );


       //fetching all the activites from the firebase by using this function:
        fetchActivitiesFromFirestore();



        filterBtn.setOnClickListener(v -> {
            FilterDialogFragment filterDialog = new FilterDialogFragment();
            filterDialog.setFilterListener((domain, days, minAge, maxAge, maxParticipants, description) ->
                    filterActivities(domain, days, minAge, maxAge, maxParticipants, description)
            );
            filterDialog.show(getParentFragmentManager(), "FilterDialog");
        });

        resetBtn.setOnClickListener(v -> {
            recyclerView.setAdapter(new ActivityAdapter(allActivities)); // show all again
        });

        return view;
    }
    private void fetchActivitiesFromFirestore() {
        FirebaseFirestore.getInstance().collection("activities")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    allActivities = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Activity activity = doc.toObject(Activity.class);
                        if (activity != null) {
                            allActivities.add(activity);
                        }
                    }

                    recyclerView.setAdapter(new ActivityAdapter(allActivities, AdapterMode.BROWSE));
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch activities", Toast.LENGTH_SHORT).show();
                });
    }



    private void filterActivities(String domain, List<String> days, Integer minAge, Integer maxAge, Integer maxParticipants, String description) {
        List<Activity> filtered = new ArrayList<>();

        for (Activity activity : allActivities) {
            boolean match = true;

            // Filter domain if selected
            if (domain != null && !activity.getDomain().equalsIgnoreCase(domain)) match = false;

            // Filter days if selected
            if (days != null && !days.isEmpty()) {
                List<String> activityDays = activity.getDays();
                boolean dayMatch = false;
                for (String selectedDay : days) {
                    if (activityDays.contains(selectedDay)) {
                        dayMatch = true;
                        break;
                    }
                }
                if (!dayMatch) match = false;
            }

            // Filter min age
            if (minAge != null && activity.getMinAge() < minAge) match = false;

            // Filter max age
            if (maxAge != null && activity.getMaxAge() > maxAge) match = false;

            // Filter max participants
            if (maxParticipants != null && activity.getMaxParticipants() > maxParticipants) match = false;

            // Filter description
            if (description != null && !description.isEmpty() &&
                    !activity.getDescription().toLowerCase().contains(description.toLowerCase())) match = false;

            if (match) filtered.add(activity);
        }

        recyclerView.setAdapter(new ActivityAdapter(filtered));
    }
}


