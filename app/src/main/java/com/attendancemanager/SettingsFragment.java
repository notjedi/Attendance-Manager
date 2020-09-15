package com.attendancemanager;

import androidx.appcompat.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Locale;

import github.com.st235.lib_expandablebottombar.ExpandableBottomBar;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {

    private static final String TAG = "SettingsFragment";
    private static final String ATTENDANCE_CRITERIA_SELECTOR = "key_attendance_criterion";
    private static final String EDIT_SUBJECTS = "key_edit_subjects";
    private static final String NOTIFICATION = "key_notification";
    private static final String NOTIFICATION_TIME = "key_notification_time";

    private RecyclerView recyclerView;
    private Preference attendanceCriteriaSelector;
    private Preference editSubject;
    private Preference notificationTimePicker;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        recyclerView = super.onCreateRecyclerView(inflater, parent, savedInstanceState);
        recyclerView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
        return recyclerView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        attendanceCriteriaSelector = findPreference(ATTENDANCE_CRITERIA_SELECTOR);
        editSubject = findPreference(getString(R.string.key_edit_subjects));
        notificationTimePicker = findPreference(NOTIFICATION_TIME);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        attendanceCriteriaSelector.setOnPreferenceClickListener(this);
        editSubject.setOnPreferenceClickListener(this);
        notificationTimePicker.setOnPreferenceClickListener(this);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            ExpandableBottomBar bottomBar = getActivity().findViewById(R.id.bottom_bar);

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    /* Scrolled down */
                    bottomBar.hide();
                } else {
                    /* Scrolled up */
                    bottomBar.show();
                }
            }
        });
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        switch (preference.getKey()) {

            case ATTENDANCE_CRITERIA_SELECTOR:
                buildAttendanceCriteriaSelector();
                break;

            case EDIT_SUBJECTS:
                Intent intent = new Intent(getContext(), EditSubjectActivity.class);
                startActivity(intent);
                break;

            case NOTIFICATION_TIME:
                buildTimePicker();
                break;

            default:
                throw new IllegalStateException("Unexpected preference key" + preference.getKey());
        }

        return true;
    }

    private void buildAttendanceCriteriaSelector() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.AlertDialog_App_Theme);
        dialogBuilder.setTitle("Set attendance criteria");
        View view = LayoutInflater.from(getContext()).inflate(R.layout.attendance_criteria_slider, (ViewGroup) getActivity().findViewById(android.R.id.content).getRootView(), false);

        Slider slider = view.findViewById(R.id.attendance_criteria_slider);
        TextView attendancePercentage = view.findViewById(R.id.attendance_criteria_percentage);
        ProgressBar progressBar = view.findViewById(R.id.attendance_criteria_progress_bar);
        dialogBuilder.setView(view);

        progressBar.setProgress(75);
        attendancePercentage.setText("75%");

        slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            int sliderValue;

            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                sliderValue = (int) slider.getValue();
                progressBar.setProgress(sliderValue);
                attendancePercentage.setText(String.format(Locale.US,"%d%%", sliderValue));
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                sliderValue = (int) slider.getValue();
                progressBar.setProgress(sliderValue);
                attendancePercentage.setText(String.format(Locale.US,"%d%%", sliderValue));
            }
        });

        slider.addOnChangeListener((slider1, value, fromUser) -> {
            progressBar.setProgress((int) value);
            attendancePercentage.setText(String.format(Locale.US,"%d%%", (int) value));
        });

        slider.setLabelFormatter(value -> Integer.toString((int) value));

        dialogBuilder.setPositiveButton("OK", (dialog, which) -> {

            int sliderValue = (int) slider.getValue();
            Log.i(TAG, "buildAttendanceCriteriaSelector: " + sliderValue);
            dialog.dismiss();
        });

        dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        negativeButton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
    }

    private void buildTimePicker() {

        MaterialTimePicker timePicker = new MaterialTimePicker();
        timePicker.show(getChildFragmentManager(), "time_picker");
        timePicker.setTimeFormat(TimeFormat.CLOCK_12H);

        timePicker.setListener(dialog -> {
            int hour = dialog.getHour();
            int minute = dialog.getMinute();
            Log.i(TAG, "buildTimePicker: " + hour + " " + minute);
        });
    }
}