package com.example.finalhw_322392986_322389784_213913312.logic_model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalhw_322392986_322389784_213913312.R;

import java.util.List;

public class StudentRatingAdapter extends RecyclerView.Adapter<StudentRatingAdapter.StudentViewHolder> {

    private final List<Student> students;
    private final String activityId;
    private final Context context;
    private final OnRatingSubmittedListener ratingListener;

    // Interface to delegate rating logic to the fragment
    public interface OnRatingSubmittedListener {
        void onRatingSubmitted(Student student, int rating, String comment);
    }

    public StudentRatingAdapter(Context context, List<Student> students, String activityId, OnRatingSubmittedListener listener) {
        this.context = context;
        this.students = students;
        this.activityId = activityId;
        this.ratingListener = listener;
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

        // Load rating
        int totalRating = 0;
        if (student.getRatingsByActivity() != null && student.getRatingsByActivity().containsKey(activityId)) {
            totalRating = student.getRatingsByActivity().get(activityId);
        }

        int leftRating = Math.min(5, totalRating);
        holder.ratingBar1.setRating(leftRating);

        // Load comment
        if (student.getComments() != null && student.getComments().containsKey(activityId)) {
            holder.commentEditText.setText(student.getComments().get(activityId));
        }

        // Trigger on rating change
        holder.ratingBar1.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                int roundedRating = Math.round(rating);
                ratingBar.setRating(roundedRating); // Round to whole numbers
                String currentComment = holder.commentEditText.getText().toString().trim();
                ratingListener.onRatingSubmitted(student, roundedRating, currentComment);
            }
        });

        //  NEW: Trigger on comment text change (not just on focus loss)
        holder.commentEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int currentRating = Math.round(holder.ratingBar1.getRating());
                ratingListener.onRatingSubmitted(student, currentRating, s.toString().trim());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
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


        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.studentNameText);
            ratingBar1 = itemView.findViewById(R.id.ratingBarLeft);
            commentEditText = itemView.findViewById(R.id.commentEditText);
        }
    }
}
