package com.example.finalhw_322392986_322389784_213913312.logic_model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalhw_322392986_322389784_213913312.AdapterMode;
import com.example.finalhw_322392986_322389784_213913312.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {

    private List<Activity> activityList;
    private AdapterMode mode;

    public ActivityAdapter(List<Activity> activityList) {
        this.activityList = activityList;
    }
    public ActivityAdapter(List<Activity> activityList, AdapterMode mode){
        this.activityList = activityList;
        this.mode = mode;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activities_cardview, parent, false);
        return new ActivityViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {

        Activity activity = activityList.get(position);

        holder.actName.setText(activity.getName());
        holder.domainAndSubDomain.setText(activity.getDomain()+"->"+activity.getSubDomain() );
        holder.ageTv.setText("Ages:"+ activity.getMinAge()+"-"+activity.getMaxAge());
        holder.days.setText("Days: " + activity.getDays());
        holder.description.setText(activity.getDescription());
        holder.maxParticipants.setText("Max Participants: " + activity.getMaxParticipants());
        holder.guideName.setText("Guide Name: " + activity.getGuideFullName());
        holder.startDateTv.setText("Start: " + activity.getStartDate());
        holder.endDateTv.setText("End: " + activity.getEndDate());


        //  set a register button only in JOIN mode
        if (mode == AdapterMode.JOIN) {
            holder.joinBtn.setVisibility(View.VISIBLE);
            holder.joinBtn.setOnClickListener(v -> {
                if (joinClickListener != null) {
                    joinClickListener.onJoinClicked(activity);
                }
            });
        } else {
            holder.joinBtn.setVisibility(View.GONE);
        }

        // set delete button only in edit mode
        if (mode == AdapterMode.EDIT){
            holder.deleteBtn.setVisibility(View.VISIBLE);
            holder.deleteBtn.setOnClickListener(v ->{
                if(deleteClickListener != null){
                    deleteClickListener.onDeleteClicked(activity);
                }
            });
        } else {
            holder.deleteBtn.setVisibility(View.GONE);
        }

        //set card as clickable only in Rate mode
        if (mode == AdapterMode.GUIDE) {
            holder.itemView.setOnClickListener(v -> {
                if (guideClickListener != null) {
                    guideClickListener.onGuideClicked(activity);
                }
            });
        } else {
            holder.itemView.setOnClickListener(null);
        }


    if (mode == AdapterMode.PARENT_COMMENT) {
        holder.itemView.setOnClickListener(v -> {
            if (parentClickListener != null) {
                parentClickListener.onParentCommentClicked(activity);
            }
        });
    }
}

    public void updateActivityList(List<Activity> newList) {
        this.activityList = newList;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return activityList.size();
    }

    //this is an interface for the join button to use fro the listener in the join activity fragment.
    public interface OnJoinClickListener {
        void onJoinClicked(Activity activity);
    }

    private OnJoinClickListener joinClickListener;

    public void setOnJoinClickListener(OnJoinClickListener listener) {
        this.joinClickListener = listener;
    }

    //this is an interface for the delete button to use for the listener in the my monthly activities fragment.
    public interface OnDeleteClickListener {
        void onDeleteClicked(Activity activity);
    }

    private OnDeleteClickListener deleteClickListener;

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }


    // a card listener to use in GUIDE mode to make the card clickable
    public interface OnGuideClickListener {
        void onGuideClicked(Activity activity);
    }

    private OnGuideClickListener guideClickListener;

    public void setOnRateClickListener(OnGuideClickListener listener) {
        this.guideClickListener = listener;
    }

    // for parent comment click
    public interface OnParentCommentClickListener {
        void onParentCommentClicked(Activity activity);
    }

    private OnParentCommentClickListener parentClickListener;

    public void setOnParentCommentClickListener(OnParentCommentClickListener listener) {
        this.parentClickListener = listener;
    }






    public static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView actName, domainAndSubDomain, days, description, maxParticipants, guideName, startDateTv, endDateTv,ageTv;
        Button joinBtn, deleteBtn;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            actName = itemView.findViewById(R.id.actNameTv);
            domainAndSubDomain= itemView.findViewById(R.id.domainTv);
            ageTv = itemView.findViewById(R.id.ageTv);
            days = itemView.findViewById(R.id.daysTv);
            description = itemView.findViewById(R.id.descriptionTv);
            maxParticipants = itemView.findViewById(R.id.maxParticipantsTv);
            guideName = itemView.findViewById(R.id.guideNameTv);
            startDateTv = itemView.findViewById(R.id.startDateTv);
            endDateTv = itemView.findViewById(R.id.endDateTv);
            joinBtn = itemView.findViewById(R.id.joinBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }
}
