package com.example.finalhw_322392986_322389784_213913312;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finalhw_322392986_322389784_213913312.logic_model.Activity;
import com.example.finalhw_322392986_322389784_213913312.logic_model.Student;

import java.util.List;
import java.util.Map;

public class GeneralReportFragment extends Fragment {

    private TextView activityCountText, activityCountLabel, ratingLabel;
    private ProgressBar scienceProgress, socialProgress, creativityProgress, ratingProgress;

    private Student currentStudent;
    private List<Activity> allActivities;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monthly_general_report, container, false);

        activityCountText = view.findViewById(R.id.activityCountNumber);
        activityCountLabel = view.findViewById(R.id.activityCountLabel);
        ratingLabel = view.findViewById(R.id.ratingLabel);
        ratingProgress = view.findViewById(R.id.ratingProgress);
        scienceProgress = view.findViewById(R.id.scienceProgress);
        socialProgress = view.findViewById(R.id.socialProgress);
        creativityProgress = view.findViewById(R.id.creativityProgress);

        activityCountLabel.setText("Number Of Activities Joined");

        currentStudent = DummyDataProvider.getDummyStudent();
        allActivities = DummyDataProvider.getDummyActivities();

        updateReport(view);

        return view;
    }

    private void updateReport(View view) {
        if (currentStudent == null || currentStudent.getRegisteredActivityIds() == null) return;

        List<String> registeredIds = currentStudent.getRegisteredActivityIds();
        activityCountText.setText(String.valueOf(registeredIds.size()));

        // Domain distribution
        int science = 0, social = 0, creativity = 0;
        for (Activity activity : allActivities) {
            if (registeredIds.contains(activity.getActivityId())) {
                switch (activity.getDomain()) {
                    case "Science": science++; break;
                    case "Social": social++; break;
                    case "Creativity": creativity++; break;
                }
            }
        }

        int total = science + social + creativity;
        if (total == 0) total = 1; // prevent division by 0 and ensure max is not 0

        // Safely set max and progress
        scienceProgress.setMax(total);
        socialProgress.setMax(total);
        creativityProgress.setMax(total);

        scienceProgress.setProgress(science);
        socialProgress.setProgress(social);
        creativityProgress.setProgress(creativity);

        // Set domain labels using the safe 'view'
        TextView scienceLabel = view.findViewById(R.id.scienceLabel);
        TextView socialLabel = view.findViewById(R.id.socialLabel);
        TextView creativityLabel = view.findViewById(R.id.creativityLabel);
        scienceLabel.setText("Science: " + science);
        socialLabel.setText("Social: " + social);
        creativityLabel.setText("Creativity: " + creativity);

        // Rating logic from Map<String, Integer>
        Map<String, Integer> ratingsMap = currentStudent.getRatingsByActivity();
        if (ratingsMap != null && !ratingsMap.isEmpty()) {
            int sum = 0;
            for (int rating : ratingsMap.values()) {
                sum += rating;
            }
            int avgRating = Math.round((float) sum / ratingsMap.size());
            ratingProgress.setProgress(avgRating);
            ratingLabel.setText("Average Rating: " + avgRating + "/10");
        } else {
            ratingProgress.setProgress(0);
            ratingLabel.setText("Average Rating: N/A");
        }
    }
}
