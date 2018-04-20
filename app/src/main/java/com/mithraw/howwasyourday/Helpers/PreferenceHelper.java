package com.mithraw.howwasyourday.Helpers;

import android.preference.PreferenceManager;

import com.mithraw.howwasyourday.App;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/*
Convert and retrieve data from the preferences
 */
public class PreferenceHelper {
    public static int getSyncTime(){
        String timeSync = PreferenceManager.getDefaultSharedPreferences(App.getContext()).getString("sync_frequency","180");
        int timeInt = 0;
        try {
            timeInt = Integer.parseInt(timeSync);
        }catch(Exception e){
        }
        return timeInt;
    }
    public static int toTime(String timeSync){
        int timeInt = 0;
        try {
            timeInt = Integer.parseInt(timeSync);
        }catch(Exception e){
        }
        return timeInt;
    }
}
