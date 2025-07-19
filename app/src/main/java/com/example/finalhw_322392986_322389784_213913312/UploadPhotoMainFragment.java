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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UploadPhotoMainFragment extends Fragment {

    private RecyclerView recyclerView;
    private ActivityAdapter adapter;

    private List<Activity> allActivities;
    private Guide currentGuide;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guides_activities_students, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewGuideActivities);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Replace with actual logged-in guide when sync is done
        currentGuide = DummyDataProvider.getDummyGuide();
        allActivities = DummyDataProvider.getDummyActivities();

        List<Activity> filtered = getActivitiesForCurrentGuide();

        adapter = new ActivityAdapter(filtered, AdapterMode.GUIDE);


        adapter.setOnRateClickListener(activity -> {
            Fragment fragment = PhotoUploaderFragment.newInstance(activity.getActivityId());

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
     * - Have already started
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
}
