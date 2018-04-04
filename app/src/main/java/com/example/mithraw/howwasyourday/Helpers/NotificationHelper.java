package com.example.mithraw.howwasyourday.Helpers;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.mithraw.howwasyourday.Activities.MainActivity;
import com.example.mithraw.howwasyourday.Activities.RateADay;
import com.example.mithraw.howwasyourday.App;
import com.example.mithraw.howwasyourday.R;
import com.example.mithraw.howwasyourday.Tools.Hour;
import com.example.mithraw.howwasyourday.Tools.TimeAlarm;
import com.example.mithraw.howwasyourday.databases.Day;
import com.example.mithraw.howwasyourday.databases.DaysDatabase;

import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


public class NotificationHelper {
    private static String CHANNEL_ID = "rate_your_day_channel";
    private static AlarmManager alarmMgr;
    private static PendingIntent alarmIntent;

    public static void buildChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Resources res = MainActivity.getContext().getResources();
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name = res.getString(R.string.channel_name);
            String description = res.getString(R.string.channel_description);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManager notificationManager = (NotificationManager) MainActivity.getContext().getSystemService(MainActivity.getContext().NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void setupNotificationStatus() {
        updateNotificationStatus(
                PreferenceManager.getDefaultSharedPreferences(App.getApplication().getApplicationContext()).getBoolean("use_notifications", true),
                PreferenceManager.getDefaultSharedPreferences(App.getApplication().getApplicationContext()).getString("notify_time", "21:30"),
                PreferenceManager.getDefaultSharedPreferences(App.getApplication().getApplicationContext()).getString("ringtone", "content://settings/system/notification_sound"),
                PreferenceManager.getDefaultSharedPreferences(App.getApplication().getApplicationContext()).getBoolean("notifications_new_message_vibrate", true)
        );
    }

    public static void updateNotificationStatus(boolean enable, String time, String ringtone, boolean vibration) {
        Logger.getLogger("NotificationHelper").log(new LogRecord(Level.WARNING, "FMORALDO : Notification updated" + " " + enable + " " + time + " " + ringtone + " " + vibration));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        Hour curHour = new Hour(time);
        calendar.set(Calendar.HOUR_OF_DAY, curHour.getIntHour());
        calendar.set(Calendar.MINUTE, curHour.getIntMinute());
        Intent intent = new Intent(App.getApplication().getApplicationContext(), TimeAlarm.class);
        intent.setAction("com.example.mithraw.howwasyourday.alarm");
        alarmMgr = (AlarmManager) App.getApplication().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmIntent = PendingIntent.getBroadcast(App.getApplication().getApplicationContext(), 0, intent, 0);
        alarmMgr.cancel(alarmIntent);
        if(enable) {
            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
        }
    }

    public static void triggerNotification() {
        new Thread() {
            @Override
            public void run() {
                DaysDatabase db = DaysDatabase.getInstance(App.getApplication().getApplicationContext());
                Calendar calendar = Calendar.getInstance();
                //if the day has already been filled with a rating we don't show the notification
                List<Day> days = db.dayDao().loadAllByDate(calendar.get(java.util.Calendar.DAY_OF_MONTH), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.YEAR));
                // TODO remove the comments here
                if (!days.isEmpty())
                    return;
                Resources res = App.getApplication().getApplicationContext().getResources();

                Intent intent = new Intent(App.getApplication().getApplicationContext(), RateADay.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(MainActivity.EXTRA_DATE_DAY, calendar.get(java.util.Calendar.DAY_OF_MONTH));
                intent.putExtra(MainActivity.EXTRA_DATE_MONTH, calendar.get(java.util.Calendar.MONTH));
                intent.putExtra(MainActivity.EXTRA_DATE_YEAR, calendar.get(java.util.Calendar.YEAR));
                PendingIntent pendingIntent = PendingIntent.getActivity(App.getApplication().getApplicationContext(), 0, intent, 0);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(App.getApplication().getApplicationContext(), CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(res.getString(R.string.channel_name))
                        .setContentText(res.getString(R.string.channel_name))
                        .setContentIntent(pendingIntent)
                        .setSound(Uri.parse(PreferenceManager.getDefaultSharedPreferences(App.getApplication().getApplicationContext()).getString("ringtone", "content://settings/system/notification_sound")))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                if (PreferenceManager.getDefaultSharedPreferences(App.getApplication().getApplicationContext()).getBoolean("notifications_new_message_vibrate", true)) {
                    long[] pattern = new long []{500,110,500,110,450,110,200,110,170,40,450,110,200,110,170,40,500};
                    mBuilder.setVibrate(pattern);
                }
                NotificationManager notificationManager = (NotificationManager) App.getApplication().getApplicationContext().getSystemService(App.getApplication().getApplicationContext().NOTIFICATION_SERVICE);
                notificationManager.notify(0, mBuilder.build());
            }
        }.start();

    }
}
