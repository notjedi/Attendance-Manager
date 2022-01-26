package com.attendancemanager.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.attendancemanager.R;
import com.attendancemanager.adapters.TimeTableFragmentAdapter;
import com.attendancemanager.model.Subject;
import com.attendancemanager.model.TimeTableSubject;
import com.attendancemanager.viewmodel.DayViewModel;
import com.attendancemanager.viewmodel.SubjectViewModel;

import java.util.ArrayList;
import java.util.List;

public class DayFragment extends Fragment {

    private static final String ARG_DAY_NAME = "argDayName";
    private TimeTableFragmentAdapter timeTableAdapter;
    private RecyclerView timeTableRecyclerView;
    private SubjectViewModel subjectViewModel;
    private DayViewModel dayViewModel;
    private String argDay;

    public static DayFragment newInstance(String day) {
        /* Create new instance with the ARG_DAY_NAME */
        DayFragment dayFragment = new DayFragment();
        final Bundle args = new Bundle();
        args.putString(ARG_DAY_NAME, day);
        dayFragment.setArguments(args);
        return dayFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        argDay = getArguments().getString(ARG_DAY_NAME);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_day, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        timeTableRecyclerView = view.findViewById(R.id.time_table_recycler_view);
        dayViewModel = new ViewModelProvider(this).get(DayViewModel.class);
        subjectViewModel = new ViewModelProvider(this).get(SubjectViewModel.class);

        timeTableAdapter = new TimeTableFragmentAdapter();
        timeTableRecyclerView.setAdapter(timeTableAdapter);
        timeTableRecyclerView.setHasFixedSize(true);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                TimeTableSubject timeTableSubject = dayViewModel.getSubjectsOfDay(argDay).get(position);
                dayViewModel.delete(timeTableSubject);
            }
        });

        TimeTableFragment.isEditable.observe(getViewLifecycleOwner(), isEditable -> {
            if (isEditable)
                itemTouchHelper.attachToRecyclerView(timeTableRecyclerView);
            else
                itemTouchHelper.attachToRecyclerView(null);
        });

        dayViewModel.getSubjectsOfDayWithoutTemp(argDay).observe(getViewLifecycleOwner(), timeTableSubjectList -> {
            List<Subject> subjectList = new ArrayList<>();
            for (TimeTableSubject timeTableSubject : timeTableSubjectList) {
                Subject subject = subjectViewModel.getSubject(timeTableSubject.getSubjectName());
                if (subject != null) {
                    subjectList.add(subject);
                }
            }
            timeTableAdapter.setSubjectList(subjectList);
        });
    }

    @Override
    public void onDestroyView() {
        // Avoid memory leak
        timeTableRecyclerView.setAdapter(null);
        timeTableRecyclerView = null;
        super.onDestroyView();
    }
}
