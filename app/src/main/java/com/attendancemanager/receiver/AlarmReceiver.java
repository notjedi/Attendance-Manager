package com.attendancemanager.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.attendancemanager.helper.NotificationHelper;

public class AlarmReceiver extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nBuilder = notificationHelper.getDailyNotification();
        notificationHelper.getManager().notify(1, nBuilder.build());
    }
}
