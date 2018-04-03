package com.example.mithraw.howwasyourday.Helpers;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class NotificationHelper {
    public static void updateNotificationStatus(boolean enable, String time, String ringtone, boolean vibration){
        Logger.getLogger("NotificationHelper").log(new LogRecord(Level.WARNING,enable + " " + time + " " + ringtone + " " + vibration));
    }
}
