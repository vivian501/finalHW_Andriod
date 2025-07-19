package com.example.finalhw_322392986_322389784_213913312;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilterDialogFragment extends DialogFragment {
    private Button cancelBtn, applyBtn;
    private Spinner maxAgeS, minAgeS, maxParticipantsS;
    private TextView headerTv, domainTv, maxAgeTv, minAgeTv, descriptionTv, maxParticipantsTv, daysTv;
    private EditText descriptionEt;
    private RadioGroup domainRG;
    private RadioButton scienceRB, socialRB, creativityRB; //radio buttons for the days
    private CheckBox monCB, sunCB, thuCB, satCB,tueCB, wedCB, friCB; //check boxes for the days
    private FilterListener listener;


    public interface FilterListener {
        void onFilterApplied(String domain, List<String> days, Integer minAge, Integer maxAge, Integer maxParticipants, String description);
    }

    public void setFilterListener(FilterListener listener) {
        this.listener = listener;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_filter, container, false);

        domainRG = view.findViewById(R.id.domainRadioGroup);
        monCB = view.findViewById(R.id.checkMon);
        tueCB = view.findViewById(R.id.checkTue);
        sunCB = view.findViewById(R.id.checkSun);
        thuCB = view.findViewById(R.id.checkThu);
        friCB = view.findViewById(R.id.checkFri);
        wedCB = view.findViewById(R.id.checkWed);
        satCB = view.findViewById(R.id.checkSat);
        maxAgeS = view.findViewById(R.id.maxAgeSpinner);
        minAgeS = view.findViewById(R.id.minAgeSpinner);
        maxParticipantsS = view.findViewById(R.id.maxPartspinner);
        headerTv = view.findViewById(R.id.filterHeaderTv);
        domainTv = view.findViewById(R.id.domainFilterTv);
        maxAgeTv = view.findViewById(R.id.maxAgeTv);
        minAgeTv = view.findViewById(R.id.minAgeTv);
        daysTv = view.findViewById(R.id.daysFilterTv);
        descriptionTv = view.findViewById(R.id.descriptionTv);
        maxParticipantsTv = view.findViewById(R.id.maxParticipantsfltrTv);
        descriptionEt = view.findViewById(R.id.descriptionEt);
        scienceRB = view.findViewById(R.id.radioScience);
        socialRB = view.findViewById(R.id.radioSocial);
        creativityRB = view.findViewById(R.id.radioCreativity);
        applyBtn = view.findViewById(R.id.applyBtn);
        cancelBtn = view.findViewById(R.id.cancleBtn);

        // add data to the max participants spinner.
        ArrayAdapter<String> participantsAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item,
                Arrays.asList("Any", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"));
        participantsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maxParticipantsS.setAdapter(participantsAdapter);

        // add data to the  max age spinner.
        ArrayAdapter<String> maxAgeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item,
                Arrays.asList("Any", "13", "14", "15", "16", "17", "18"));
        maxAgeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maxAgeS.setAdapter(maxAgeAdapter);

        // add data to the min age spinner.
        ArrayAdapter<String> minAgeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item,
                Arrays.asList("Any", "12", "13", "14", "15", "16", "17"));
        minAgeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minAgeS.setAdapter(minAgeAdapter);

       // when this button is clicked selected data for the filtering is collected
        applyBtn.setOnClickListener(v -> {
            // Get selected domain
            int selectedDomainId = domainRG.getCheckedRadioButtonId();
            String domain = null;
            if (selectedDomainId == R.id.radioScience) {
                domain = "Science";
            } else if (selectedDomainId == R.id.radioSocial) {
                domain = "Social";
            } else if (selectedDomainId == R.id.radioCreativity) {
                domain = "Creativity";
            }

            // Get selected days
            List<String> selectedDays = new java.util.ArrayList<>();
            if (sunCB.isChecked()) selectedDays.add("Sunday");
            if (monCB.isChecked()) selectedDays.add("Monday");
            if (tueCB.isChecked()) selectedDays.add("Tuesday");
            if (wedCB.isChecked()) selectedDays.add("Wednesday");
            if (thuCB.isChecked()) selectedDays.add("Thursday");
            if (friCB.isChecked()) selectedDays.add("Friday");
            if (satCB.isChecked()) selectedDays.add("Saturday");

            // Get min age
            Integer minAge = null;
            String minAgeStr = minAgeS.getSelectedItem().toString();
            if (!minAgeStr.equals("Any")) {
                minAge = Integer.parseInt(minAgeStr);
            }

            // get max age
            Integer maxAge = null;
            String maxAgeStr = maxAgeS.getSelectedItem().toString();
            if (!maxAgeStr.equals("Any")) {
                maxAge = Integer.parseInt(maxAgeStr);
            }

           // get max Participants
            Integer maxParticipants = null;
            String maxPartStr = maxParticipantsS.getSelectedItem().toString();
            if (!maxPartStr.equals("Any")) {
                maxParticipants = Integer.parseInt(maxPartStr);
            }

            // Get description (null if empty)
            String description = descriptionEt.getText().toString().trim();
            if (description.isEmpty()) {
                description = null;
            }

            // Call the listener
            if (listener != null) {
                listener.onFilterApplied(domain, selectedDays, minAge, maxAge, maxParticipants, description);
            }

            // Dismiss the dialog
            dismiss();
        });

// close the fragment without applying filtering
        cancelBtn.setOnClickListener(v -> dismiss());

        return view;
    }

}
