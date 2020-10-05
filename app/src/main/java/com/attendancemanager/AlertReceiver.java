package com.attendancemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nBuilder = notificationHelper.getDailyNotification();
        Log.i("TAG", "onReceive: ");
        notificationHelper.getManager().notify(1, nBuilder.build());
    }
}
