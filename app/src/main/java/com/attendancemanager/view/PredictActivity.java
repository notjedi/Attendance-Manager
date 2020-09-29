package com.attendancemanager.view;

import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.attendancemanager.R;
import com.attendancemanager.adapters.PredictAdapter;
import com.attendancemanager.model.Subject;
import com.attendancemanager.model.SubjectMinimal;
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
    private List<Subject> predictSubjectList;
    private List<SubjectMinimal> mondayList;
    private List<SubjectMinimal> tuesdayList;
    private List<SubjectMinimal> wednesdayList;
    private List<SubjectMinimal> thursdayList;
    private List<SubjectMinimal> fridayList;
    private List<SubjectMinimal> saturdayList;
    private List<SubjectMinimal> sundayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict);
        Log.i("TAG", "onCreate: ");

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
        predictSubjectList = subjectViewModel.getAllSubjects().getValue();

        dayViewModel.getMondayList().observe(this, subjectMinimals -> mondayList = subjectMinimals);
        dayViewModel.getTuesdayList().observe(this, subjectMinimals -> tuesdayList = subjectMinimals);
        dayViewModel.getWednesdayList().observe(this, subjectMinimals -> wednesdayList = subjectMinimals);
        dayViewModel.getThursdayList().observe(this, subjectMinimals -> thursdayList = subjectMinimals);
        dayViewModel.getFridayList().observe(this, subjectMinimals -> fridayList = subjectMinimals);
        dayViewModel.getSaturdayList().observe(this, subjectMinimals -> saturdayList = subjectMinimals);
        dayViewModel.getSundayList().observe(this, subjectMinimals -> sundayList = subjectMinimals);

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
        predictAdapter.submitList(predictSubjectList);
    }

    @SuppressWarnings("ConstantConditions")
    private void calculateAttendance(String day, boolean isChecked) {
        List<Integer> indexList = new ArrayList<>();
        List<SubjectMinimal> subjectMinimalList = new ArrayList<>();
        subjectMinimalList.addAll(getDaySubject(day));

        for (SubjectMinimal subjectMinimal : subjectMinimalList) {
            indexList.addAll(indexOf(subjectMinimal));
            for (Integer index : indexList) {
                Subject subject = predictSubjectList.get(index);
                if (isChecked) {
                    subject.incrementTotalClasses();
                } else {
                    subject.decrementTotalClasses();
                }
                predictSubjectList.set(index, subject);
                predictAdapter.notifyItemChanged(index);
            }
            indexList.clear();
        }
    }

    private List<SubjectMinimal> getDaySubject(String dayName) {
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

    private List<Integer> indexOf(SubjectMinimal subjectMinimal) {
        List<Integer> indexList = new ArrayList<>();
        for (int i = 0; i < predictSubjectList.size(); i++) {
            if (predictSubjectList.get(i).getSubjectName().equals(subjectMinimal.getSubjectName())) {
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
    protected void onResume() {
        super.onResume();
        Log.i("TAG", "onResume: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("TAG", "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("TAG", "onDestroy: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("TAG", "onStart: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("TAG", "onPause: ");
    }
}