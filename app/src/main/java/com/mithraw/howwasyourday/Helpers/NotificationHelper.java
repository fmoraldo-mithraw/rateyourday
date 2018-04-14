package com.mithraw.howwasyourday.Helpers;

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
import android.widget.RemoteViews;

import com.mithraw.howwasyourday.Activities.MainActivity;
import com.mithraw.howwasyourday.Activities.RateADay;
import com.mithraw.howwasyourday.App;
import com.mithraw.howwasyourday.Services.NotificationIntentService;
import com.mithraw.howwasyourday.R;
import com.mithraw.howwasyourday.Tools.Hour;
import com.mithraw.howwasyourday.Tools.TimeAlarm;
import com.mithraw.howwasyourday.databases.Day;
import com.mithraw.howwasyourday.databases.DaysDatabase;

import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


public class NotificationHelper {
    private static String CHANNEL_ID = "rate_your_day_channel";
    private static AlarmManager alarmMgr;
    private static PendingIntent alarmIntent;
    public static final String ONE_STAR = "1";
    public static final String TWO_STAR = "2";
    public static final String THREE_STAR = "3";
    public static final String FOUR_STAR = "4";
    public static final String FIVE_STAR = "5";

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
                PreferenceManager.getDefaultSharedPreferences(App.getApplication().getApplicationContext()).getString("notifications_new_message_ringtone", "content://settings/system/notification_sound"),
                PreferenceManager.getDefaultSharedPreferences(App.getApplication().getApplicationContext()).getBoolean("notifications_new_message_vibrate", true)
        );
    }

    public static void updateNotificationStatus(boolean enable, String time, String ringtone, boolean vibration) {
        Logger.getLogger("NotificationHelper").log(new LogRecord(Level.INFO, "FMORALDO : Notification updated" + " " + enable + " " + time + " " + ringtone + " " + vibration));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        Hour curHour = new Hour(time);
        calendar.set(Calendar.HOUR_OF_DAY, curHour.getIntHour());
        calendar.set(Calendar.MINUTE, curHour.getIntMinute());
        calendar.set(Calendar.SECOND, 0);
        //if the date is in the past, we add a day
        if( calendar.getTimeInMillis() < System.currentTimeMillis()) {
            Logger.getLogger("NotificationIntentService").log(new LogRecord(Level.INFO, "FMORALDO : updateNotificationStatus : Time set in the past, we add a day"));
            calendar.setTimeInMillis(calendar.getTimeInMillis() + 86400000);
        }

        Resources res = App.getApplication().getResources();
        String notificationIntentAction = res.getString(R.string.notificationIntentAction);
        Intent intent = new Intent(App.getApplication().getApplicationContext(), TimeAlarm.class);
        intent.setAction(notificationIntentAction);
        alarmMgr = (AlarmManager) App.getApplication().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmIntent = PendingIntent.getBroadcast(App.getApplication().getApplicationContext(), 0, intent, 0);
        alarmMgr.cancel(alarmIntent);
        if(enable) {
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
        }
    }

    public static void triggerNotification(final Context context) {
        final Context pContext = context;
        new Thread() {
            @Override
            public void run() {
                DaysDatabase db = DaysDatabase.getInstance(App.getApplication().getApplicationContext());
                Calendar calendar = Calendar.getInstance();
                //if the day has already been filled with a rating we don't show the notification
                List<Day> days = db.dayDao().getAllByDate(calendar.get(java.util.Calendar.DAY_OF_MONTH), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.YEAR));
                if (!days.isEmpty()) {
                    Logger.getLogger("NotificationIntentService").log(new LogRecord(Level.INFO, "FMORALDO : triggerNotification : The day has already been rated, we skip the notification"));
                    return;
                }

                //Complex notifications
                RemoteViews notificationLayout = new RemoteViews(App.getApplication().getPackageName(), R.layout.notification_custom);
                RemoteViews notificationLayoutExpanded = new RemoteViews(App.getApplication().getPackageName(), R.layout.notification_custom_large);
                Intent oneIntent = new Intent(App.getApplication().getApplicationContext(), NotificationIntentService.class);
                oneIntent.setAction(ONE_STAR);
                notificationLayoutExpanded.setOnClickPendingIntent(R.id.Button1, PendingIntent.getService(App.getApplication().getApplicationContext(), 0, oneIntent, PendingIntent.FLAG_UPDATE_CURRENT));

                Intent twoIntent = new Intent(App.getApplication().getApplicationContext(), NotificationIntentService.class);
                twoIntent.setAction(TWO_STAR);
                notificationLayoutExpanded.setOnClickPendingIntent(R.id.Button2, PendingIntent.getService(App.getApplication().getApplicationContext(), 0, twoIntent, PendingIntent.FLAG_UPDATE_CURRENT));

                Intent threeIntent = new Intent(App.getApplication().getApplicationContext(), NotificationIntentService.class);
                threeIntent.setAction(THREE_STAR);
                notificationLayoutExpanded.setOnClickPendingIntent(R.id.Button3, PendingIntent.getService(App.getApplication().getApplicationContext(), 0, threeIntent, PendingIntent.FLAG_UPDATE_CURRENT));

                Intent fourIntent = new Intent(App.getApplication().getApplicationContext(), NotificationIntentService.class);
                fourIntent.setAction(FOUR_STAR);
                notificationLayoutExpanded.setOnClickPendingIntent(R.id.Button4, PendingIntent.getService(App.getApplication().getApplicationContext(), 0, fourIntent, PendingIntent.FLAG_UPDATE_CURRENT));

                Intent fiveIntent = new Intent(App.getApplication().getApplicationContext(), NotificationIntentService.class);
                fiveIntent.setAction(FIVE_STAR);
                notificationLayoutExpanded.setOnClickPendingIntent(R.id.Button5, PendingIntent.getService(App.getApplication().getApplicationContext(), 0, fiveIntent, PendingIntent.FLAG_UPDATE_CURRENT));


                Intent intent = new Intent(App.getApplication().getApplicationContext(), RateADay.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(RateADay.EXTRA_DATE_DAY, calendar.get(java.util.Calendar.DAY_OF_MONTH));
                intent.putExtra(RateADay.EXTRA_DATE_MONTH, calendar.get(java.util.Calendar.MONTH));
                intent.putExtra(RateADay.EXTRA_DATE_YEAR, calendar.get(java.util.Calendar.YEAR));
                PendingIntent pendingIntent = PendingIntent.getActivity(App.getApplication().getApplicationContext(), 0, intent, 0);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(App.getApplication().getApplicationContext(), CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_stars_24dp)
                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setCustomContentView(notificationLayout)
                        .setCustomBigContentView(notificationLayoutExpanded)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setSound(Uri.parse(PreferenceManager.getDefaultSharedPreferences(App.getApplication().getApplicationContext()).getString("notifications_new_message_ringtone", "content://settings/system/notification_sound")))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                if (PreferenceManager.getDefaultSharedPreferences(App.getApplication().getApplicationContext()).getBoolean("notifications_new_message_vibrate", true)) {
                    long[] pattern = new long []{500,110,500,110,450,110,200,110,170,40,450,110,200,110,170,40,500};
                    mBuilder.setVibrate(pattern);
                }
                NotificationManager notificationManager = (NotificationManager) pContext.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, mBuilder.build());
            }
        }.start();

    }
}
