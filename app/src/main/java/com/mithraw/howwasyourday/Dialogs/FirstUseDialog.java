package com.mithraw.howwasyourday.Dialogs;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.mithraw.howwasyourday.Helpers.BitmapHelper;
import com.mithraw.howwasyourday.Helpers.GoogleSignInHelper;
import com.mithraw.howwasyourday.Helpers.NotificationHelper;
import com.mithraw.howwasyourday.Helpers.SyncLauncher;
import com.mithraw.howwasyourday.R;

public class FirstUseDialog extends DialogFragment {
    Switch syncSwitch;
    Switch notificationSwitch;
    Switch externalStorageSwitch;
    Switch locationSwitch;
    View mView;
    ViewGroup mContainer;
    boolean externalStorageState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContainer = container;
        if (getShowsDialog()) {
            // one could return null here, or be nice and call super()
            return super.onCreateView(inflater, container, savedInstanceState);
        }
        return getLayout(inflater, container);
    }
    private View getLayout(LayoutInflater inflater, ViewGroup container) {
        mView = inflater.inflate(R.layout.dialog_first_use, container, false);
        return mView;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(getLayout(LayoutInflater.from(getContext()), mContainer))
                // Add action buttons
                .setPositiveButton(R.string.stat_removed_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (syncSwitch.isChecked()) {
                            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("sync_frequency", "1440").apply();
                            GoogleSignInHelper.getInstance(getActivity()).doSignIn(new SyncLauncher());
                        } else {
                            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("sync_frequency", "0").apply();
                        }
                        if (notificationSwitch.isChecked()) {
                            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean("use_notifications", true).apply();
                            NotificationHelper.setupNotificationStatus();
                        } else {
                            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean("use_notifications", false).apply();
                        }
                        if (externalStorageSwitch.isChecked()) {
                            if ((ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED) ||
                                    (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED)) {
                                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                                requestPermissions(permissions, 0);

                            }
                            if (!externalStorageState)
                                BitmapHelper.moveImages();
                            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean("save_images_on_external_drive", true).apply();
                        } else {
                            if (externalStorageState)
                                BitmapHelper.moveImages();
                            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean("save_images_on_external_drive", false).apply();
                        }
                        if (locationSwitch.isChecked()) {
                            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED) {
                                String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
                                requestPermissions(permissions, 0);
                            }
                            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean("location_activated", true).apply();
                        } else {
                            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean("location_activated", false).apply();
                        }

                        //Set first use panel shown = true
                        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean("first_use_screen_showed", true).apply();

                    }
                }).setCancelable(false);
        syncSwitch = mView.findViewById(R.id.sync_switch);
        syncSwitch.setChecked(!PreferenceManager.getDefaultSharedPreferences(getContext()).getString("sync_frequency","1440").equals("0"));
        notificationSwitch = mView.findViewById(R.id.notifications_switch);
        notificationSwitch.setChecked(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("use_notifications", true));
        externalStorageSwitch = mView.findViewById(R.id.external_drive);
        externalStorageState = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("location_activated", false);
        locationSwitch = mView.findViewById(R.id.location_switch);
        if (!BitmapHelper.isExternalStorageAvailable()) {
            externalStorageSwitch.setChecked(false);
            externalStorageSwitch.setEnabled(false);
        }
        externalStorageSwitch.setChecked(externalStorageState);
        AlertDialog ad = builder.create();
        ad.setCanceledOnTouchOutside(false);
        return ad;
    }
}
