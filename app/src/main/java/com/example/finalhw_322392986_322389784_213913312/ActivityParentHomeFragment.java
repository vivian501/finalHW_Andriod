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

    private Button ratingBtn, browseBtn, pictureBtn, commentBtn; // added comment button

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_parent_home_fragment, container, false);

        ratingBtn = view.findViewById(R.id.ratingBtn);
        pictureBtn = view.findViewById(R.id.pictureBtn);
        browseBtn = view.findViewById(R.id.browseActBtn);
        commentBtn = view.findViewById(R.id.commentBtn); // must exist in XML

        ratingBtn.setOnClickListener(v -> {
            // Navigate to the fragment that shows activities the parents can rate
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ParentsActivitiesStudents())
                    .addToBackStack(null)
                    .commit();
        });

        pictureBtn.setOnClickListener(v -> {
            // Navigate to the fragment that shows activities the parents can see pictures for
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ParenImagesGallery())
                    .addToBackStack(null)
                    .commit();
        });

        browseBtn.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new BrowseActivitiesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        commentBtn.setOnClickListener(v -> {
            // Navigate to the new fragment that displays guide comments + reply field
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ParentCommentsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
