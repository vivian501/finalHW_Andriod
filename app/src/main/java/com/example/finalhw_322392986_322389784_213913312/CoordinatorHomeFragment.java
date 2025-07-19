package com.example.finalhw_322392986_322389784_213913312;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CoordinatorHomeFragment extends Fragment {

    private Button browseActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_coordinator_home_fragment, container, false);
        browseActivity = view.findViewById(R.id.browseActBtn);

        browseActivity.setOnClickListener(v -> {
            // Navigate to the fragment that shows activities the guide can rate
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new BrowseActivitiesFragment())
                    .addToBackStack(null)
                    .commit();
        });
        return view;
    }
}
