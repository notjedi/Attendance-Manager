package com.attendancemanager.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.attendancemanager.R;
import com.google.android.material.chip.Chip;

import java.util.Calendar;
import java.util.Date;

public class PredictActivity extends AppCompatActivity {

    private Chip chip1;
    private Chip chip2;
    private Chip chip3;
    private Chip chip4;
    private Chip chip5;
    private Chip chip6;
    private Chip chip7;


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

        setChipText();
    }

    private void setChipText() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int dayInt = calendar.get(Calendar.DAY_OF_WEEK);
        dayInt += 12;
        chip1.setText(TimeTableFragment.DAY_NAMES[dayInt++ % 7]);
        chip2.setText(TimeTableFragment.DAY_NAMES[dayInt++ % 7]);
        chip3.setText(TimeTableFragment.DAY_NAMES[dayInt++ % 7]);
        chip4.setText(TimeTableFragment.DAY_NAMES[dayInt++ % 7]);
        chip5.setText(TimeTableFragment.DAY_NAMES[dayInt++ % 7]);
        chip6.setText(TimeTableFragment.DAY_NAMES[dayInt++ % 7]);
        chip7.setText(TimeTableFragment.DAY_NAMES[dayInt % 7]);

    }
}