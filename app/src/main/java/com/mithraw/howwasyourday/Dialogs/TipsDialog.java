package com.mithraw.howwasyourday.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mithraw.howwasyourday.R;

import java.util.ArrayList;

/*
A tips dialog
Showed by
        String preferenceName = "tip_main_showed";
        if ((PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(preferenceName, false) == false)&&
            (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("show_tips", true) == true)){
            ArrayList<Integer> iList = new ArrayList<Integer>();
            iList.add(R.layout.tips_fragment_main1);
            iList.add(R.layout.tips_fragment_main2);
            Bundle bundl = new Bundle();
            bundl.putIntegerArrayList("listView", iList);
            bundl.putString("preference", preferenceName);
            bundl.putInt("title", R.string.tips_main_title);
            DialogFragment newFragment = new TipsDialog();
            newFragment.setArguments(bundl);
            newFragment.show(getSupportFragmentManager(), preferenceName);
        }
 */
public class TipsDialog extends DialogFragment {
    Button previousButton;
    Button nextButton;
    CheckBox mNeverDisplayCheckbox;
    View mView;
    ArrayList<Integer> mListView;
    LinearLayout mLayout;
    SeekBar mSeekBar;
    String mPreferenceName;
    int currentScreen = 0;
    LayoutInflater mInflater;
    View mLastView;
    int mTextId;
    ViewGroup mContainer;

    private void updateDialogState() {
        if (mListView.size() == currentScreen) {
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(mPreferenceName, true).apply();
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean("show_tips", !mNeverDisplayCheckbox.isChecked()).apply();
            dismiss();
        }
        if (currentScreen < mListView.size()) {
            mLayout.removeView(mLastView);
            mLastView = mInflater.inflate(mListView.get(currentScreen), null);
            mLayout.addView(mLastView);
        }
        if (mListView.size() == (currentScreen + 1)) {
            nextButton.setText(R.string.stat_removed_ok);
        } else {
            nextButton.setText(R.string.button_next);
        }
        previousButton.setEnabled(!(currentScreen == 0));
        if (Build.VERSION.SDK_INT >= 24) {
            mSeekBar.setProgress(currentScreen, true);
        } else {
            mSeekBar.setProgress(currentScreen);
        }

    }
    private View getLayout(LayoutInflater inflater, ViewGroup container) {
        mView = inflater.inflate(R.layout.tips_dialog_container, container, false);
        return mView;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContainer = container;
        if (getShowsDialog()) {
            // one could return null here, or be nice and call super()
            return super.onCreateView(inflater, container, savedInstanceState);
        }
        return getLayout(inflater, container);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(getLayout(LayoutInflater.from(getContext()), mContainer))
                // Add action buttons
                .setCancelable(false);
        AlertDialog ad = builder.create();
        ad.setCanceledOnTouchOutside(false);
        //Retrieve arguments
        Bundle bundle = getArguments();
        mListView = bundle.getIntegerArrayList("listView");
        mPreferenceName = bundle.getString("preference");
        mTextId = bundle.getInt("title");
        TextView title = mView.findViewById(R.id.tips_title);
        title.setText(mTextId);

        previousButton = mView.findViewById(R.id.button_previous);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentScreen--;
                updateDialogState();
            }
        });
        nextButton = mView.findViewById(R.id.button_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentScreen++;
                updateDialogState();
            }
        });
        mNeverDisplayCheckbox = mView.findViewById(R.id.checkbox_never_display);
        mNeverDisplayCheckbox.setChecked(false);
        mSeekBar = mView.findViewById(R.id.seek_progress_bar_tips);
        mSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        // TODO Auto-generated method stub
                        currentScreen = progress;
                        updateDialogState();
                    }
                });
        mLayout = mView.findViewById(R.id.layout_content);
        mInflater = getActivity().getLayoutInflater();
        mLastView = mInflater.inflate(mListView.get(currentScreen), null);
        mLayout.addView(mLastView);
        if (mListView.size() == 1) {
            previousButton.setVisibility(View.GONE);
            mSeekBar.setVisibility(View.GONE);
            nextButton.setText(R.string.stat_removed_ok);
        } else {
            mSeekBar.setMax(mListView.size() - 1);
            mSeekBar.setProgress(0);
        }
        return ad;
    }
}
