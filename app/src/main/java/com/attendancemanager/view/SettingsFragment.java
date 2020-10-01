package com.attendancemanager.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.attendancemanager.R;
import com.attendancemanager.model.Subject;
import com.attendancemanager.viewmodel.DayViewModel;
import com.attendancemanager.viewmodel.SubjectViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.List;
import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {

    public static final String NAME = "key_name";
    public static final String VIBRATE = "key_vibration";
    public static final String ATTENDANCE_CRITERIA_SELECTOR = "key_attendance_criterion";
    public static final String EDIT_SUBJECTS = "key_edit_subjects";
    public static final String PREDICT = "key_predict";
    public static final String NOTIFICATION = "key_notification";
    public static final String NOTIFICATION_TIME = "key_notification_time";
    public static final String RESET_DATABASE = "key_reset_attendance";
    public static final String CLEAR_DATABASE = "key_clear_database";
    public static final String BACKUP_DATABASE = "key_backup";
    public static final String RESTORE_DATABASE = "key_restore";
    public static final String RATE_APP = "key_rate_app";
    public static final String SHARE_APP = "key_share_app";
    public static final String BUG_REPORT = "key_bug_report";
    public static final String ABOUT = "key_about";
    public static final String DEVELOPED_BY = "key_developed_by";
    public static final int WRITE_PERMISSION_REQUEST = 1;
    public static final int READ_PERMISSION_REQUEST = 2;
    public static final int FILE_CHOOSER_ACTIVITY_REQUEST = 1;
    private static final String TAG = "SettingsFragment";
    private SubjectViewModel subjectViewModel;
    private DayViewModel dayViewModel;

    /* TODO: change font for the settings page */

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        subjectViewModel = new ViewModelProvider(this).get(SubjectViewModel.class);
        dayViewModel = new ViewModelProvider(this).get(DayViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        RecyclerView recyclerView = super.onCreateRecyclerView(inflater, parent, savedInstanceState);
        recyclerView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
        return recyclerView;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Preference attendanceCriteriaSelector = findPreference(ATTENDANCE_CRITERIA_SELECTOR);
        Preference editSubject = findPreference(EDIT_SUBJECTS);
        Preference predict = findPreference(PREDICT);
        Preference notificationTimePicker = findPreference(NOTIFICATION_TIME);
        Preference resetDatabase = findPreference(RESET_DATABASE);
        Preference clearDatabase = findPreference(CLEAR_DATABASE);
        Preference backupDatabase = findPreference(BACKUP_DATABASE);
        Preference restoreDatabase = findPreference(RESTORE_DATABASE);
        Preference rateApp = findPreference(RATE_APP);
        Preference shareApp = findPreference(SHARE_APP);
        Preference bugReport = findPreference(BUG_REPORT);
        Preference about = findPreference(ABOUT);
        Preference developedBy = findPreference(DEVELOPED_BY);

        attendanceCriteriaSelector.setOnPreferenceClickListener(this);
        editSubject.setOnPreferenceClickListener(this);
        predict.setOnPreferenceClickListener(this);
        notificationTimePicker.setOnPreferenceClickListener(this);
        resetDatabase.setOnPreferenceClickListener(this);
        clearDatabase.setOnPreferenceClickListener(this);
        backupDatabase.setOnPreferenceClickListener(this);
        restoreDatabase.setOnPreferenceClickListener(this);
        rateApp.setOnPreferenceClickListener(this);
        shareApp.setOnPreferenceClickListener(this);
        bugReport.setOnPreferenceClickListener(this);
        about.setOnPreferenceClickListener(this);
        developedBy.setOnPreferenceClickListener(this);

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

            case PREDICT:
                Intent predictIntent = new Intent(getContext(), PredictActivity.class);
                startActivity(predictIntent);
                break;

            case NOTIFICATION_TIME:
                buildTimePicker();
                break;

            case RESET_DATABASE:
                buildAlertDialog(RESET_DATABASE);
                break;

            case CLEAR_DATABASE:
                buildAlertDialog(CLEAR_DATABASE);
                break;

            case BACKUP_DATABASE:
                if (checkPermissions("write"))
                    backupDatabase();
                else
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST);
                break;

            case RESTORE_DATABASE:
                if (checkPermissions("read")) {
                    Intent fileChooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    fileChooserIntent.setType("*/*");
                    startActivityForResult(fileChooserIntent, FILE_CHOOSER_ACTIVITY_REQUEST);
                } else
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_REQUEST);
                break;

            case RATE_APP:
                openPlayStore();
                break;

            case BUG_REPORT:
                bugReport();
                break;

            case SHARE_APP:
                shareApp();
                break;

            case ABOUT:
                Intent aboutIntent = new Intent(getContext(), AboutActivity.class);
                startActivity(aboutIntent);
                break;

            case DEVELOPED_BY:
                Intent githubIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.github_developer_url)));
                startActivity(githubIntent);
                break;

            default:
                throw new IllegalStateException("Unexpected preference key" + preference.getKey());
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case WRITE_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    backupDatabase();
                else
                    Toast.makeText(getContext(), "Backup failed", Toast.LENGTH_SHORT).show();
                break;
            case READ_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent fileChooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    fileChooserIntent.setType("*/*");
                    startActivityForResult(fileChooserIntent, FILE_CHOOSER_ACTIVITY_REQUEST);
                } else
                    Toast.makeText(getContext(), "Recovery failed", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_ACTIVITY_REQUEST) {
            if (resultCode == Activity.RESULT_OK)
                if (data != null)
                    restoreDatabase(data.getData());
                else
                    Toast.makeText(getContext(), "Recovery failed", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getContext(), "Recovery failed", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SetTextI18n")
    @SuppressWarnings("ConstantConditions")
    private void buildAttendanceCriteriaSelector() {
        /* TODO Set default value to current value */

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.AlertDialog_App_Theme);
        dialogBuilder.setTitle("Set attendance criteria");
        View view = LayoutInflater.from(getContext()).inflate(R.layout.attendance_criteria_slider,
                (ViewGroup) getActivity().findViewById(android.R.id.content).getRootView(), false);

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

    @SuppressWarnings("ConstantConditions")
    private void buildAlertDialog(String key) {
        /* Alert dialog for clearing database and resetting attendance based on @param key*/

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

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(getContext(),
                R.style.AlertDialog_App_Theme)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
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

        List<Subject> subjectList = subjectViewModel.getAllSubjects().getValue();
        if (subjectList != null) {
            for (Subject subject : subjectList) {
                subject.setAttendedClasses(0);
                subject.setTotalClasses(0);
                subjectViewModel.update(subject);
            }
        }
    }

    private void clearDatabase() {

        subjectViewModel.deleteAllSubjects();
        dayViewModel.deleteAllSubjects();
    }

    private boolean checkPermissions(String type) {
        /* https://developer.android.com/training/permissions/requesting */
        if (type.equals("write")) {
            return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        } else if (type.equals("read")) {
            return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    @SuppressWarnings("ConstantConditions")
    private void backupDatabase() {
        
    }

    private void restoreDatabase(Uri uri) {

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

        try {
            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.market_uri)));
            startActivity(playStoreIntent);
        } catch (ActivityNotFoundException unused) {
            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_store_url)));
            startActivity(playStoreIntent);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void bugReport() {

        String appVersion;
        String mailSubject = getString(R.string.app_name) + ": Bug Report";
        try {
            appVersion = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            appVersion = getString(R.string.app_version_number) + " (from version number)";
            Toast.makeText(getContext(), "Not able to get app version", Toast.LENGTH_SHORT).show();
        }
        StringBuilder mailInfoBuilder;
        mailInfoBuilder = new StringBuilder();

        mailInfoBuilder.append("\nDevice info: \nAndroid version: ");
        mailInfoBuilder.append(Build.VERSION.RELEASE);

        mailInfoBuilder.append("\nApp Version: ");
        mailInfoBuilder.append(appVersion);

        mailInfoBuilder.append("\nDevice Manufacturer: ");
        mailInfoBuilder.append(Build.MANUFACTURER);

        mailInfoBuilder.append("\nDevice Model: ");
        mailInfoBuilder.append(Build.MODEL);

        Intent mailIntent = new Intent(Intent.ACTION_SEND);
        mailIntent.setType("message/rfc822");
        mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"krithickumar26@gmail.com"});
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, mailSubject);
        mailIntent.putExtra(Intent.EXTRA_TEXT, mailInfoBuilder.toString());
        startActivity(Intent.createChooser(mailIntent, "Choose an email client :"));
    }

    private void shareApp() {

        Intent shareAppIntent = new Intent(Intent.ACTION_SEND);
        shareAppIntent.setType("text/plain");
        StringBuilder shareText;
        shareText = new StringBuilder();
        shareText.append("Hey, check out this Attendance Managing App - ");
        shareText.append(getString(R.string.app_name));
        shareText.append("\n\n").append(getString(R.string.play_store_url));
        shareAppIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        shareAppIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());
        Intent.createChooser(shareAppIntent, "Share via");
        startActivity(shareAppIntent);
    }
}