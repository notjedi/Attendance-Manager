package com.attendancemanager.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.attendancemanager.R;

public class NotificationHelper extends ContextWrapper {

    public static final String DAILY_REMAINDER_CHANNEL_ID = "channel1";
    public static final String DAILY_REMAINDER_CHANNEL_NAME = "Daily remainder";
    public static final String SHARED_PREFS_HOUR_KEY = "notification_hour";
    public static final String SHARED_PREFS_MINUTES_KEY = "notification_minute";

    private NotificationManager notificationManager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createNotificationChannels();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannels() {
        NotificationChannel dailyNotificationChannel = new NotificationChannel(DAILY_REMAINDER_CHANNEL_ID, DAILY_REMAINDER_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        dailyNotificationChannel.enableVibration(true);
        dailyNotificationChannel.enableLights(true);
        dailyNotificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        Log.i("TAG", "createNotificationChannels: ");
        getManager().createNotificationChannel(dailyNotificationChannel);
    }

    public NotificationManager getManager() {
        if (notificationManager == null)
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Log.i("TAG", "getManager: ");
        return notificationManager;
    }

    public NotificationCompat.Builder getDailyNotification() {
        Log.i("TAG", "getDailyNotification: ");
        return new NotificationCompat.Builder(getApplicationContext(), DAILY_REMAINDER_CHANNEL_ID)
                .setContentTitle("Attendance Manager")
                .setContentText("Don't forget to mark your attendance")
                .setSmallIcon(R.drawable.ic_check);
    }
}
