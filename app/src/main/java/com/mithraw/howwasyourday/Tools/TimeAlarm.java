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

/*
Manage when an alarm is triggered
You have to add a <action android:name="@string/notificationIntentAction" /> in the manifest under <receiver android:name=".Tools.TimeAlarm"><intent-filter>
 */
public class TimeAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(intent.getAction() != null) {
            //Get the ressources for the action names
            Resources res = App.getApplication().getResources();
            String notificationIntentAction = res.getString(R.string.notificationIntentAction);
            String syncIntentAction = res.getString(R.string.syncIntentAction);
            Logger.getLogger("TimeAlarm").log(new LogRecord(Level.INFO,"FMORALDO : " + intent.getAction()));

            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) { //On reboot, we trigger the notification again
                NotificationHelper.setupNotificationStatus();
            } else if (intent.getAction().equals(notificationIntentAction)) { // When we receive a notification
                NotificationHelper.triggerNotification(context);
            }else if(intent.getAction().equals(syncIntentAction)){ //When we receive a sync request
                new SyncLauncher().SignInSuccess(GoogleSignInHelper.getInstance().getGoogleSignInAccount());
            }
        }else {
            Logger.getLogger("TimeAlarm").log(new LogRecord(Level.INFO, "FMORALDO : Intent with no action name "));
        }
    }

}
