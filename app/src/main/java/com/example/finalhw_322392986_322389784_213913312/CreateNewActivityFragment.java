package com.example.finalhw_322392986_322389784_213913312;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateNewActivityFragment extends Fragment {

    private EditText titleEditText, descriptionEditText,
           maxParticipantsEditText,
            daysEditText, startDateEditText, endDateEditText;
    private CheckBox mondayCheckbox, tuesdayCheckbox, wednesdayCheckbox, thursdayCheckbox,
            fridayCheckbox, saturdayCheckbox, sundayCheckbox;
    private Spinner domainSpinner, subDomainSpinner, minAgeSpinner, maxAgeSpinner, guideSpinner;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private Map<String, String> guideNameToUid = new HashMap<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_new_activity_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        titleEditText = view.findViewById(R.id.titleEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        minAgeSpinner = view.findViewById(R.id.minAgeSpinner);
        maxAgeSpinner = view.findViewById(R.id.maxAgeSpinner);
        guideSpinner = view.findViewById(R.id.guideSpinner);
        maxParticipantsEditText = view.findViewById(R.id.maxParticipantsEditText);


        mondayCheckbox = view.findViewById(R.id.mondayCheckbox);
        tuesdayCheckbox = view.findViewById(R.id.tuesdayCheckbox);
        wednesdayCheckbox = view.findViewById(R.id.wednesdayCheckbox);
        thursdayCheckbox = view.findViewById(R.id.thursdayCheckbox);
        fridayCheckbox = view.findViewById(R.id.fridayCheckbox);
        saturdayCheckbox = view.findViewById(R.id.saturdayCheckbox);
        sundayCheckbox = view.findViewById(R.id.sundayCheckbox);

        startDateEditText = view.findViewById(R.id.startDateEditText);
        endDateEditText = view.findViewById(R.id.endDateEditText);

        startDateEditText.setOnClickListener(v -> showDatePicker(startDateEditText));
        endDateEditText.setOnClickListener(v -> showDatePicker(endDateEditText));

        domainSpinner = view.findViewById(R.id.domainCategorySpinner);
        subDomainSpinner = view.findViewById(R.id.subDomainSpinner);

        view.findViewById(R.id.saveActivityBtn).setOnClickListener(v -> saveActivity());

        //  Populate min age spinner (12-17)
        List<Integer> minAges = new ArrayList<>();
        for (int i = 12; i <= 17; i++) minAges.add(i);
        ArrayAdapter<Integer> minAgeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, minAges);
        minAgeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minAgeSpinner.setAdapter(minAgeAdapter);

//  Populate max age spinner (13-18)
        List<Integer> maxAges = new ArrayList<>();
        for (int i = 13; i <= 18; i++) maxAges.add(i);
        ArrayAdapter<Integer> maxAgeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, maxAges);
        maxAgeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maxAgeSpinner.setAdapter(maxAgeAdapter);

// Fetch guides from Firestore and populate spinner
        db.collection("users")
                .whereEqualTo("userType", "Activity guide")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<String> guideNames = new ArrayList<>();
                    guideNameToUid.clear(); // clear any previous data

                    for (var doc : querySnapshot.getDocuments()) {
                        String name = doc.getString("user name");
                        String uid = doc.getId(); // ✅ Firestore UID

                        if (name != null && uid != null) {
                            guideNames.add(name);
                            guideNameToUid.put(name, uid); // Map name → UID
                        }
                    }

                    if (isAdded() && getContext() != null) {
                        ArrayAdapter<String> guideAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, guideNames);
                        guideAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        guideSpinner.setAdapter(guideAdapter);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load guides: " + e.getMessage(), Toast.LENGTH_SHORT).show());



        Map<String, List<String>> domainMap = new HashMap<>();
        domainMap.put("Science", Arrays.asList("biology", "robotics", "physics", "math"));
        domainMap.put("Social", Arrays.asList("leadership", "public speaking", "team working"));
        domainMap.put("Creativity", Arrays.asList("arts", "writing", "music"));

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new ArrayList<>(domainMap.keySet()));
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        domainSpinner.setAdapter(categoryAdapter);

        domainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                List<String> subdomains = domainMap.get(selectedCategory);
                ArrayAdapter<String> subAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, subdomains);
                subAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                subDomainSpinner.setAdapter(subAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });


    }

    //helper method for opening date picker
    private void showDatePicker(EditText targetEditText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    String date = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth);
                    targetEditText.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }


    private void saveActivity() {
        String title = titleEditText.getText().toString().trim();
        String selectedDomain = domainSpinner.getSelectedItem().toString();
        String selectedSubdomain = subDomainSpinner.getSelectedItem().toString();
        String description = descriptionEditText.getText().toString().trim();
        String maxParticipants = maxParticipantsEditText.getText().toString().trim();
        int minAge = (int) minAgeSpinner.getSelectedItem();
        int maxAge = (int) maxAgeSpinner.getSelectedItem();
        String guideName = guideSpinner.getSelectedItem().toString();
        String guideUid = guideNameToUid.get(guideName);




        List<String> selectedDays = new ArrayList<>();
        if (mondayCheckbox.isChecked()) selectedDays.add("Monday");
        if (tuesdayCheckbox.isChecked()) selectedDays.add("Tuesday");
        if (wednesdayCheckbox.isChecked()) selectedDays.add("Wednesday");
        if (thursdayCheckbox.isChecked()) selectedDays.add("Thursday");
        if (fridayCheckbox.isChecked()) selectedDays.add("Friday");
        if (saturdayCheckbox.isChecked()) selectedDays.add("Saturday");
        if (sundayCheckbox.isChecked()) selectedDays.add("Sunday");





        String startDateStr = startDateEditText.getText().toString().trim();
        String endDateStr = endDateEditText.getText().toString().trim();

        if (TextUtils.isEmpty(title) ) {
            Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> activity = new HashMap<>();
        activity.put("name", title);
        activity.put("domain", selectedDomain );
        activity.put("subDomain",selectedSubdomain);
        activity.put("description", description);
        activity.put("minAge", minAge);
        activity.put("maxAge", maxAge);
        activity.put("maxParticipants", Integer.parseInt(maxParticipants));
        activity.put("guideFullName", guideName);
        activity.put("guideId", guideUid);  //  Store UID for logic
        activity.put("days", selectedDays);
        activity.put("createdBy", auth.getCurrentUser().getUid());

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date start = sdf.parse(startDateStr);
            Date end = sdf.parse(endDateStr);
            activity.put("startDate", new Timestamp(start));
            activity.put("endDate", new Timestamp(end));
        } catch (ParseException e) {
            Toast.makeText(getContext(), "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("activities")
                .add(activity)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(getContext(), "Activity saved!", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
