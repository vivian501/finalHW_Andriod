package com.example.finalhw_322392986_322389784_213913312.logic_model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalhw_322392986_322389784_213913312.R;
import com.example.finalhw_322392986_322389784_213913312.AdapterMode;

import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {

    private List<Activity> activityList;
    private AdapterMode mode;
    private OnItemClickListener onItemClickListener;
    private OnRateClickListener onRateClickListener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public interface OnItemClickListener {
        void onItemClick(Activity activity, int position);
    }

    public interface OnRateClickListener {
        void onRateClick(Activity activity);
    }

    public ActivityAdapter(List<Activity> activityList, AdapterMode mode) {
        this.activityList = activityList;
        this.mode = mode;
    }

    public ActivityAdapter(List<Activity> activityList, OnItemClickListener listener) {
        this.activityList = activityList;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        Activity activity = activityList.get(position);
        holder.activityTitleTextView.setText(activity.getName());
        holder.activityDomainTextView.setText(activity.getDomain());

        holder.itemView.setBackgroundColor(
                position == selectedPosition
                        ? ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_blue_light)
                        : ContextCompat.getColor(holder.itemView.getContext(), android.R.color.transparent)
        );

        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(v -> {
                onItemClickListener.onItemClick(activity, position);
            });
        }

        if (mode == AdapterMode.GUIDE && onRateClickListener != null) {
            holder.itemView.setOnClickListener(v -> {
                onRateClickListener.onRateClick(activity);
            });
        }
    }

    @Override
    public int getItemCount() {
        return activityList != null ? activityList.size() : 0;
    }

    public void setOnRateClickListener(OnRateClickListener listener) {
        this.onRateClickListener = listener;
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
        notifyDataSetChanged();
    }

    public static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView activityTitleTextView;
        TextView activityDomainTextView;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            activityTitleTextView = itemView.findViewById(R.id.activityTitleTextView);
            activityDomainTextView = itemView.findViewById(R.id.activityDomainTextView);
        }
    }
}