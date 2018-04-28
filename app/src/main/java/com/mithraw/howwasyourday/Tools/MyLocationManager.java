package com.mithraw.howwasyourday.Tools;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.mithraw.howwasyourday.App;

public class MyLocationManager {
    Activity mActivity = null;
    LocationListener mLocationListener;
    LocationManager mLocationManager;

    public MyLocationManager() {
        this(null);
    }

    public MyLocationManager(Activity act) {
        this(act, (LocationListener) null);
    }

    public MyLocationManager(Activity act, LocationListener locationListener) {
        mActivity = act;

        mLocationManager = (LocationManager) App.getContext().getSystemService(Context.LOCATION_SERVICE);

        if (locationListener == null) {
            // Define a listener that responds to location updates
            mLocationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };
        }else {
            mLocationListener = locationListener;
        }
    }

    public void init() {
        if (PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean("location_activated", false)) {
            if (mActivity != null) {
                if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
                    ActivityCompat.requestPermissions(mActivity, permissions, 0);
                    // Register the listener with the Location Manager to receive location updates
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
                }
            }
        }
    }

    public Location getLocation() {
        if (PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean("location_activated", false)) {
            if (mActivity != null) {
                if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
                    ActivityCompat.requestPermissions(mActivity, permissions, 0);
                }
            }
            Location lastKnownLocationWifi = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location lastKnownLocationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if ((lastKnownLocationWifi == null) && (lastKnownLocationGPS != null))
                return lastKnownLocationGPS;
            if ((lastKnownLocationWifi != null) && (lastKnownLocationGPS == null))
                return lastKnownLocationWifi;
            if ((lastKnownLocationWifi != null) && (lastKnownLocationGPS != null)) {
                if ((lastKnownLocationGPS.getAccuracy() > lastKnownLocationWifi.getAccuracy()) ||
                        ((lastKnownLocationGPS.getAccuracy() == 0) && (lastKnownLocationWifi.getAccuracy() != 0))) {
                    return lastKnownLocationWifi;
                } else {
                    return lastKnownLocationGPS;
                }
            }
        }
        return null;
    }

    public void clean() {
        mLocationManager.removeUpdates(mLocationListener);
    }
}
