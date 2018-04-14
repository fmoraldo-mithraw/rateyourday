package com.mithraw.howwasyourday.Tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import com.mithraw.howwasyourday.App;
import com.mithraw.howwasyourday.Helpers.GoogleSignInHelper;
import com.mithraw.howwasyourday.Helpers.NotificationHelper;
import com.mithraw.howwasyourday.Helpers.SyncLauncher;
import com.mithraw.howwasyourday.R;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class TimeAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Resources res = App.getApplication().getResources();
        String notificationIntentAction = res.getString(R.string.notificationIntentAction);
        String syncIntentAction = res.getString(R.string.syncIntentAction);
        if(intent.getAction() != null) {
            Logger.getLogger("TimeAlarm").log(new LogRecord(Level.INFO,"FMORALDO : " + intent.getAction()));
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                NotificationHelper.setupNotificationStatus();
            } else if (intent.getAction().equals(notificationIntentAction)) {
                NotificationHelper.triggerNotification(context);
            }else if(intent.getAction().equals(syncIntentAction)){
                new SyncLauncher().SignInSuccess(GoogleSignInHelper.getInstance().getGoogleSignInAccount());
            }
        }else {
            Logger.getLogger("TimeAlarm").log(new LogRecord(Level.INFO, "FMORALDO : Intent with no action name "));
        }
    }

}
