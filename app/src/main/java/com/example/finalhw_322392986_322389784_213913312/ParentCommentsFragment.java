package com.example.finalhw_322392986_322389784_213913312;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalhw_322392986_322389784_213913312.logic_model.Activity;
import com.example.finalhw_322392986_322389784_213913312.logic_model.Student;
import com.example.finalhw_322392986_322389784_213913312.logic_model.ActivityAdapter;
import com.example.finalhw_322392986_322389784_213913312.AdapterMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.*;

public class ParentCommentsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyText;
    private Student student;
    private List<Activity> joinedActivities = new ArrayList<>();
    private Map<String, String> parentReplies = new HashMap<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String studentId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parent_comments, container, false);

        recyclerView = view.findViewById(R.id.parentCommentsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        emptyText = view.findViewById(R.id.emptyTextView);

        fetchLinkedStudentId();

        return view;
    }

    // Step 1: Get childIds from the currently logged-in parent
    private void fetchLinkedStudentId() {
        String parentId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(parentId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<String> childIds = (List<String>) snapshot.get("childIds");
                    if (childIds != null && !childIds.isEmpty()) {
                        studentId = childIds.get(0); // we assume one child for now
                        fetchStudentData();
                    } else {
                        Toast.makeText(getContext(), "No child linked to this parent", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.e("PARENT_COMMENTS", "Failed to fetch parent data", e));
    }

    // Step 2: Fetch student data using studentId
    private void fetchStudentData() {
        db.collection("users").document(studentId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        student = doc.toObject(Student.class);
                        student.setUid(doc.getId());

                        Object parentRepliesObj = doc.get("parentReplies");
                        if (parentRepliesObj instanceof Map) {
                            Map<?, ?> rawMap = (Map<?, ?>) parentRepliesObj;
                            for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                                if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
                                    parentReplies.put((String) entry.getKey(), (String) entry.getValue());
                                }
                            }
                        }

                        fetchJoinedActivities();
                    } else {
                        Toast.makeText(getContext(), "Student not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.e("PARENT_COMMENTS", "Failed to fetch student", e));
    }

    private void fetchJoinedActivities() {
        if (student.getRegisteredActivityIds() == null || student.getRegisteredActivityIds().isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            return;
        }

        db.collection("activities")
                .get()
                .addOnSuccessListener(snapshot -> {
                    joinedActivities.clear();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Activity activity = doc.toObject(Activity.class);
                        if (activity != null && student.getRegisteredActivityIds().contains(doc.getId())) {
                            activity.setActivityId(doc.getId());
                            joinedActivities.add(activity);
                        }
                    }

                    if (joinedActivities.isEmpty()) {
                        emptyText.setVisibility(View.VISIBLE);
                    } else {
                        emptyText.setVisibility(View.GONE);
                        setupAdapter();
                    }
                })
                .addOnFailureListener(e -> Log.e("PARENT_COMMENTS", "Failed to fetch activities", e));
    }

    private void setupAdapter() {
        ActivityAdapter adapter = new ActivityAdapter(joinedActivities, AdapterMode.PARENT_COMMENT);
        adapter.setOnParentCommentClickListener(activity -> {
            String activityId = activity.getActivityId();
            String guideComment = student.getComments() != null ? student.getComments().get(activityId) : null;
            String parentReply = parentReplies.get(activityId);
            showReplyDialog(activityId, guideComment, parentReply);
        });
        recyclerView.setAdapter(adapter);
    }

    private void showReplyDialog(String activityId, String guideComment, String existingReply) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Reply to Guide's Comment");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(existingReply != null ? existingReply : "");
        builder.setView(input);

        builder.setMessage("Guide: " + (guideComment != null ? guideComment : "No comment from guide."));
        builder.setPositiveButton("Submit", (dialog, which) -> {
            String reply = input.getText().toString().trim();

            if (reply.isEmpty()) {
                Toast.makeText(getContext(), "Reply cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            parentReplies.put(activityId, reply);

            db.collection("users")
                    .document(student.getUid())
                    .update("parentReplies", parentReplies)
                    .addOnSuccessListener(unused -> Toast.makeText(getContext(), "Reply saved", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> {
                        Log.e("PARENT_COMMENTS", "Failed to update parent reply", e);
                        Toast.makeText(getContext(), "Failed to save reply", Toast.LENGTH_SHORT).show();
                    });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
