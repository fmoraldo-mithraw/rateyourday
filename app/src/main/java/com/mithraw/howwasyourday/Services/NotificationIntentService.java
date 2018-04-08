package com.mithraw.howwasyourday.Services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Looper;
import android.widget.Toast;

import com.mithraw.howwasyourday.App;
import com.mithraw.howwasyourday.Helpers.NotificationHelper;
import com.mithraw.howwasyourday.R;
import com.mithraw.howwasyourday.databases.Day;
import com.mithraw.howwasyourday.databases.DaysDatabase;

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import io.reactivex.annotations.Nullable;

public class NotificationIntentService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public class notificationRunnable implements Runnable {
        private String toastString;
        private int rate;
        public notificationRunnable(String toastString,int rate) {
            this.toastString = toastString;
            this.rate = rate;
        }

        public void run() {
            DaysDatabase db = DaysDatabase.getInstance(App.getApplication().getApplicationContext());
            Calendar calendar = Calendar.getInstance();
            Day d = new Day(calendar.get(Calendar.DAY_OF_WEEK),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.YEAR),
                    rate,
                    "",
                    "");
            db.dayDao().insertDay(d);
            Looper.prepare();
            Toast.makeText(getBaseContext(), toastString, Toast.LENGTH_LONG).show();
            Looper.loop();
        }
    }
    public NotificationIntentService() {
        super("notificationIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(App.getApplication().NOTIFICATION_SERVICE);
        mNotificationManager.cancel(0);
        Resources res = App.getApplication().getResources();
        Logger.getLogger("NotificationIntentService").log(new LogRecord(Level.INFO, "FMORALDO : NotificationIntentService triggered " + intent.getAction()));
        String toastString = res.getString(R.string.notification_default_star);
        int rate = 1;
        switch (intent.getAction()) {
            case NotificationHelper.ONE_STAR:
                toastString = res.getString(R.string.notification_one_star);
                rate = 1;
                break;
            case NotificationHelper.TWO_STAR:
                toastString = res.getString(R.string.notification_two_star);
                rate = 2;
                break;
            case NotificationHelper.THREE_STAR:
                toastString = res.getString(R.string.notification_three_star);
                rate = 3;
                break;
            case NotificationHelper.FOUR_STAR:
                toastString = res.getString(R.string.notification_four_star);
                rate = 4;
                break;
            case NotificationHelper.FIVE_STAR:
                toastString = res.getString(R.string.notification_five_star);
                rate = 5;
                break;

        }
        Thread t = new Thread(new notificationRunnable(toastString,rate));
        t.start();
    }
}