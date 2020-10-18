package com.attendancemanager.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    public static boolean IS_ALREADY_LAUNCHED = false;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Runnable newActivityRunnable = () -> {
        if (!IS_ALREADY_LAUNCHED) {
            IS_ALREADY_LAUNCHED = true;
            Intent mainActivityIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(mainActivityIntent);
            this.finish();
        }
    };

    public static void resetLaunched() {
        IS_ALREADY_LAUNCHED = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler.postDelayed(newActivityRunnable, 1000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mHandler.removeCallbacks(newActivityRunnable);
    }

}