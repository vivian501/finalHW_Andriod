package com.example.finalhw_322392986_322389784_213913312.logic_model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalhw_322392986_322389784_213913312.AdapterMode;
import com.example.finalhw_322392986_322389784_213913312.R;

import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {

    private List<Activity> activities;
    private AdapterMode mode;
    private OnJoinClickListener joinClickListener;
    private OnItemClickListener itemClickListener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public interface OnJoinClickListener {
        void onJoinClick(Activity activity);
    }

    public interface OnItemClickListener {
        void onItemClick(Activity activity, int position);
    }

    public ActivityAdapter(List<Activity> activities, AdapterSMode mode) {
        this.activities = activities;
        this.mode = mode;
    }

    public ActivityAdapter(List<Activity> activities, AdapterMode mode, OnItemClickListener listener) {
        this.activities = activities;
        this.mode = mode;
        this.itemClickListener = listener;
    }

    public void setOnJoinClickListener(OnJoinClickListener listener) {
        this.joinClickListener = listener;
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public Activity getSelectedActivity() {
        if (selectedPosition != RecyclerView.NO_POSITION && selectedPosition < activities.size()) {
            return activities.get(selectedPosition);
        }
        return null;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        Activity activity = activities.get(position);
        holder.titleText.setText(activity.getName());
        holder.domainText.setText(activity.getDomain());

        // Highlight selected item if in BROWSE/EDIT mode
        if (mode == AdapterMode.BROWSE || mode == AdapterMode.EDIT) {
            holder.itemView.setSelected(position == selectedPosition);
        }

        holder.itemView.setOnClickListener(v -> {
            switch (mode) {
                case JOIN:
                    if (joinClickListener != null)
                        joinClickListener.onJoinClick(activity);
                    break;
                case BROWSE:
                case EDIT:
                    if (itemClickListener != null) {
                        selectedPosition = position;
                        itemClickListener.onItemClick(activity, position);
                        notifyDataSetChanged();
                    }
                    break;
                case GUIDE:
                case PARENT:
                case PARENT_COMMENT:
                    // Extend behavior as needed for these modes
                    break;
            }
        });
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, domainText;
        ImageView rateIcon;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.activityTitleTextView);
            domainText = itemView.findViewById(R.id.activityDomainTextView);
            rateIcon = itemView.findViewById(R.id.rateIcon);
        }
    }
}
