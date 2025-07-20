package com.example.finalhw_322392986_322389784_213913312;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SuperintendentHomeFragment extends Fragment {

    private Button seeComment;// added comment button

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_superintendent_home, container, false);

        seeComment = view.findViewById(R.id.seeCommentsBtn);
        Log.d("DEBUG", "SuperintendentHomeFragment loaded");
        seeComment.setOnClickListener(v -> {
            // Navigate to the fragment that shows activities the parents can rate
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ParentsActivitiesStudents())
                    .addToBackStack(null)
                    .commit();
        });



        return view;
    }

}
