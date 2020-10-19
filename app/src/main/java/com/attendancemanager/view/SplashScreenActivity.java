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
            SplashScreenActivity.this.finish();
        }
    };

    public static void resetLaunched() {
        IS_ALREADY_LAUNCHED = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(newActivityRunnable, 500);
    }

    @Override
    protected void onPause() {
        super.onPause();
        /* Makes sure that newActivityRunnable is not posted when the user presses the back or home button */
        mHandler.removeCallbacks(newActivityRunnable);
    }

}