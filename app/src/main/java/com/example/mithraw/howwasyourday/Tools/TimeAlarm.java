package com.example.mithraw.howwasyourday.Tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mithraw.howwasyourday.Helpers.NotificationHelper;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class TimeAlarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(intent.getAction() != null) {
            Logger.getLogger("TimeAlarm").log(new LogRecord(Level.WARNING,"FMORALDO : " + intent.getAction()));
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                NotificationHelper.setupNotificationStatus();
            } else if (intent.getAction().equals("com.example.mithraw.howwasyourday.alarm")) {
                NotificationHelper.triggerNotification();
            }

        }else {
            Logger.getLogger("TimeAlarm").log(new LogRecord(Level.WARNING, "FMORALDO : Intent with no action name "));
        }
    }

}
