package com.attendancemanager.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.attendancemanager.R;
import com.attendancemanager.view.MainActivity;

import java.util.Calendar;

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

        getManager().createNotificationChannel(dailyNotificationChannel);
    }

    public NotificationManager getManager() {
        if (notificationManager == null)
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return notificationManager;
    }

    public NotificationCompat.Builder getDailyNotification(Context context) {
        Intent activityIntent = new Intent(context, MainActivity.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent activityPendingIntent = PendingIntent.getActivity(context, 1, activityIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        Uri ringToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        return new NotificationCompat.Builder(getApplicationContext(), DAILY_REMAINDER_CHANNEL_ID)
                .setContentTitle("Attendance Manager")
                .setContentText("Don't forget to mark your attendance")
                .setSmallIcon(R.drawable.ic_check)
                .setContentIntent(activityPendingIntent)
                .setAutoCancel(true)
                .setSound(ringToneUri);
    }
}
