package com.attendancemanager.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.attendancemanager.helper.NotificationHelper;
import com.attendancemanager.view.MainActivity;
import com.attendancemanager.view.SettingsFragment;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SettingsFragment.NOTIFICATION, true) &&
                (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) ||
                        "android.intent.action.QUICKBOOT_POWERON".equals(intent.getAction()))) {

            SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.SHARED_PREFS_SETTINGS_FILE_KEY, Context.MODE_PRIVATE);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent notificationIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, sharedPreferences.getInt(NotificationHelper.SHARED_PREFS_HOUR_KEY, 17));
            calendar.set(Calendar.MINUTE, sharedPreferences.getInt(NotificationHelper.SHARED_PREFS_MINUTES_KEY, 30));
            calendar.set(Calendar.SECOND, 0);
            if (calendar.before(Calendar.getInstance()))
                calendar.add(Calendar.DATE, 1);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }
}