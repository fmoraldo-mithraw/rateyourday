package com.mithraw.howwasyourday.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mithraw.howwasyourday.App;
import com.mithraw.howwasyourday.Dialogs.TipsDialog;
import com.mithraw.howwasyourday.Helpers.RateViewHelper;
import com.mithraw.howwasyourday.R;
import com.mithraw.howwasyourday.databases.Day;
import com.mithraw.howwasyourday.databases.DaysDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/*
RateADay Activity shows a screen that permit you to save informations on the day
Saved on a ratingView click or when returned
 */
public class RateADay extends AppCompatActivity {
    private enum MSG_ID {MSG_RATING, MSG_TITLE, MSG_LOG, MSG_EMPTY}

    protected final java.util.Calendar m_calendar = java.util.Calendar.getInstance();
    protected DaysDatabase db;
    protected static Handler handler;
    public static final String EXTRA_DATE_DAY = "extra_date_day";
    public static final String EXTRA_DATE_MONTH = "extra_date_month";
    public static final String EXTRA_DATE_YEAR = "extra_date_year";
    EditText mTitleText;
    EditText mLogText;
    RateViewHelper mRateView;
    int mFlagsTitle;
    int mFlagsLog;

    @SuppressLint({"HandlerLeak", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_aday);

        //Add the back button to the activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String preferenceName = "tip_rate_showed";
        //PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(preferenceName, false).apply(); // TODO to remove outside tests
        if ((!(PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean(preferenceName, false)))&&
                (PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean("show_tips", true))){
            ArrayList<Integer> iList = new ArrayList<Integer>();
            iList.add(R.layout.tips_fragment_rate);
            Bundle bundl = new Bundle();
            bundl.putIntegerArrayList("listView", iList);
            bundl.putString("preference", preferenceName);
            bundl.putInt("title", R.string.tips_rate_title);
            DialogFragment newFragment = new TipsDialog();
            newFragment.setArguments(bundl);
            newFragment.show(getSupportFragmentManager(), preferenceName);
        }
        //Retrieve the database
        db = DaysDatabase.getInstance(getApplicationContext());

        mTitleText =  findViewById(R.id.titleTextRate);
        mFlagsTitle = mTitleText.getInputType();
        mLogText =  findViewById(R.id.logTextRate);
        mFlagsLog = mLogText.getInputType();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_ID.MSG_RATING.ordinal()) {
                    mRateView.setRating((Integer) (msg.obj));
                    allowFocusOnTexts(true);
                } else if (msg.what == MSG_ID.MSG_LOG.ordinal()) {
                    mLogText.setText((String) (msg.obj));
                    allowFocusOnTexts(true);

                } else if (msg.what == MSG_ID.MSG_TITLE.ordinal()) {
                    mTitleText.setText((String) (msg.obj));
                    allowFocusOnTexts(true);
                }
                if (msg.what == MSG_ID.MSG_EMPTY.ordinal()) {
                    mTitleText.setText("");
                    mLogText.setText("");
                    mRateView.setRating(0);
                    allowFocusOnTexts(false);
                }
            }
        };
        // Retreive the informations of the date from the main activity and fill the calendar with them
        Intent intent = getIntent();
        m_calendar.set(java.util.Calendar.DAY_OF_MONTH, intent.getIntExtra(EXTRA_DATE_DAY, 0));
        m_calendar.set(java.util.Calendar.MONTH, intent.getIntExtra(EXTRA_DATE_MONTH, 0));
        m_calendar.set(java.util.Calendar.YEAR, intent.getIntExtra(EXTRA_DATE_YEAR, 0));

        // Adjusting the controls of the page

        mTitleText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mRateView.getRating() == 0) {
                    ((EditText)v).setText("");
                    Toast.makeText(getBaseContext(), R.string.cant_click, Toast.LENGTH_LONG).show();
                } else {
                    allowFocusOnTexts(true);
                }

                return false;
            }
        });

        mLogText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mRateView.getRating() == 0) {
                    ((EditText)v).setText("");
                    Toast.makeText(getBaseContext(), R.string.cant_click, Toast.LENGTH_LONG).show();
                } else {
                    allowFocusOnTexts(true);
                }
                return false;
            }

        });


        TextView dateText = (TextView) findViewById(R.id.dateTextView);
        mRateView = new RateViewHelper((View) findViewById(R.id.ratingBar));
        mRateView.setOnRateChanged(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowFocusOnTexts(true);
                saveDay();
            }
        });
        allowFocusOnTexts(false);

        //Fill the controls with the correct informations
        fillTheInformations();
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        dateText.setText(dateFormat.format(m_calendar.getTime()));
    }

    private void allowFocusOnTexts(boolean allow) {
        if (allow) {
            mTitleText.setInputType(mFlagsTitle);
            mLogText.setInputType(mFlagsLog);
            Logger.getLogger("SettingsActivity").log(new LogRecord(Level.INFO, "FMORALDO : allowFocusOnTexts " + mLogText.getInputType()));
        } else {
            mTitleText.setInputType(InputType.TYPE_NULL);
            mLogText.setInputType(InputType.TYPE_NULL);

        }

    }

    protected void fillTheInformations() {
        new Thread() {
            @Override
            public void run() {
                List<Day> days = db.dayDao().getAllByDate(m_calendar.get(java.util.Calendar.DAY_OF_MONTH), m_calendar.get(java.util.Calendar.MONTH), m_calendar.get(java.util.Calendar.YEAR));
                if (days.isEmpty()) {
                    handler.sendEmptyMessage(MSG_ID.MSG_EMPTY.ordinal());
                } else {
                    Message msg_rating = Message.obtain();
                    msg_rating.what = MSG_ID.MSG_RATING.ordinal();
                    msg_rating.obj = days.get(0).getRating();
                    handler.sendMessage(msg_rating);

                    Message msg_title = Message.obtain();
                    msg_title.what = MSG_ID.MSG_TITLE.ordinal();
                    msg_title.obj = days.get(0).getTitleText();
                    handler.sendMessage(msg_title);

                    Message msg_log = Message.obtain();
                    msg_log.what = MSG_ID.MSG_LOG.ordinal();
                    msg_log.obj = days.get(0).getLog();
                    handler.sendMessage(msg_log);
                }
            }
        }.start();
    }

    protected boolean saveDay() {
        new Thread() {
            @Override
            public void run() {
                if (mRateView.getRating() == 0)
                    return;
                Day d = new Day(m_calendar.get(Calendar.DAY_OF_WEEK),
                        m_calendar.get(Calendar.DAY_OF_MONTH),
                        m_calendar.get(Calendar.MONTH),
                        m_calendar.get(Calendar.YEAR),
                        m_calendar.get(Calendar.WEEK_OF_YEAR),
                        m_calendar.getTimeInMillis(),
                        (int) (mRateView.getRating()),
                        mTitleText.getText().toString(),
                        mLogText.getText().toString(),
                        false);
                db.dayDao().insertDay(d);
            }
        }.start();
        if (mRateView.getRating() == 0)
            return false;
        return true;
    }

    private void endActivity() {
        Intent myIntent = getIntent();
        if(saveDay())
            setResult((int) mRateView.getRating(), myIntent);
        else
            setResult(Activity.RESULT_CANCELED,myIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        endActivity();
        return;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        endActivity();
        return true;
    }


}
