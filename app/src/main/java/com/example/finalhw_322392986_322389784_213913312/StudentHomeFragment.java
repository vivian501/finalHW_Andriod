package com.example.finalhw_322392986_322389784_213913312;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class StudentHomeFragment extends Fragment {
    private Button joinActBtn, browseBtn, reportBtn, monthlyBtn;
    private Fragment browseActFrag, joinActFrag, progReportFrag, monthlyActFrag;

    @SuppressLint("MissingInflatedId")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_home, container, false);
        
        joinActBtn = view.findViewById(R.id.joinActBtn);
        browseBtn = view.findViewById(R.id.browseActBtn);
        reportBtn = view.findViewById(R.id.progReportBtn);
        monthlyBtn = view.findViewById(R.id.monthlyActivitiesBtn);

        browseActFrag = new BrowseActivitiesFragment();
        joinActFrag = new JoinActivitiesFragment();
        monthlyActFrag = new MyMonthlyActivitiesFragment();
        progReportFrag = new ProgressReportFragment();

        browseBtn.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, browseActFrag)
                    .addToBackStack(null)
                    .commit();
        });

        monthlyBtn.setOnClickListener(v ->{
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, monthlyActFrag)
                    .addToBackStack(null)
                    .commit();

        });

        reportBtn.setOnClickListener(v ->{
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, progReportFrag)
                    .addToBackStack(null)
                    .commit();

        });

        joinActBtn.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            int dayOfMonth = now.get(Calendar.DAY_OF_MONTH);
            if(dayOfMonth <= 7) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, joinActFrag)
                        .addToBackStack(null)
                        .commit();
            }
            else {
                Toast.makeText(requireContext(), "You can only join activities in the first week of the month, come back next month!", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}
