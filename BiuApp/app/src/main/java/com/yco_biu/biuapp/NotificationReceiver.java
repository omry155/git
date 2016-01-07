package com.biu.ap2.winder.ex4;



import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.GregorianCalendar;
/**
 * this class is to notifiy who ever me want
 * code taken from advance-programing-2 course practice lessens
 */
public class NotificationReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.noaa)
                        .setContentTitle("My notification")
                        .setContentText(intent.getExtras().getString("sender"));

        int mNotificationId = 001;

        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());

        if (intent.getExtras().getString("sender").equals("one"))
        {
            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Long timeToAlert = new GregorianCalendar().getTimeInMillis() + 5000;

            Intent i = new Intent(context, NotificationReceiver.class);
            i.putExtra("sender", "one");

            alarmMgr.set(AlarmManager.RTC_WAKEUP, timeToAlert, PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT));
        }
    }
}