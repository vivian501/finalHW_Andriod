package com.example.finalhw_322392986_322389784_213913312;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

    public class ActivityParentHomeFragment extends Fragment {

    private Button ratingBtn, browseBtn, pictureBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_parent_home_fragment, container, false);

        ratingBtn = view.findViewById(R.id.ratingBtn);
        pictureBtn = view.findViewById(R.id.pictureBtn);
        browseBtn = view.findViewById(R.id.browseActBtn);

        ratingBtn.setOnClickListener(v -> {
            // Navigate to the fragment that shows activities the guide can rate
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new GuidesActivitiesStudents())
                    .addToBackStack(null)
                    .commit();
        });

        pictureBtn.setOnClickListener(v -> {
            // Navigate to the fragment that shows activities the guide can upload photos for
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new UploadPhotoMainFragment())
                    .addToBackStack(null)
                    .commit();
        });

        browseBtn.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new BrowseActivitiesFragment())
                    .addToBackStack(null)
                    .commit();
        });


        return view;
    }
}
