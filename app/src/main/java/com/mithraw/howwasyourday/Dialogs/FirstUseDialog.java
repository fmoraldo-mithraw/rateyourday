package com.mithraw.howwasyourday.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.mithraw.howwasyourday.Helpers.GoogleSignInHelper;
import com.mithraw.howwasyourday.Helpers.NotificationHelper;
import com.mithraw.howwasyourday.Helpers.SyncLauncher;
import com.mithraw.howwasyourday.R;

public class FirstUseDialog extends DialogFragment {
    Switch syncSwitch;
    Switch notificationSwitch;
    View mView;
    ViewGroup mContainer;

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
                        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean("first_use_screen_showed", true).apply();

                    }
                }).setCancelable(false);
        syncSwitch = mView.findViewById(R.id.sync_switch);
        syncSwitch.setChecked(!PreferenceManager.getDefaultSharedPreferences(getContext()).getString("sync_frequency","1440").equals("0"));
        notificationSwitch = mView.findViewById(R.id.notifications_switch);
        notificationSwitch.setChecked(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("use_notifications", true));
        AlertDialog ad = builder.create();
        ad.setCanceledOnTouchOutside(false);
        return ad;
    }
}
