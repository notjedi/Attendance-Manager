package com.attendancemanager.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    private final Runnable newActivityRunnable = () -> {
        Intent mainActivity = new Intent(SplashScreen.this, MainActivity.class);
        startActivity(mainActivity);
    };
    private final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler.postDelayed(newActivityRunnable, 1000);
    }
}