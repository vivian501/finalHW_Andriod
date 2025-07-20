package com.example.finalhw_322392986_322389784_213913312;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalhw_322392986_322389784_213913312.logic_model.Activity;
import com.example.finalhw_322392986_322389784_213913312.logic_model.ActivityAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BrowseActivitiesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ActivityAdapter adapter;
    private List<Activity> activityList = new ArrayList<>();
    private int selectedPosition = -1;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activities_browser_join, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ActivityAdapter(activityList, new ActivityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Activity activity, int position) {
                selectedPosition = position;
                adapter.setSelectedPosition(position);
            }
        });
        recyclerView.setAdapter(adapter);

        Button newActivityBtn = view.findViewById(R.id.newActivityBtn);
        Button editActivityBtn = view.findViewById(R.id.editActivityBtn);
        Button deleteActivityBtn = view.findViewById(R.id.deleteActivityBtn);

        newActivityBtn.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new CreateNewActivityFragment())
                    .addToBackStack(null)
                    .commit();
        });

        editActivityBtn.setOnClickListener(v -> {
            if (selectedPosition != -1) {
                Activity selected = activityList.get(selectedPosition);
                CreateNewActivityFragment fragment = new CreateNewActivityFragment();
                Bundle args = new Bundle();
                args.putBoolean("isEditMode", true);
                args.putString("activityId", selected.getActivityId());
                fragment.setArguments(args);

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(getContext(), "Select an activity to edit", Toast.LENGTH_SHORT).show();
            }
        });

        deleteActivityBtn.setOnClickListener(v -> {
            if (selectedPosition != -1) {
                Activity selected = activityList.get(selectedPosition);
                db.collection("activities").document(selected.getActivityId())
                        .delete()
                        .addOnSuccessListener(unused -> {
                            activityList.remove(selectedPosition);
                            adapter.notifyItemRemoved(selectedPosition);
                            selectedPosition = -1;
                            Toast.makeText(getContext(), "Activity deleted", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(getContext(), "Delete failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(getContext(), "Select an activity to delete", Toast.LENGTH_SHORT).show();
            }
        });

        loadActivities();
    }

    private void loadActivities() {
        db.collection("activities")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    activityList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Activity activity = doc.toObject(Activity.class);
                        activity.setActivityId(doc.getId());
                        activityList.add(activity);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load activities", Toast.LENGTH_SHORT).show());
    }
}
