package com.attendancemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class EditSubjectActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ExtendedFloatingActionButton extendedFab;

    private DBHelper dbHelper;
    private List<Subject> subjectList;
    private Subject deletedSubject;
    private EditSubjectAdapter editSubjectAdapter;
    public static boolean changed;

    private static final String TAG = "EditSubjectActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_subject);

        initialSetup();
        buildRecyclerView();
        setExtendedFabListener();

    }

    private void initialSetup() {

        toolbar = findViewById(R.id.edit_subject_toolbar);
        recyclerView = findViewById(R.id.edit_subject_recycler_view);
        extendedFab = findViewById(R.id.extended_fab);

        dbHelper = new DBHelper(this);
        deletedSubject = null;
        changed = false;
        subjectList = dbHelper.getAllSubjects();

        toolbar.setTitle("Subjects");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.light_black));
        //        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

    }

    private void buildRecyclerView() {

        editSubjectAdapter = new EditSubjectAdapter(subjectList, this);
        recyclerView.setAdapter(editSubjectAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
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

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getAdapterPosition();
                final boolean[] flag = {false};
                deletedSubject = subjectList.get(position);

                subjectList.remove(position);
                editSubjectAdapter.notifyItemRemoved(position);
                Snackbar snackbar = Snackbar.make(recyclerView, "Deleted " + deletedSubject.getSubjectName(), Snackbar.LENGTH_LONG);
                snackbar.setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        flag[0] = true;
                        subjectList.add(position, deletedSubject);
                        editSubjectAdapter.notifyItemInserted(position);
                    }
                });
                snackbar.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        Log.i(TAG, "onDismissed: " + flag[0] + " " + position);
                        if (!flag[0]) {
                            changed = true;
                            dbHelper.deleteSubject(deletedSubject.getSubjectName());
                        }
                    }
                });
                snackbar.show();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void setExtendedFabListener() {

        extendedFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EditSubjectActivity.this, R.style.AlertDialog_App_Theme);
                dialogBuilder.setTitle("Add Subject");

                View subjectInputView = LayoutInflater.from(EditSubjectActivity.this).inflate(R.layout.add_subject_edit_text, (ViewGroup) findViewById(android.R.id.content).getRootView(), false);
                TextInputEditText subjectNameEditText = subjectInputView.findViewById(R.id.subject_name_input);
                TextInputEditText attendedClassesEditText = subjectInputView.findViewById(R.id.subject_attended_input);
                TextInputEditText totalClassesEditText = subjectInputView.findViewById(R.id.subject_total_input);
                dialogBuilder.setView(subjectInputView);

                dialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String newSubjectName = subjectNameEditText.getText().toString().trim();
                        int attendClass;
                        int totalClass;
                        try {
                            attendClass = Integer.parseInt(attendedClassesEditText.getText().toString());
                            totalClass = Integer.parseInt(totalClassesEditText.getText().toString());
                        }
                        catch (NumberFormatException e) {
                            attendClass = totalClass = 0;
                        }

                        Subject subject = new Subject(newSubjectName, attendClass, totalClass);
                        int position = subjectList.size();

                        if (subjectList.contains(subject)) {
                            Snackbar snackbar = Snackbar.make(recyclerView, "Subject already exists", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                            return;
                        }

                        dbHelper.addSubject(subject);
                        subjectList.add(subject);
                        editSubjectAdapter.notifyItemInserted(position);

                        dialog.dismiss();
                    }
                });

                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });

                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setEnabled(false);

                attendedClassesEditText.addTextChangedListener(new TextWatcher() {

                    String subjectName;
                    int totalClass;
                    int attendClass;

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        try {
                            totalClass = Integer.parseInt(totalClassesEditText.getText().toString());
                            attendClass = Integer.parseInt(s.toString());
                        } catch (NumberFormatException e) {
                            totalClass = attendClass = -1;
                        }

                        if (totalClass < attendClass || totalClass == -1 || attendClass == -1) {
                            attendedClassesEditText.setError("Total classes less than attended classes");
                            positiveButton.setEnabled(false);
                        }
                        else {
                            positiveButton.setEnabled(true);
                            attendedClassesEditText.setError(null);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        subjectName = subjectNameEditText.getText().toString().trim();

                        if (subjectName.isEmpty() || subjectList.contains(new Subject(subjectName))) {
                            positiveButton.setEnabled(false);
                        }
                        else {
                            positiveButton.setEnabled(true);
                            attendedClassesEditText.setError(null);
                        }

                    }
                });

                totalClassesEditText.addTextChangedListener(new TextWatcher() {

                    String subjectName;
                    int totalClass;
                    int attendClass;

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        try {
                            totalClass = Integer.parseInt(s.toString());
                            attendClass = Integer.parseInt(attendedClassesEditText.getText().toString());
                        } catch (NumberFormatException e) {
                            totalClass = attendClass = -1;
                        }

                        if (totalClass < attendClass || totalClass == -1 || attendClass == -1) {
                            attendedClassesEditText.setError("Total classes less than attended classes");
                            positiveButton.setEnabled(false);
                        }
                        else {
                            attendedClassesEditText.setError(null);
                            positiveButton.setEnabled(true);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        subjectName = subjectNameEditText.getText().toString().trim();

                        if (subjectName.isEmpty() || subjectList.contains(new Subject(subjectName))) {
                            positiveButton.setEnabled(false);
                        }
                        else {
                            positiveButton.setEnabled(true);
                            attendedClassesEditText.setError(null);
                        }
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
                        if (subjectList.contains(new Subject(s.toString().trim()))) {
                            positiveButton.setEnabled(false);
                            subjectNameEditText.setError("Subject already exists");
                        }
                        else if (s.toString().isEmpty()) {
                            positiveButton.setEnabled(false);
                            subjectNameEditText.setError("Subject name cannot be empty");
                        }
                        else {
                            positiveButton.setEnabled(true);
                            subjectNameEditText.setError(null);
                        }
                    }
                });
            }
        });
    }
}