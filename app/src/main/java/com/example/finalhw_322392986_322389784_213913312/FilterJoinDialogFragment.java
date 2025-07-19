package com.example.finalhw_322392986_322389784_213913312;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import java.util.HashMap;
import java.util.Map;

public class FilterJoinDialogFragment extends DialogFragment {

        private Button cancelBtn, applyBtn;
        private TextView  domainTv;
        private RadioGroup domainRG;
        private RadioButton scienceRB, socialRB, creativityRB; //radio buttons for the days
        private FilterListener listener;



        public interface FilterListener {
            void onFilterApplied(String domain);
        }

        public void setFilterListener(FilterListener listener) {
            this.listener = listener;
        }
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.dialog_fragment_filter_join, container, false);

            domainRG = view.findViewById(R.id.domainRadioGroup);
            domainTv = view.findViewById(R.id.domainFilterTv);
            scienceRB = view.findViewById(R.id.radioScience);
            socialRB = view.findViewById(R.id.radioSocial);
            creativityRB = view.findViewById(R.id.radioCreativity);
            applyBtn = view.findViewById(R.id.applyBtn);
            cancelBtn = view.findViewById(R.id.cancleBtn);


            // when this button is clicked selected data for the filtering is collected
            applyBtn.setOnClickListener(v -> {
                // Get selected domain
                int selectedDomainId = domainRG.getCheckedRadioButtonId();
                String domain = null;
                if (selectedDomainId == R.id.radioScience) {
                    domain = "Science";
                } else if (selectedDomainId == R.id.radioSocial) {
                    domain = "Social";
                } else if (selectedDomainId == R.id.radioCreativity) {
                    domain = "Creativity";
                }

                // Call the listener
                if (listener != null) {
                    listener.onFilterApplied(domain);
                }

                // Dismiss the dialog
                dismiss();
            });

// close the fragment without applying filtering
            cancelBtn.setOnClickListener(v -> dismiss());

            return view;
        }

    }


