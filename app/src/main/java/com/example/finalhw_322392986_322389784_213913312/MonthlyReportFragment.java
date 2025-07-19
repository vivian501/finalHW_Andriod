package com.example.finalhw_322392986_322389784_213913312;

import android.annotation.SuppressLint;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MonthlyReportFragment extends Fragment {

    private TextView activityCountText, activityCountLabel, ratingLabel;
    private ProgressBar scienceProgress, socialProgress, creativityProgress, ratingProgress;

    private Student currentStudent;
    private List<Activity> allActivities;

    private TextView scienceLabel, socialLabel, creativityLabel;

    @SuppressLint("SetTextI18n")
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

        scienceLabel = view.findViewById(R.id.scienceLabel);
        socialLabel = view.findViewById(R.id.socialLabel);
        creativityLabel = view.findViewById(R.id.creativityLabel);

        activityCountLabel.setText("Number Of Activities Joined This Month");

        currentStudent = DummyDataProvider.getDummyStudent();
        allActivities = DummyDataProvider.getDummyActivities();

        updateMonthlyReport();

        return view;
    }

    private void updateMonthlyReport() {
        List<Activity> monthlyActivities = getActivitiesJoinedThisMonth(currentStudent, allActivities);

        activityCountText.setText(String.valueOf(monthlyActivities.size()));

        int science = 0, social = 0, creativity = 0;
        int ratingSum = 0, ratingCount = 0;

        Map<String, Integer> ratingsMap = currentStudent.getRatingsByActivity();

        for (Activity activity : monthlyActivities) {
            String domain = activity.getDomain();
            switch (domain) {
                case "Science": science++; break;
                case "Social": social++; break;
                case "Creativity": creativity++; break;
            }

            if (ratingsMap != null && ratingsMap.containsKey(activity.getActivityId())) {
                ratingSum += ratingsMap.get(activity.getActivityId());
                ratingCount++;
            }
        }

        // Progress and labels
        int max = Math.max(1, Math.max(science, Math.max(social, creativity)));
        scienceProgress.setMax(max);
        socialProgress.setMax(max);
        creativityProgress.setMax(max);

        scienceProgress.setProgress(science);
        socialProgress.setProgress(social);
        creativityProgress.setProgress(creativity);

        scienceLabel.setText("Science: " + science);
        socialLabel.setText("Social: " + social);
        creativityLabel.setText("Creativity: " + creativity);

        // Rating display
        if (ratingCount > 0) {
            int avg = Math.round((float) ratingSum / ratingCount);
            ratingProgress.setProgress(avg);
            ratingLabel.setText("Average Rating: " + avg + "/10");
        } else {
            ratingProgress.setProgress(0);
            ratingLabel.setText("Average Rating: N/A");
        }
    }

    private List<Activity> getActivitiesJoinedThisMonth(Student student, List<Activity> allActivities) {
        List<Activity> result = new ArrayList<>();
        if (student.getRegisteredActivityIds() == null || student.getJoinedActivityDates() == null) return result;

        Calendar now = Calendar.getInstance();
        int currentMonth = now.get(Calendar.MONTH);
        int currentYear = now.get(Calendar.YEAR);

        for (Activity activity : allActivities) {
            String activityId = activity.getActivityId();
            if (student.getRegisteredActivityIds().contains(activityId)) {
                Date joinDate = student.getJoinedActivityDates().get(activityId);
                if (joinDate != null) {
                    Calendar joinCal = Calendar.getInstance();
                    joinCal.setTime(joinDate);
                    if (joinCal.get(Calendar.MONTH) == currentMonth && joinCal.get(Calendar.YEAR) == currentYear) {
                        result.add(activity);
                    }
                }
            }
        }

        return result;
    }
}

