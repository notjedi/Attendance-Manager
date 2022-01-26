package com.attendancemanager.view;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.attendancemanager.R;
import com.attendancemanager.adapters.EditSubjectActivityAdapter;
import com.attendancemanager.model.Subject;
import com.attendancemanager.model.TimeTableSubject;
import com.attendancemanager.viewmodel.DayViewModel;
import com.attendancemanager.viewmodel.SubjectViewModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class EditSubjectActivity extends AppCompatActivity {

    public static int CHANGED = 0;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private ExtendedFloatingActionButton mExtendedFab;

    private SubjectViewModel subjectViewModel;
    private EditSubjectActivityAdapter editSubjectAdapter;
    private Subject deletedSubject;

    public static void resetChanged() {
        CHANGED = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_subject);

        subjectViewModel = new ViewModelProvider(this).get(SubjectViewModel.class);
        subjectViewModel.getAllSubjects().observe(this, subjects -> editSubjectAdapter.submitList(subjects));

        initialSetup();
        setupToolbar();
        buildRecyclerView();
        setExtendedFabListener();

    }

    private void initialSetup() {
        /* Finding views */

        mToolbar = findViewById(R.id.edit_subject_toolbar);
        mRecyclerView = findViewById(R.id.edit_subject_recycler_view);
        mExtendedFab = findViewById(R.id.extended_fab);

        deletedSubject = null;

    }

    private void setupToolbar() {
        /* Toolbar stuff */

        mToolbar.setTitle("Subjects");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.lightBlack));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPinkPrimaryDark));
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void buildRecyclerView() {
        /* Initializing recycler view related stuff */

        editSubjectAdapter = new EditSubjectActivityAdapter(this);
        SharedPreferences sharedPrefs = getSharedPreferences(MainActivity.SHARED_PREFS_SETTINGS_FILE_KEY, MODE_PRIVATE);
        editSubjectAdapter.setCriteria(sharedPrefs.getInt(MainActivity.SHARED_PREFS_ATTENDANCE_CRITERIA, 75));
        /* Overriding interface click listener */
        editSubjectAdapter.setItemClickListener(position -> buildUpdateSubjectDialog(
                editSubjectAdapter.getSubjectAt(position)));

        mRecyclerView.setAdapter(editSubjectAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                /* Shrink FAB on scroll */
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    /* Scrolled down */
                    mExtendedFab.shrink();
                } else {
                    /* Scrolled up */
                    mExtendedFab.extend();
                }
            }
        });

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper
                .SimpleCallback(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.LEFT) {
            /* ItemTouchHelper for swiping support in recycler view */

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getBindingAdapterPosition();
                deletedSubject = editSubjectAdapter.getSubjectAt(position);
                subjectViewModel.delete(editSubjectAdapter.getSubjectAt(position));

                Snackbar snackbar = Snackbar.make(mRecyclerView, "Deleted " +
                        deletedSubject.getSubjectName(), Snackbar.LENGTH_LONG);
                snackbar.setAction("Undo", v -> subjectViewModel.insert(deletedSubject));
                snackbar.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        /* Deleting subject from all time_table too, if the subject is
                        not present in subject_table, i.e if "Undo" is not pressed */
                        if (subjectViewModel.containsSubject(deletedSubject.getSubjectName()))
                            return;
                        DayViewModel dayViewModel = new ViewModelProvider(EditSubjectActivity.this).get(DayViewModel.class);
                        dayViewModel.deleteSubjectByName(deletedSubject.getSubjectName());
                    }
                });
                snackbar.show();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void setExtendedFabListener() {
        /* Build new dialog for the user to add a subject */
        mExtendedFab.setOnClickListener(v -> buildNewSubjectDialog());
    }

    private AlertDialog.Builder getDialogBuilder(String title, String positiveText, DialogInterface.OnClickListener listener) {

        return new AlertDialog.Builder(this, R.style.AlertDialog_App_Theme)
                .setTitle(title)
                .setPositiveButton(positiveText, listener)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
    }

    private void buildNewSubjectDialog() {

        View subjectInputView = LayoutInflater.from(EditSubjectActivity.this).inflate(R.layout.add_subject_edit_text,
                (ViewGroup) findViewById(android.R.id.content).getRootView(), false);
        TextInputEditText subjectNameEditText = subjectInputView.findViewById(R.id.subject_name_input);
        TextInputEditText attendedClassesEditText = subjectInputView.findViewById(R.id.subject_attended_input);
        TextInputEditText totalClassesEditText = subjectInputView.findViewById(R.id.subject_total_input);

        DialogInterface.OnClickListener listener = (dialog, which) -> {
            String subjectName = subjectNameEditText.getText().toString().trim();
            int[] result = getClasses(attendedClassesEditText, totalClassesEditText);
            int attendClass = result[0];
            int totalClass = result[1];

            insertSubject(subjectName, attendClass, totalClass);
            CHANGED = 1;
            dialog.dismiss();
        };
        AlertDialog.Builder dialogBuilder = getDialogBuilder("Add Subject", "OK", listener);
        dialogBuilder.setView(subjectInputView);

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setEnabled(false);

        setTextChangeListener(attendedClassesEditText, totalClassesEditText, positiveButton);
        setSubjectTextChangeListener(subjectNameEditText, positiveButton, 1);
    }

    private void buildUpdateSubjectDialog(Subject subject) {

        View subjectInputView = LayoutInflater.from(EditSubjectActivity.this).inflate(R.layout.add_subject_edit_text,
                (ViewGroup) findViewById(android.R.id.content).getRootView(), false);
        TextInputEditText subjectNameEditText = subjectInputView.findViewById(R.id.subject_name_input);
        TextInputEditText attendedClassesEditText = subjectInputView.findViewById(R.id.subject_attended_input);
        TextInputEditText totalClassesEditText = subjectInputView.findViewById(R.id.subject_total_input);

        subjectNameEditText.setText(subject.getSubjectName());
        attendedClassesEditText.setText(String.valueOf(subject.getAttendedClasses()));
        totalClassesEditText.setText(String.valueOf(subject.getTotalClasses()));

        DialogInterface.OnClickListener listener = (dialog, which) -> {
            String subjectName = subjectNameEditText.getText().toString().trim();
            int[] result = getClasses(attendedClassesEditText, totalClassesEditText);
            int attendClass = result[0];
            int totalClass = result[1];

            Subject updatedSubject = new Subject(subjectName, attendClass, totalClass);
            updatedSubject.setId(subject.getId());
            subjectViewModel.update(updatedSubject);
            CHANGED = 1;
            dialog.dismiss();
        };
        AlertDialog.Builder dialogBuilder = getDialogBuilder("Edit Subject", "Update", listener);
        dialogBuilder.setView(subjectInputView);

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setEnabled(false);

        setTextChangeListener(attendedClassesEditText, totalClassesEditText, positiveButton);
        setSubjectTextChangeListener(subjectNameEditText, positiveButton, 0);
    }

    private void setSubjectTextChangeListener(TextInputEditText subjectEditText, Button positiveButton,
                                              int flag) {

        subjectEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /* Required */
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /* Required */
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkSubjectName(subjectEditText, positiveButton, flag);
            }
        });
    }

    private void setTextChangeListener(TextInputEditText attendedEditText,
                                       TextInputEditText totalEditText, Button positiveButton) {

        attendedEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /* Required */
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkNumberOfClasses(attendedEditText, totalEditText, positiveButton);
            }

            @Override
            public void afterTextChanged(Editable s) {
                /* Required */
            }
        });

        totalEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /* Required */
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkNumberOfClasses(attendedEditText, totalEditText, positiveButton);
            }

            @Override
            public void afterTextChanged(Editable s) {
                /* Required */
            }
        });
    }

    private int[] getClasses(TextInputEditText attendedEditText, TextInputEditText totalEditText) {

        try {
            int attendClass, totalClass;
            attendClass = Integer.parseInt(attendedEditText.getText().toString());
            totalClass = Integer.parseInt(totalEditText.getText().toString());
            return new int[]{attendClass, totalClass};
        } catch (NumberFormatException e) {
            return new int[]{0, 0};
        }
    }

    private void checkSubjectName(TextInputEditText subjectEditText, Button positiveButton, int flag) {
        String subjectName = subjectEditText.getText().toString().trim();
        if (editSubjectAdapter.getCurrentList().contains(new Subject(subjectName)) && flag == 1) {
            /* Throwing this error only if it is a new subject dialog */
            positiveButton.setEnabled(false);
            subjectEditText.setError("Subject already exists");
        } else if (subjectName.isEmpty()) {
            /* Throwing error if subject name is empty */
            positiveButton.setEnabled(false);
            subjectEditText.setError("Subject name cannot be empty");
        } else {
            positiveButton.setEnabled(true);
            subjectEditText.setError(null);
        }
    }

    private void checkNumberOfClasses(TextInputEditText attendedEditText,
                                      TextInputEditText totalEditText, Button positiveButton) {

        try {
            /* Catching NumberFormatException if the text field is empty */
            int attendedClass, totalClass;
            attendedClass = Integer.parseInt(attendedEditText.getText().toString());
            totalClass = Integer.parseInt(totalEditText.getText().toString());

            if (totalClass < attendedClass) {
                /* Setting error message if total classes less than attended classes */
                attendedEditText.setError("Total classes less than attended classes");
                positiveButton.setEnabled(false);
            } else {
                attendedEditText.setError(null);
                positiveButton.setEnabled(true);
            }
        } catch (NumberFormatException e) {
            attendedEditText.setError("Total classes less than attended classes");
            positiveButton.setEnabled(false);
        }
    }

    private void insertSubject(String newSubjectName, int attendClass, int totalClass) {
        /* Add new subject to the database */

        Subject subject = new Subject(newSubjectName, attendClass, totalClass);
        subject.setStatus(TimeTableSubject.NONE);

        if (subjectViewModel.containsSubject(subject.getSubjectName())) {
            Snackbar snackbar = Snackbar.make(mRecyclerView, "Subject already exists", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return;
        } else if (newSubjectName.isEmpty()) {
            /* TextChange listeners are trickable so implementing safety checks */
            Snackbar.make(mRecyclerView, "Subject name cannot be empty", Snackbar.LENGTH_SHORT).show();
            return;
        } else if (attendClass > totalClass) {
            /* TextChange listeners are trickable so implementing safety checks */
            subject.setAttendedClasses(0);
            subject.setTotalClasses(0);
        }

        subjectViewModel.insert(subject);
    }
}