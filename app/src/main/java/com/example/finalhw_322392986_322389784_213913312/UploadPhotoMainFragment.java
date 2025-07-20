package com.example.finalhw_322392986_322389784_213913312;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalhw_322392986_322389784_213913312.logic_model.Activity;
import com.example.finalhw_322392986_322389784_213913312.logic_model.ActivityAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UploadPhotoMainFragment extends Fragment {

    private RecyclerView recyclerView;
    private ActivityAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guides_activities_students, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewGuideActivities);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // getting the id of the guide (logged-in user)
        String guideId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fetchActivitiesForGuide(guideId);

        return view;
    }

    // setup the adapter with activities filtered by guide and date
    private void setupAdapter(List<Activity> activities) {
        adapter = new ActivityAdapter(activities, AdapterMode.GUIDE);

        // on click go to photo upload fragment with the selected activity ID
        adapter.setOnRateClickListener(activity -> {
            Fragment fragment = PhotoUploaderFragment.newInstance(activity.getActivityId());

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(adapter);
    }

    // fetch only activities that belong to the logged-in guide and already started
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
                            activity.setActivityId(doc.getId()); // Set ID from document
                            filtered.add(activity);
                        }
                    }

                    setupAdapter(filtered);
                })
                .addOnFailureListener(e ->
                        Log.e("PHOTO_FETCH", "Failed to fetch guide activities", e));
    }


}
