package com.example.finalhw_322392986_322389784_213913312.logic_model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalhw_322392986_322389784_213913312.R;

import java.util.HashMap;
import java.util.List;

public class StudentRatingAdapter extends RecyclerView.Adapter<StudentRatingAdapter.StudentViewHolder> {

    private final List<Student> students;
    private final String activityId;
    private final Context context;

    public StudentRatingAdapter(Context context, List<Student> students, String activityId) {
        this.context = context;
        this.students = students;
        this.activityId = activityId;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.student_rating_item, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = students.get(position);
        holder.nameTextView.setText(student.getFullName());

        // Load existing ratings
        int totalRating = 0;
        if (student.getRatingsByActivity() != null && student.getRatingsByActivity().containsKey(activityId)) {
            totalRating = student.getRatingsByActivity().get(activityId);
        }

        int leftRating = Math.min(5, totalRating);


        holder.ratingBar1.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            int roundedRating = Math.round(rating);
            ratingBar.setRating(roundedRating); // Force to display only whole stars, the rating must be round numbers only
        });



        // Load existing comment
        if (student.getComments() != null && student.getComments().containsKey(activityId)) {
            holder.commentEditText.setText(student.getComments().get(activityId));
        }

        // Save button logic
        holder.saveBtn.setOnClickListener(v -> {
            int combinedRating = (int) holder.ratingBar1.getRating();
            String comment = holder.commentEditText.getText().toString().trim();

            if (combinedRating < 1 || combinedRating > 10) {
                Toast.makeText(context, "Rating must be between 1 and 10", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save to student object
            if (student.getRatingsByActivity() == null) {
                student.setRatingsByActivity(new HashMap<>());
            }
            student.getRatingsByActivity().put(activityId, combinedRating);

            if (student.getComments() == null) {
                student.setComments(new HashMap<>());
            }
            student.getComments().put(activityId, comment);

            Toast.makeText(context, "Saved for " + student.getFullName(), Toast.LENGTH_SHORT).show();

            // Later: sync to Firebase or local DB here
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        RatingBar ratingBar1;
        EditText commentEditText;
        Button saveBtn;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.studentNameText);
            ratingBar1 = itemView.findViewById(R.id.ratingBarLeft);
            commentEditText = itemView.findViewById(R.id.commentEditText);
            saveBtn = itemView.findViewById(R.id.saveRatingButton);
        }
    }
}
