package com.example.finalhw_322392986_322389784_213913312;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ReportPagerAdapter extends FragmentStateAdapter {
    public ReportPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new GeneralReportFragment();
        } else {
            return new MonthlyReportFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Two tabs, one for the monthly report and one for the general report.
    }
}
