package com.attendancemanager.view;

import android.os.Bundle;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.attendancemanager.R;
import com.attendancemanager.adapters.PredictAdapter;
import com.attendancemanager.model.Subject;
import com.attendancemanager.model.TimeTableSubject;
import com.attendancemanager.viewmodel.DayViewModel;
import com.attendancemanager.viewmodel.SubjectViewModel;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PredictActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private Chip chip1;
    private Chip chip2;
    private Chip chip3;
    private Chip chip4;
    private Chip chip5;
    private Chip chip6;
    private Chip chip7;
    private RecyclerView recyclerView;

    private PredictAdapter predictAdapter;
    private List<TimeTableSubject> mondayList;
    private List<TimeTableSubject> tuesdayList;
    private List<TimeTableSubject> wednesdayList;
    private List<TimeTableSubject> thursdayList;
    private List<TimeTableSubject> fridayList;
    private List<TimeTableSubject> saturdayList;
    private List<TimeTableSubject> sundayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict);

        chip1 = findViewById(R.id.chip_1);
        chip2 = findViewById(R.id.chip_2);
        chip3 = findViewById(R.id.chip_3);
        chip4 = findViewById(R.id.chip_4);
        chip5 = findViewById(R.id.chip_5);
        chip6 = findViewById(R.id.chip_6);
        chip7 = findViewById(R.id.chip_7);
        recyclerView = findViewById(R.id.predict_recycler_view);

        predictAdapter = new PredictAdapter(this);
        SubjectViewModel subjectViewModel = new ViewModelProvider(this).get(SubjectViewModel.class);
        DayViewModel dayViewModel = new ViewModelProvider(this).get(DayViewModel.class);

        subjectViewModel.getAllSubjects().observe(this, subjects -> predictAdapter.submitList(subjects));
        mondayList = dayViewModel.getSubjectsOfDay("monday");
        tuesdayList = dayViewModel.getSubjectsOfDay("tuesday");
        wednesdayList = dayViewModel.getSubjectsOfDay("wednesday");
        thursdayList = dayViewModel.getSubjectsOfDay("thursday");
        fridayList = dayViewModel.getSubjectsOfDay("friday");
        saturdayList = dayViewModel.getSubjectsOfDay("saturday");
        sundayList = dayViewModel.getSubjectsOfDay("sunday");

        setChipText();
        buildRecyclerView();
    }

    private void setChipText() {
        Calendar calendar = Calendar.getInstance();
        String[] days = TimeTableFragment.DAY_NAMES;
        calendar.setTime(new Date());

        int dayInt = calendar.get(Calendar.DAY_OF_WEEK);
        dayInt += 12;
        chip1.setText(days[dayInt % 7]);
        chip1.setTag(days[dayInt++ % 7]);
        chip1.setOnCheckedChangeListener(this);

        chip2.setText(days[dayInt % 7]);
        chip2.setTag(days[dayInt++ % 7]);
        chip2.setOnCheckedChangeListener(this);

        chip3.setText(days[dayInt % 7]);
        chip3.setTag(days[dayInt++ % 7]);
        chip3.setOnCheckedChangeListener(this);

        chip4.setText(days[dayInt % 7]);
        chip4.setTag(days[dayInt++ % 7]);
        chip4.setOnCheckedChangeListener(this);

        chip5.setText(days[dayInt % 7]);
        chip5.setTag(days[dayInt++ % 7]);
        chip5.setOnCheckedChangeListener(this);

        chip6.setText(days[dayInt % 7]);
        chip6.setTag(days[dayInt++ % 7]);
        chip6.setOnCheckedChangeListener(this);

        chip7.setText(days[dayInt % 7]);
        chip7.setTag(days[dayInt % 7]);
        chip7.setOnCheckedChangeListener(this);
    }

    private void buildRecyclerView() {

        recyclerView.setAdapter(predictAdapter);
        recyclerView.setHasFixedSize(true);
    }

    @SuppressWarnings("ConstantConditions")
    private void calculateAttendance(String day, boolean isChecked) {
        List<Integer> indexList = new ArrayList<>();
        List<TimeTableSubject> timeTableSubjectList = new ArrayList<>(getDaySubject(day));

        for (TimeTableSubject timeTableSubject : timeTableSubjectList) {
            indexList.addAll(indexOf(timeTableSubject));
            for (Integer index : indexList) {
                Subject subject = predictAdapter.getSubjectAt(index);
                if (isChecked) {
                    subject.incrementTotalClasses();
                } else {
                    subject.decrementTotalClasses();
                }
                predictAdapter.notifyItemChanged(index);
            }
            indexList.clear();
        }
    }

    private List<TimeTableSubject> getDaySubject(String dayName) {
        switch (dayName.toLowerCase()) {
            case "monday":
                return mondayList;
            case "tuesday":
                return tuesdayList;
            case "wednesday":
                return wednesdayList;
            case "thursday":
                return thursdayList;
            case "friday":
                return fridayList;
            case "saturday":
                return saturdayList;
            case "sunday":
                return sundayList;
        }
        return null;
    }

    private List<Integer> indexOf(TimeTableSubject timeTableSubject) {
        List<Integer> indexList = new ArrayList<>();
        for (int i = 0; i < predictAdapter.getItemCount(); i++) {
            if (predictAdapter.getSubjectAt(i).getSubjectName()
                    .equals(timeTableSubject.getSubjectName())) {
                indexList.add(i);
            }
        }
        return indexList;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        calculateAttendance(buttonView.getTag().toString(), isChecked);
    }

    @Override
    protected void onPause() {
        super.onPause();
        /* A weird bug causes the changes in the arraylist(passed to the adapter) to persist even
        after leaving the activity and i'm able to notifyItemChanged() without submitting the list
        to the adapter(maybe because they are pointing to the same location under the hood). This
        is my ugly fix to the problem, probably the ugliest. */
        chip1.setChecked(false);
        chip2.setChecked(false);
        chip3.setChecked(false);
        chip4.setChecked(false);
        chip5.setChecked(false);
        chip6.setChecked(false);
        chip7.setChecked(false);
    }
}