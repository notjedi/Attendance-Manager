package com.attendancemanager;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.List;
import java.util.Locale;

import github.com.st235.lib_expandablebottombar.ExpandableBottomBar;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {

    private static final String TAG = "SettingsFragment";
    public static final String NAME = "key_name";
    public static final String VIBRATE = "key_vibration";
    public static final String ATTENDANCE_CRITERIA_SELECTOR = "key_attendance_criterion";
    public static final String EDIT_SUBJECTS = "key_edit_subjects";
    public static final String NOTIFICATION = "key_notification";
    public static final String NOTIFICATION_TIME = "key_notification_time";
    public static final String RESET_DATABASE = "key_reset_attendance";
    public static final String CLEAR_DATABASE = "key_clear_database";
    public static final String RATE_APP = "key_rate_app";
    public static final String SHARE_APP = "key_share_app";
    public static final String ABOUT = "key_about";

    private RecyclerView recyclerView;
    private DBHelper dbHelper;
    private Preference attendanceCriteriaSelector;
    private Preference editSubject;
    private Preference notificationTimePicker;
    private Preference resetDatabase;
    private Preference clearDatabase;
    private Preference rateApp;
    private Preference shareApp;
    private Preference about;

    /* TODO: change font for the settings page */

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

        dbHelper = new DBHelper(getContext());

        attendanceCriteriaSelector = findPreference(ATTENDANCE_CRITERIA_SELECTOR);
        editSubject = findPreference(getString(R.string.key_edit_subjects));
        notificationTimePicker = findPreference(NOTIFICATION_TIME);
        resetDatabase = findPreference(RESET_DATABASE);
        clearDatabase = findPreference(CLEAR_DATABASE);
        rateApp = findPreference(RATE_APP);
        shareApp = findPreference(SHARE_APP);
        about = findPreference(ABOUT);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        attendanceCriteriaSelector.setOnPreferenceClickListener(this);
        editSubject.setOnPreferenceClickListener(this);
        notificationTimePicker.setOnPreferenceClickListener(this);
        resetDatabase.setOnPreferenceClickListener(this);
        clearDatabase.setOnPreferenceClickListener(this);
        rateApp.setOnPreferenceClickListener(this);
        shareApp.setOnPreferenceClickListener(this);
        about.setOnPreferenceClickListener(this);

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
                Intent editSubjectIntent = new Intent(getContext(), EditSubjectActivity.class);
                startActivity(editSubjectIntent);
                break;

            case NOTIFICATION_TIME:
                buildTimePicker();
                break;

            case RESET_DATABASE:
                /* TODO: Prompt user for confirmation */
                buildAlertDialog(RESET_DATABASE);
                break;

            case CLEAR_DATABASE:
                /* TODO: Prompt user for confirmation */
                buildAlertDialog(CLEAR_DATABASE);
                break;

            case RATE_APP:
                openPlayStore();
                break;

            case SHARE_APP:
                shareApp();
                break;

            case ABOUT:
                Intent aboutIntent = new Intent(getContext(), AboutActivity.class);
                startActivity(aboutIntent);
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
                attendancePercentage.setText(String.format(Locale.US, "%d%%", sliderValue));
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                sliderValue = (int) slider.getValue();
                progressBar.setProgress(sliderValue);
                attendancePercentage.setText(String.format(Locale.US, "%d%%", sliderValue));
            }
        });

        slider.addOnChangeListener((slider1, value, fromUser) -> {
            progressBar.setProgress((int) value);
            attendancePercentage.setText(String.format(Locale.US, "%d%%", (int) value));
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

    private void buildAlertDialog(String key) {

        String title = null;
        String message = null;
        String common = "This action cannot be undone. Do you want to continue?";
        if (key.equals(RESET_DATABASE)) {
            title = "Reset Attendance?";
            message = "This will wipe your attendance data. " + common;
        } else if (key.equals(CLEAR_DATABASE)) {
            title = "Clear Database?";
            message = "This will wipe all your data. " + common;
        }

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialog_App_Theme)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.cancel();
                })
                .setPositiveButton("Confirm", (dialog, which) -> {
                    if (key.equals(RESET_DATABASE)) {
                        resetAttendance();
                    } else if (key.equals(CLEAR_DATABASE)) {
                        clearDatabase();
                    }
                    dialog.dismiss();
                });
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
        TextView messageText = dialog.findViewById(android.R.id.message);
        messageText.setTypeface(ResourcesCompat.getFont(getContext(), R.font.raleway));
    }

    private void resetAttendance() {

        List<Subject> subjectList = dbHelper.getAllSubjects();
        for (Subject subject : subjectList) {
            dbHelper.resetAttendance(subject);
        }
    }

    private void clearDatabase() {

        dbHelper.deleteAllData();
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

    private void openPlayStore() {

        String appPackageName = getString(R.string.app_package_name);
        try {
            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
            startActivity(playStoreIntent);
        } catch (ActivityNotFoundException unused) {
            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
            startActivity(playStoreIntent);
        }
    }

    private void bugReport() {

        String str2;
        try {
            String str3 = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName;
            StringBuilder sb = new StringBuilder();
            sb.append("\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: ");
            sb.append(Build.VERSION.RELEASE);
            sb.append("\n App Version: ");
            sb.append(str3);
            sb.append("\n Device Brand: ");
            sb.append(Build.BRAND);
            sb.append("\n Device Model: ");
            sb.append(Build.MODEL);
            sb.append("\n Device Manufacturer: ");
            sb.append(Build.MANUFACTURER);
            str2 = sb.toString();
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(getContext(), e.getMessage(), 0).show();
            str2 = null;
        }
        Intent intent = new Intent("android.intent.action.SEND");
        Intent putExtra = intent.setType("message/rfc822").putExtra("android.intent.extra.EMAIL", new String[]{"mail.kodalog@gmail.com"});
        StringBuilder sb2 = new StringBuilder();
        sb2.append(str);
        sb2.append(": ");
        sb2.append(getString(R.string.app_name));
        putExtra.putExtra("android.intent.extra.SUBJECT", sb2.toString()).putExtra("android.intent.extra.TEXT", str2);
        context.startActivity(Intent.createChooser(intent, context.getString(C0467R.string.choose_email_client)));
    }

    private void shareApp() {

        Intent shareAppIntent = new Intent(Intent.ACTION_SEND);
        shareAppIntent.setType("text/plain");
        StringBuilder shareText = new StringBuilder();
        shareText.append("Hey, check out this Attendance Managing App - ");
        shareText.append(getString(R.string.app_name));
        shareText.append("\n\n http://play.google.com/store/apps/details?id=com.devsebastian.attendancemanager");
        shareAppIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        shareAppIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());
        Intent.createChooser(shareAppIntent, "Share via");
        startActivity(shareAppIntent);
    }
}