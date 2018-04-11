package com.mithraw.howwasyourday.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mithraw.howwasyourday.Helpers.RateView;
import com.mithraw.howwasyourday.R;
import com.mithraw.howwasyourday.databases.Day;
import com.mithraw.howwasyourday.databases.DaysDatabase;

import java.util.Calendar;
import java.util.List;

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
    RateView mRateView;
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_aday);

        //Add the back button to the activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Retreive the database
        db = DaysDatabase.getInstance(getApplicationContext());

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                EditText titleText = (EditText) findViewById(R.id.titleText);
                EditText logText = (EditText) findViewById(R.id.logText);

                if (msg.what == MSG_ID.MSG_RATING.ordinal()) {
                    mRateView.setRating((Integer) (msg.obj));
                    mTitleText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                    mLogText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                } else if (msg.what == MSG_ID.MSG_LOG.ordinal()) {
                    logText.setText((String) (msg.obj));
                    mTitleText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                    mLogText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);

                } else if (msg.what == MSG_ID.MSG_TITLE.ordinal()) {
                    titleText.setText((String) (msg.obj));
                    mTitleText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                    mLogText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                }
                if (msg.what == MSG_ID.MSG_EMPTY.ordinal()) {
                    titleText.setText("");
                    logText.setText("");
                    mRateView.setRating(0);
                    mTitleText.setInputType(InputType.TYPE_NULL);
                    mLogText.setInputType(InputType.TYPE_NULL);
                }
            }
        };
        // Retreive the informations of the date from the main activity and fill the calendar with them
        Intent intent = getIntent();
        m_calendar.set(java.util.Calendar.DAY_OF_MONTH, intent.getIntExtra(EXTRA_DATE_DAY, 0));
        m_calendar.set(java.util.Calendar.MONTH, intent.getIntExtra(EXTRA_DATE_MONTH, 0));
        m_calendar.set(java.util.Calendar.YEAR, intent.getIntExtra(EXTRA_DATE_YEAR, 0));

        // Adjusting the controls of the page
        mTitleText = (EditText) findViewById(R.id.titleText);
        mLogText = (EditText) findViewById(R.id.logText);
        mTitleText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mRateView.getRating() == 0) {
                    Toast.makeText(getBaseContext(), R.string.cant_click, Toast.LENGTH_LONG).show();
                } else {
                    mTitleText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                    mLogText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                }
                return false;
            }
        });
        mLogText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mRateView.getRating() == 0) {
                    Toast.makeText(getBaseContext(), R.string.cant_click, Toast.LENGTH_LONG).show();
                } else {
                    mTitleText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                    mLogText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                }
                return false;
            }

        });
        EditText dateText = (EditText) findViewById(R.id.dateTextView);
        mRateView = new RateView((View) findViewById(R.id.ratingBar));

        mRateView.setOnRateChanged(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDay();
                mTitleText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                mLogText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);

            }
        });
       mTitleText.setEnabled(true);
        mLogText.setEnabled(true);
        mTitleText.setEnabled(true);
        mLogText.setEnabled(true);
        mTitleText.setInputType(InputType.TYPE_NULL);
        mLogText.setInputType(InputType.TYPE_NULL);

        dateText.setEnabled(false);
        //Fill the controls with the correct informations
        fillTheInformations();
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        dateText.setText(dateFormat.format(m_calendar.getTime()));
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
                EditText titleText = (EditText) findViewById(R.id.titleText);
                EditText logText = (EditText) findViewById(R.id.logText);
                Day d = new Day(m_calendar.get(Calendar.DAY_OF_WEEK),
                        m_calendar.get(Calendar.DAY_OF_MONTH),
                        m_calendar.get(Calendar.MONTH),
                        m_calendar.get(Calendar.YEAR),
                        m_calendar.get(Calendar.WEEK_OF_YEAR),
                        m_calendar.getTimeInMillis(),
                        (int) (mRateView.getRating()),
                        titleText.getText().toString(),
                        logText.getText().toString());
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
