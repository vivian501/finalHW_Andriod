package com.example.finalhw_322392986_322389784_213913312;

import android.os.Bundle;
import android.util.Log;
import android.view.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.*;

public class ParenImagesGallery extends Fragment {

    private RecyclerView recyclerView;
    private static final String TAG = "ParentPhotoGallery";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_parents_images_gallery, container, false);
        recyclerView = view.findViewById(R.id.recyclerPhotos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        String parentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fetchChildIds(parentUid);

        return view;
    }

    private void fetchChildIds(String parentUid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(parentUid).get().addOnSuccessListener(parentDoc -> {
            List<String> childIds = (List<String>) parentDoc.get("childIds");

            if (childIds == null || childIds.isEmpty()) {
                showSnackbar("No registered children found.");
                return;
            }

            fetchActivitiesWithPhotos(childIds);
        }).addOnFailureListener(e ->
                Log.e(TAG, "Failed to fetch parent data", e));
    }

    private void fetchActivitiesWithPhotos(List<String> childIds) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("activities").get().addOnSuccessListener(querySnapshot -> {
            List<String> photoList = new ArrayList<>();

            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                List<String> joinedStudentIds = (List<String>) doc.get("joinedStudentsIds");

                if (joinedStudentIds != null) {
                    for (String childId : childIds) {
                        if (joinedStudentIds.contains(childId)) {
                            List<String> photos = (List<String>) doc.get("photos");
                            if (photos != null) {
                                photoList.addAll(photos);
                            }
                            break;
                        }
                    }
                }
            }

            if (photoList.isEmpty()) {
                showSnackbar("No activity photos found for your children.");
            }

            setupPhotoAdapter(photoList);
        }).addOnFailureListener(e ->
                Log.e(TAG, "Failed to fetch activities", e));
    }

    private void setupPhotoAdapter(List<String> photos) {
        PhotoAdapter adapter = new PhotoAdapter(photos);
        recyclerView.setAdapter(adapter);
    }

    private void showSnackbar(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        }
    }

}
