package com.attendancemanager.view;

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
import com.attendancemanager.viewmodel.SubjectViewModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class EditSubjectActivity extends AppCompatActivity {

    private static final String TAG = "EditSubjectActivity";

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ExtendedFloatingActionButton extendedFab;

    private SubjectViewModel subjectViewModel;
    private EditSubjectActivityAdapter editSubjectAdapter;
    private List<Subject> subjectList;
    private Subject deletedSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_subject);

        subjectList = new ArrayList<>();
        subjectViewModel = new ViewModelProvider(this).get(SubjectViewModel.class);
        subjectViewModel.getAllSubjects().observe(this, subjects -> {
            editSubjectAdapter.submitList(subjects);
            subjectList = subjects;
        });

        initialSetup();
        setupToolbar();
        buildRecyclerView();
        setExtendedFabListener();

    }

    private void initialSetup() {
        /* Finding views */

        toolbar = findViewById(R.id.edit_subject_toolbar);
        recyclerView = findViewById(R.id.edit_subject_recycler_view);
        extendedFab = findViewById(R.id.extended_fab);

        deletedSubject = null;

    }

    private void setupToolbar() {
        /* Toolbar stuff */

        toolbar.setTitle("Subjects");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.light_black));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPinkPrimaryDark));
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void buildRecyclerView() {
        /* Initializing recycler view related stuff */

        editSubjectAdapter = new EditSubjectActivityAdapter(this);
        /* Overriding interface click listener */
        editSubjectAdapter.setItemClickListener(position -> buildDialog(
                editSubjectAdapter.getSubjectAt(position)));

        recyclerView.setAdapter(editSubjectAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                /* Shrink FAB on scroll */
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    /* Scrolled down */
                    extendedFab.shrink();
                } else {
                    /* Scrolled up */
                    extendedFab.extend();
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

                int position = viewHolder.getAdapterPosition();
                deletedSubject = editSubjectAdapter.getSubjectAt(position);
                subjectViewModel.delete(editSubjectAdapter.getSubjectAt(position));

                Snackbar snackbar = Snackbar.make(recyclerView, "Deleted " +
                        deletedSubject.getSubjectName(), Snackbar.LENGTH_LONG);
                snackbar.setAction("Undo", v -> subjectViewModel.insert(deletedSubject));
                snackbar.show();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void setExtendedFabListener() {
        /* Implement FAB click listener */
        extendedFab.setOnClickListener(v -> buildDialog(null));
    }

    @SuppressWarnings("ConstantConditions")
    private void buildDialog(Subject subject) {
        /* Build alert dialog based on parameters passed
        Add Subject dialog - if parameter not passed
        Edit Subject dialog - if parameter passed */

        String title;
        String positiveText;

        if (subject != null) {
            title = "Edit Subject";
            positiveText = "OK";
        } else {
            title = "Add Subject";
            positiveText = "Add";
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EditSubjectActivity.this,
                R.style.AlertDialog_App_Theme);
        dialogBuilder.setTitle(title);

        View subjectInputView = LayoutInflater.from(EditSubjectActivity.this).inflate(R.layout.add_subject_edit_text,
                (ViewGroup) findViewById(android.R.id.content).getRootView(), false);
        TextInputEditText subjectNameEditText = subjectInputView.findViewById(R.id.subject_name_input);
        TextInputEditText attendedClassesEditText = subjectInputView.findViewById(R.id.subject_attended_input);
        TextInputEditText totalClassesEditText = subjectInputView.findViewById(R.id.subject_total_input);

        if (subject != null) {
            subjectNameEditText.setText(subject.getSubjectName());
            attendedClassesEditText.setText(String.valueOf(subject.getAttendedClasses()));
            totalClassesEditText.setText(String.valueOf(subject.getTotalClasses()));
        }
        dialogBuilder.setView(subjectInputView);

        dialogBuilder.setPositiveButton(positiveText, (dialog, which) -> {

            String subjectName = subjectNameEditText.getText().toString().trim();
            int attendClass;
            int totalClass;

            try {
                attendClass = Integer.parseInt(attendedClassesEditText.getText().toString());
                totalClass = Integer.parseInt(totalClassesEditText.getText().toString());
            } catch (NumberFormatException e) {
                attendClass = totalClass = 0;
            }

            if (subject != null) {
                /* Updating subject based on it's unique id */
                Subject updatedSubject = new Subject(subjectName, attendClass, totalClass);
                updatedSubject.setId(subject.getId());
                updateSubject(updatedSubject);
            }
            else
                insertSubject(subjectName, attendClass, totalClass);

            dialog.dismiss();
        });

        dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setEnabled(false);

        /* Implementing TextChangedListeners for the 3 text fields */
        attendedClassesEditText.addTextChangedListener(new TextWatcher() {
            /* Set error based on certain conditions */

            int totalClass;
            int attendClass;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {
                    /* Catching NumberFormatException if the text field is empty */
                    totalClass = Integer.parseInt(totalClassesEditText.getText().toString());
                    attendClass = Integer.parseInt(s.toString());
                } catch (NumberFormatException e) {
                    totalClass = attendClass = -1;
                }

                if (totalClass < attendClass || totalClass == -1 || attendClass == -1) {
                    /* Throwing error if total classes less than attended classes */
                    attendedClassesEditText.setError("Total classes less than attended classes");
                    positiveButton.setEnabled(false);
                } else {
                    positiveButton.setEnabled(true);
                    attendedClassesEditText.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        totalClassesEditText.addTextChangedListener(new TextWatcher() {

            int totalClass;
            int attendClass;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {
                    /* Catching NumberFormatException if the text field is empty */
                    totalClass = Integer.parseInt(s.toString());
                    attendClass = Integer.parseInt(attendedClassesEditText.getText().toString());
                } catch (NumberFormatException e) {
                    totalClass = attendClass = -1;
                }

                if (totalClass < attendClass || totalClass == -1 || attendClass == -1) {
                    /* Throwing error if total classes less than attended classes */
                    attendedClassesEditText.setError("Total classes less than attended classes");
                    positiveButton.setEnabled(false);
                } else {
                    attendedClassesEditText.setError(null);
                    positiveButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        subjectNameEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (subjectList.contains(new Subject(s.toString().trim())) && subject == null) {
                    /* Throwing this error only if the user is adding a new subject */
                    positiveButton.setEnabled(false);
                    subjectNameEditText.setError("Subject already exists");
                } else if (s.toString().trim().isEmpty()) {
                    /* Throwing error if subject name is empty */
                    positiveButton.setEnabled(false);
                    subjectNameEditText.setError("Subject name cannot be empty");
                } else {
                    positiveButton.setEnabled(true);
                    subjectNameEditText.setError(null);
                }
            }
        });
        /* End of TextChangedListeners */
    }

    private void updateSubject(Subject subject) {
        /* Update existing subject data on the database */
        subjectViewModel.update(subject);
    }

    private void insertSubject(String newSubjectName, int attendClass, int totalClass) {
        /* Add new subject to the database */

        Subject subject = new Subject(newSubjectName, attendClass, totalClass);

        if (subjectViewModel.containsSubject(subject.getSubjectName())) {
            Snackbar snackbar = Snackbar.make(recyclerView, "Subject already exists", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return;
        } else if (newSubjectName.isEmpty()) {
            /* TextChange listeners are trickable so implementing safety checks */
            Snackbar.make(recyclerView, "Subject name cannot be empty", Snackbar.LENGTH_SHORT).show();
            return;
        } else if (attendClass > totalClass) {
            /* TextChange listeners are trickable so implementing safety checks */
            subject.setAttendedClasses(0);
            subject.setTotalClasses(0);
        }

        subjectViewModel.insert(subject);
    }
}