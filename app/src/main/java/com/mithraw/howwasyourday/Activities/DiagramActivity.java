package com.mithraw.howwasyourday.Activities;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.mithraw.howwasyourday.R;
import com.mithraw.howwasyourday.WeekView;
import com.mithraw.howwasyourday.databases.DaysDatabase;

import java.util.Calendar;

public class DiagramActivity extends AppCompatActivity {

    private enum MSG_ID {
        MONDAY_CURRENT_YEAR,
        TUESDAY_CURRENT_YEAR,
        WEDNESDAY_CURRENT_YEAR,
        THURSDAY_CURRENT_YEAR,
        FRIDAY_CURRENT_YEAR,
        SATURDAY_CURRENT_YEAR,
        SUNDAY_CURRENT_YEAR,
        MONDAY_ALL_YEARS,
        TUESDAY_ALL_YEARS,
        WEDNESDAY_ALL_YEARS,
        THURSDAY_ALL_YEARS,
        FRIDAY_ALL_YEARS,
        SATURDAY_ALL_YEARS,
        SUNDAY_ALL_YEARS,
    }

    protected static Handler handler;
    protected DaysDatabase db;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagram);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = DaysDatabase.getInstance(getApplicationContext());


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                WeekView viewCur = new WeekView((View) findViewById(R.id.current_year_week));
                WeekView viewAll = new WeekView((View) findViewById(R.id.all_time_week));
                if (msg.what == MSG_ID.MONDAY_CURRENT_YEAR.ordinal()) {
                    viewCur.updateDay(WeekView.DAYS.MONDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.TUESDAY_CURRENT_YEAR.ordinal()) {
                    viewCur.updateDay(WeekView.DAYS.TUESDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.WEDNESDAY_CURRENT_YEAR.ordinal()) {
                    viewCur.updateDay(WeekView.DAYS.WEDNESDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.THURSDAY_CURRENT_YEAR.ordinal()) {
                    viewCur.updateDay(WeekView.DAYS.THURSDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.FRIDAY_CURRENT_YEAR.ordinal()) {
                    viewCur.updateDay(WeekView.DAYS.FRIDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.SATURDAY_CURRENT_YEAR.ordinal()) {
                    viewCur.updateDay(WeekView.DAYS.SATURDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.SUNDAY_CURRENT_YEAR.ordinal()) {
                    viewCur.updateDay(WeekView.DAYS.SUNDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.MONDAY_ALL_YEARS.ordinal()) {
                    viewAll.updateDay(WeekView.DAYS.MONDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.TUESDAY_ALL_YEARS.ordinal()) {
                    viewAll.updateDay(WeekView.DAYS.TUESDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.WEDNESDAY_ALL_YEARS.ordinal()) {
                    viewAll.updateDay(WeekView.DAYS.WEDNESDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.THURSDAY_ALL_YEARS.ordinal()) {
                    viewAll.updateDay(WeekView.DAYS.THURSDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.FRIDAY_ALL_YEARS.ordinal()) {
                    viewAll.updateDay(WeekView.DAYS.FRIDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.SATURDAY_ALL_YEARS.ordinal()) {
                    viewAll.updateDay(WeekView.DAYS.SATURDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.SUNDAY_ALL_YEARS.ordinal()) {
                    viewAll.updateDay(WeekView.DAYS.SUNDAY, (float) msg.obj);
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        Resources res = getResources();
        WeekView viewCur = new WeekView((View) findViewById(R.id.current_year_week));
        viewCur.setTitle(res.getString(R.string.current_diagram_title));

        WeekView viewAll = new WeekView((View) findViewById(R.id.all_time_week));
        viewAll.setTitle(res.getString(R.string.all_years_diagram_title));
        new Thread() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                for (int i = 0; i < 7; i++) {
                    int curDay = Calendar.MONDAY + i;
                    if (i == 6)
                        curDay = Calendar.SUNDAY;
                    float ret = db.dayDao().getAverageRatingPerDayOfTheWeekAndYear(curDay, calendar.get(java.util.Calendar.YEAR));
                    Message msg_rating = Message.obtain();
                    msg_rating.what = (MSG_ID.MONDAY_CURRENT_YEAR.ordinal() + i);
                    msg_rating.obj = ret;
                    handler.sendMessage(msg_rating);
                }
                for (int i = 0; i < 7; i++) {
                    int curDay = Calendar.MONDAY + i;
                    if (i == 6)
                        curDay = Calendar.SUNDAY;
                    float ret = db.dayDao().getAverageRatingPerDayOfTheWeek(curDay);
                    Message msg_rating = Message.obtain();
                    msg_rating.what = (MSG_ID.MONDAY_ALL_YEARS.ordinal() + i);
                    msg_rating.obj = ret;
                    handler.sendMessage(msg_rating);
                }
            }
        }.start();
    }
    @Override
    public void onBackPressed() {
        finish();
        return;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
