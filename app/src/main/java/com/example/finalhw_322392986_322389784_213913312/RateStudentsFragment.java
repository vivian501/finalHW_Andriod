package com.example.finalhw_322392986_322389784_213913312;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalhw_322392986_322389784_213913312.logic_model.Student;
import com.example.finalhw_322392986_322389784_213913312.logic_model.StudentRatingAdapter;

import java.io.Serializable;
import java.util.List;

public class RateStudentsFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button saveButton;

    private String activityId;
    private List<Student> registeredStudents;

    private StudentRatingAdapter adapter;

    public static RateStudentsFragment newInstance(String activityId, List<Student> students) {
        RateStudentsFragment fragment = new RateStudentsFragment();
        Bundle args = new Bundle();
        args.putString("activityId", activityId);
        args.putSerializable("students", (Serializable) students);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_rating, container, false);

        recyclerView = view.findViewById(R.id.studentRecycler);


        if (getArguments() != null) {
            activityId = getArguments().getString("activityId");
            registeredStudents = (List<Student>) getArguments().getSerializable("students");
        }

        adapter = new StudentRatingAdapter(requireContext(), registeredStudents, activityId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }
}
