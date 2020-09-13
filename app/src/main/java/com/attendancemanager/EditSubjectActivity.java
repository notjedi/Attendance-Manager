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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class EditSubjectActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ExtendedFloatingActionButton extendedFab;
    private CoordinatorLayout coordinatorLayout;

    private DBHelper dbHelper;
    private List<Subject> subjectList;
    private Subject deletedSubject;
    private EditSubjectAdapter editSubjectAdapter;

    private static final String TAG = "EditSubjectActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_subject);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        toolbar = findViewById(R.id.edit_subject_toolbar);
        recyclerView = findViewById(R.id.edit_subject_recycler_view);
        extendedFab = findViewById(R.id.extended_fab);

        dbHelper = new DBHelper(this);
        deletedSubject = null;
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

        buildRecyclerView();

        extendedFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EditSubjectActivity.this, R.style.AlertDialog_App_Theme);
                dialogBuilder.setTitle("Add Subject");
                View subjectInputView = LayoutInflater.from(EditSubjectActivity.this).inflate(R.layout.add_subject_edit_text, (ViewGroup) findViewById(android.R.id.content).getRootView(), false);
                EditText editText = subjectInputView.findViewById(R.id.subject_name_input);
                dialogBuilder.setView(subjectInputView);

                dialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newSubjectName = editText.getText().toString().trim();
                        Subject subject = new Subject(newSubjectName);
                        int position = subjectList.size();

                        if (subjectList.contains(subject)) {
                            extendedFab.animate().alpha(0).setDuration(50);
                            Snackbar snackbar = Snackbar.make(recyclerView, "Subject already exists", Snackbar.LENGTH_SHORT);
                            snackbar.addCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar transientBottomBar, int event) {
                                    super.onDismissed(transientBottomBar, event);
                                    extendedFab.animate().alpha(1).setDuration(300);
                                }
                            });
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
                dialogBuilder.show();
            }
        });
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
                deletedSubject = subjectList.get(position);

                subjectList.remove(position);
                editSubjectAdapter.notifyItemRemoved(position);
                extendedFab.animate().alpha(0).setDuration(50);
                Snackbar snackbar = Snackbar.make(recyclerView, "Deleted " + deletedSubject.getSubjectName(), Snackbar.LENGTH_LONG);
                snackbar.setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        subjectList.add(position, deletedSubject);
                        editSubjectAdapter.notifyItemInserted(position);
                    }
                });
                snackbar.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        dbHelper.deleteSubject(deletedSubject.getSubjectName());
                        extendedFab.animate().alpha(1).setDuration(300);
                    }
                });
                snackbar.show();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}