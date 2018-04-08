package com.mithraw.howwasyourday;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

public class App extends Application {

    private static Application sApplication;

    public static Application getApplication() {
        return sApplication;
    }

    public static Context getContext() {
        return getApplication().getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        sApplication = this;
    }
}