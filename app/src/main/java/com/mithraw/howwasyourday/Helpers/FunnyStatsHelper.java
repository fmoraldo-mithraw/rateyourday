package com.mithraw.howwasyourday.Helpers;

import android.app.Activity;
import android.content.DialogInterface;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.mithraw.howwasyourday.App;
import com.mithraw.howwasyourday.R;

import java.util.Calendar;

/*
Handle the CardViews from funnyStats screen
 */
public class FunnyStatsHelper {
    SharingHelper mShareHelper;
    Activity mActivity;
    final CardView mCardView;
    boolean mPanelShowed = false;
    String mPreferenceName;

    public FunnyStatsHelper(CardView cardView, Activity activity, String preferenceName) {
        this.mCardView = cardView;
        this.mActivity = activity;
        this.mPreferenceName = preferenceName;
        // Set the click listener for showing the share button
        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPanelShowed = !mPanelShowed;
                if (mPanelShowed) {
                    if (mCardView.getVisibility() != View.GONE) {
                        LinearLayout layout = (LinearLayout) mCardView.findViewById(R.id.layout_share);
                        if (layout != null)
                            layout.setVisibility(View.VISIBLE);
                    }
                }else {
                    if (mCardView.getVisibility() != View.GONE) {
                        LinearLayout layout = (LinearLayout) mCardView.findViewById(R.id.layout_share);
                        if (layout != null)
                            layout.setVisibility(View.GONE);
                    }
                }
            }
        });

        //Configure the close button
        LinearLayout layout = (LinearLayout) mCardView.findViewById(R.id.layout_share);
        ImageButton btnClose = mCardView.findViewById(R.id.close_button);
        if (layout != null) {
            mShareHelper = new SharingHelper(mCardView, mActivity,
                    mCardView.findViewById(R.id.layout_share),
                    btnClose);
            mShareHelper.attachToButton((Button) mCardView.findViewById(R.id.button_share));
        }
        btnClose.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                alertDialogBuilder.setMessage(R.string.stat_removed)
                        .setTitle(R.string.stat_removed_title);
                alertDialogBuilder.setPositiveButton(R.string.stat_removed_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mCardView.setVisibility(View.GONE);
                        String lastKeyRemoved = mPreferenceName;
                        PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean(lastKeyRemoved, false).apply();
                        PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putInt(lastKeyRemoved + "_month", Calendar.getInstance().get(Calendar.MONTH)).apply();
                        PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putInt(lastKeyRemoved + "_year", Calendar.getInstance().get(Calendar.YEAR)).apply();
                    }
                });
                alertDialogBuilder.setNegativeButton(R.string.stat_removed_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                AlertDialog dialog = alertDialogBuilder.create();
                dialog.show();
            }
        });
        //End of the configuration of the close button

    }

}
