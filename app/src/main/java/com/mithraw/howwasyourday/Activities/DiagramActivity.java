package com.mithraw.howwasyourday.Activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;

import com.mithraw.howwasyourday.App;
import com.mithraw.howwasyourday.R;
import com.mithraw.howwasyourday.Helpers.WeekView;
import com.mithraw.howwasyourday.Helpers.MonthView;
import com.mithraw.howwasyourday.databases.Day;
import com.mithraw.howwasyourday.databases.DaysDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class DiagramActivity extends AppCompatActivity {
    private enum TYPE_DATE {START_DATE, END_DATE}
    private enum MSG_ID {
        MONDAY_CUSTOM,
        TUESDAY_CUSTOM,
        WEDNESDAY_CUSTOM,
        THURSDAY_CUSTOM,
        FRIDAY_CUSTOM,
        SATURDAY_CUSTOM,
        SUNDAY_CUSTOM,
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
        JANUARY_CURRENT_YEAR,
        FEBRUARY_CURRENT_YEAR,
        MARCH_CURRENT_YEAR,
        APRIL_CURRENT_YEAR,
        MAY_CURRENT_YEAR,
        JUNE_CURRENT_YEAR,
        JULY_CURRENT_YEAR,
        AUGUST_CURRENT_YEAR,
        SEPTEMBER_CURRENT_YEAR,
        OCTOBER_CURRENT_YEAR,
        NOVEMBER_CURRENT_YEAR,
        DECEMBER_CURRENT_YEAR,
        JANUARY_ALL_TIME,
        FEBRUARY_ALL_TIME,
        MARCH_ALL_TIME,
        APRIL_ALL_TIME,
        MAY_ALL_TIME,
        JUNE_ALL_TIME,
        JULY_ALL_TIME,
        AUGUST_ALL_TIME,
        SEPTEMBER_ALL_TIME,
        OCTOBER_ALL_TIME,
        NOVEMBER_ALL_TIME,
        DECEMBER_ALL_TIME
    }

    private DatePickerDialog startDatePickerDialog = null;
    private DatePickerDialog endDatePickerDialog = null;
    private Date startDate;
    private Date endDate;
    public DatePickerDialog getStartDatePickerDialog() {
        return startDatePickerDialog;
    }

    public DatePickerDialog getEndDatePickerDialog() {
        return endDatePickerDialog;
    }

    public void onClickStartDate(View v) {
        DatePickerDialog datePickerDialog = getStartDatePickerDialog();
        if (datePickerDialog != null) {
            datePickerDialog.show();
        }
    }

    public void onClickEndDate(View v) {
        DatePickerDialog datePickerDialog = getEndDatePickerDialog();
        if (datePickerDialog != null) {
            datePickerDialog.show();
        }
    }
    private void updateDateTextView(TextView tv, Date d) {
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(App.getApplication());
        tv.setText(dateFormat.format(d));
    }

    private void updateDate(int year, int month, int day, TYPE_DATE typeDate) {
        if (typeDate == TYPE_DATE.START_DATE) {
            startDate = new Date(year - 1900, month, day);
            updateDateTextView((TextView) findViewById(R.id.text_view_start_date), startDate);
        } else {
            endDate = new Date(year - 1900, month, day);
            updateDateTextView((TextView) findViewById(R.id.text_view_end_date), endDate);
        }
        updateDataSet();
    }
    private void updateDataSet(){
        new Thread() {
            @Override
            public void run() {
                Calendar cal = Calendar.getInstance();
                cal.setTime(startDate);
                long startDateInt = cal.getTimeInMillis();
                Logger.getLogger("LogsAdapter").log(new LogRecord(Level.INFO, "FMORALDO : LogsAdapter date : " + startDateInt));
                cal.setTime(endDate);
                cal.add(Calendar.DAY_OF_MONTH,1);
                long endDateInt = cal.getTimeInMillis();

                float avgMonday = db.dayDao().getAverageRatingByBoundsAndDayOfTheMonth(startDateInt, endDateInt, Calendar.MONDAY);
                Message msg_rating = Message.obtain();
                msg_rating.what = MSG_ID.MONDAY_CUSTOM.ordinal();
                msg_rating.obj = avgMonday;
                handler.sendMessage(msg_rating);

                float avgTuesday = db.dayDao().getAverageRatingByBoundsAndDayOfTheMonth(startDateInt, endDateInt, Calendar.TUESDAY);
                msg_rating = Message.obtain();
                msg_rating.what = MSG_ID.TUESDAY_CUSTOM.ordinal();
                msg_rating.obj = avgTuesday;
                handler.sendMessage(msg_rating);

                float avgWednesday = db.dayDao().getAverageRatingByBoundsAndDayOfTheMonth(startDateInt, endDateInt, Calendar.WEDNESDAY);
                msg_rating = Message.obtain();
                msg_rating.what = MSG_ID.WEDNESDAY_CUSTOM.ordinal();
                msg_rating.obj = avgWednesday;
                handler.sendMessage(msg_rating);

                float avgThursday = db.dayDao().getAverageRatingByBoundsAndDayOfTheMonth(startDateInt, endDateInt, Calendar.THURSDAY);
                msg_rating = Message.obtain();
                msg_rating.what = MSG_ID.THURSDAY_CUSTOM.ordinal();
                msg_rating.obj = avgThursday;
                handler.sendMessage(msg_rating);

                float avgFriday = db.dayDao().getAverageRatingByBoundsAndDayOfTheMonth(startDateInt, endDateInt, Calendar.FRIDAY);
                msg_rating = Message.obtain();
                msg_rating.what = MSG_ID.FRIDAY_CUSTOM.ordinal();
                msg_rating.obj = avgFriday;
                handler.sendMessage(msg_rating);

                float avgSaturday = db.dayDao().getAverageRatingByBoundsAndDayOfTheMonth(startDateInt, endDateInt, Calendar.SATURDAY);
                msg_rating = Message.obtain();
                msg_rating.what = MSG_ID.SATURDAY_CUSTOM.ordinal();
                msg_rating.obj = avgSaturday;
                handler.sendMessage(msg_rating);

                float avgSunday = db.dayDao().getAverageRatingByBoundsAndDayOfTheMonth(startDateInt, endDateInt, Calendar.SUNDAY);
                msg_rating = Message.obtain();
                msg_rating.what = MSG_ID.SUNDAY_CUSTOM.ordinal();
                msg_rating.obj = avgSunday;
                handler.sendMessage(msg_rating);
            }
        }.start();
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
        Resources res = getResources();

        View myView = findViewById(R.id.custom_week);
        myView.findViewById(R.id.title).setVisibility(View.GONE);

        WeekView viewCur = new WeekView((View) findViewById(R.id.current_year_week));
        viewCur.setTitle(res.getString(R.string.current_diagram_title));

        WeekView viewAll = new WeekView((View) findViewById(R.id.all_time_week));
        viewAll.setTitle(res.getString(R.string.all_years_diagram_title));

        MonthView viewYearCur = new MonthView((View) findViewById(R.id.current_months));
        viewYearCur.setTitle(res.getString(R.string.current_months_diagram_title));

        MonthView viewYearAll = new MonthView((View) findViewById(R.id.all_time_months));
        viewYearAll.setTitle(res.getString(R.string.all_years_months_diagram_title));
        //Setup the date listener
        //StarDate
        startDate = new Date(118, 0, 1);
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        updateDateTextView((TextView) findViewById(R.id.text_view_end_date), startDate);
        final DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                updateDate(year, monthOfYear, dayOfMonth, TYPE_DATE.START_DATE);
            }

        };
        startDatePickerDialog = new DatePickerDialog(this, startDateListener, cal
                .get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.MONTH),
                cal.get(java.util.Calendar.DAY_OF_MONTH));

        //EndDate
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.DAY_OF_MONTH, 1);
        endDate = cal.getTime();
        updateDateTextView((TextView) findViewById(R.id.text_view_end_date), endDate);
        final DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                updateDate(year, monthOfYear, dayOfMonth, TYPE_DATE.END_DATE);
            }

        };
        endDatePickerDialog = new DatePickerDialog(this, endDateListener, cal
                .get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.MONTH),
                cal.get(java.util.Calendar.DAY_OF_MONTH));

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                WeekView viewCur = new WeekView((View) findViewById(R.id.current_year_week));
                WeekView viewCustom = new WeekView((View) findViewById(R.id.custom_week));
                WeekView viewAll = new WeekView((View) findViewById(R.id.all_time_week));
                MonthView viewYearCur = new MonthView((View) findViewById(R.id.current_months));
                MonthView viewYearAll = new MonthView((View) findViewById(R.id.all_time_months));

                if (msg.what == MSG_ID.MONDAY_CUSTOM.ordinal()) {
                    viewCustom.updateDay(WeekView.DAYS.MONDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.TUESDAY_CUSTOM.ordinal()) {
                    viewCustom.updateDay(WeekView.DAYS.TUESDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.WEDNESDAY_CUSTOM.ordinal()) {
                    viewCustom.updateDay(WeekView.DAYS.WEDNESDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.THURSDAY_CUSTOM.ordinal()) {
                    viewCustom.updateDay(WeekView.DAYS.THURSDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.FRIDAY_CUSTOM.ordinal()) {
                    viewCustom.updateDay(WeekView.DAYS.FRIDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.SATURDAY_CUSTOM.ordinal()) {
                    viewCustom.updateDay(WeekView.DAYS.SATURDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.SUNDAY_CUSTOM.ordinal()) {
                    viewCustom.updateDay(WeekView.DAYS.SUNDAY, (float) msg.obj);
                }else if (msg.what == MSG_ID.MONDAY_CURRENT_YEAR.ordinal()) {
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
                } else if (msg.what == MSG_ID.JANUARY_CURRENT_YEAR.ordinal()) {
                    viewYearCur.updateMonth(MonthView.MONTHS.JANUARY, (float) msg.obj);
                } else if (msg.what == MSG_ID.FEBRUARY_CURRENT_YEAR.ordinal()) {
                    viewYearCur.updateMonth(MonthView.MONTHS.FEBRUARY, (float) msg.obj);
                } else if (msg.what == MSG_ID.MARCH_CURRENT_YEAR.ordinal()) {
                    viewYearCur.updateMonth(MonthView.MONTHS.MARCH, (float) msg.obj);
                } else if (msg.what == MSG_ID.APRIL_CURRENT_YEAR.ordinal()) {
                    viewYearCur.updateMonth(MonthView.MONTHS.APRIL, (float) msg.obj);
                } else if (msg.what == MSG_ID.MAY_CURRENT_YEAR.ordinal()) {
                    viewYearCur.updateMonth(MonthView.MONTHS.MAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.JUNE_CURRENT_YEAR.ordinal()) {
                    viewYearCur.updateMonth(MonthView.MONTHS.JUNE, (float) msg.obj);
                } else if (msg.what == MSG_ID.JULY_CURRENT_YEAR.ordinal()) {
                    viewYearCur.updateMonth(MonthView.MONTHS.JULY, (float) msg.obj);
                } else if (msg.what == MSG_ID.AUGUST_CURRENT_YEAR.ordinal()) {
                    viewYearCur.updateMonth(MonthView.MONTHS.AUGUST, ( float)msg.obj);
                } else if (msg.what == MSG_ID.SEPTEMBER_CURRENT_YEAR.ordinal()) {
                    viewYearCur.updateMonth(MonthView.MONTHS.SEPTEMBER, (float) msg.obj);
                } else if (msg.what == MSG_ID.OCTOBER_CURRENT_YEAR.ordinal()) {
                    viewYearCur.updateMonth(MonthView.MONTHS.OCTOBER, (float) msg.obj);
                } else if (msg.what == MSG_ID.NOVEMBER_CURRENT_YEAR.ordinal()) {
                    viewYearCur.updateMonth(MonthView.MONTHS.NOVEMBER, (float) msg.obj);
                } else if (msg.what == MSG_ID.DECEMBER_CURRENT_YEAR.ordinal()) {
                    viewYearCur.updateMonth(MonthView.MONTHS.DECEMBER, (float) msg.obj);
                } else if (msg.what == MSG_ID.JANUARY_ALL_TIME.ordinal()) {
                    viewYearAll.updateMonth(MonthView.MONTHS.JANUARY, (float) msg.obj);
                } else if (msg.what == MSG_ID.FEBRUARY_ALL_TIME.ordinal()) {
                    viewYearAll.updateMonth(MonthView.MONTHS.FEBRUARY, (float) msg.obj);
                } else if (msg.what == MSG_ID.MARCH_ALL_TIME.ordinal()) {
                    viewYearAll.updateMonth(MonthView.MONTHS.MARCH, (float) msg.obj);
                } else if (msg.what == MSG_ID.APRIL_ALL_TIME.ordinal()) {
                    viewYearAll.updateMonth(MonthView.MONTHS.APRIL, (float) msg.obj);
                } else if (msg.what == MSG_ID.MAY_ALL_TIME.ordinal()) {
                    viewYearAll.updateMonth(MonthView.MONTHS.MAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.JUNE_ALL_TIME.ordinal()) {
                    viewYearAll.updateMonth(MonthView.MONTHS.JUNE, (float) msg.obj);
                } else if (msg.what == MSG_ID.JULY_ALL_TIME.ordinal()) {
                    viewYearAll.updateMonth(MonthView.MONTHS.JULY, (float) msg.obj);
                } else if (msg.what == MSG_ID.AUGUST_ALL_TIME.ordinal()) {
                    viewYearAll.updateMonth(MonthView.MONTHS.AUGUST, (float) msg.obj);
                } else if (msg.what == MSG_ID.SEPTEMBER_ALL_TIME.ordinal()) {
                    viewYearAll.updateMonth(MonthView.MONTHS.SEPTEMBER, (float) msg.obj);
                } else if (msg.what == MSG_ID.OCTOBER_ALL_TIME.ordinal()) {
                    viewYearAll.updateMonth(MonthView.MONTHS.OCTOBER, (float) msg.obj);
                } else if (msg.what == MSG_ID.NOVEMBER_ALL_TIME.ordinal()) {
                    viewYearAll.updateMonth(MonthView.MONTHS.NOVEMBER, (float) msg.obj);
                } else if (msg.what == MSG_ID.DECEMBER_ALL_TIME.ordinal()) {
                    viewYearAll.updateMonth(MonthView.MONTHS.DECEMBER, (float) msg.obj);
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDataSet();
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
                for (int i = 0; i < 12; i++) {
                    int curMonth = Calendar.JANUARY + i;
                    float ret = db.dayDao().getAverageRatingPerMonthAndYear(curMonth, calendar.get(java.util.Calendar.YEAR));
                    Message msg_rating = Message.obtain();
                    msg_rating.what = (MSG_ID.JANUARY_CURRENT_YEAR.ordinal() + i);
                    msg_rating.obj = ret;
                    handler.sendMessage(msg_rating);
                }
                for (int i = 0; i < 12; i++) {
                    int curMonth = Calendar.JANUARY + i;
                    float ret = db.dayDao().getAverageRatingPerMonth(curMonth);
                    Message msg_rating = Message.obtain();
                    msg_rating.what = (MSG_ID.JANUARY_ALL_TIME.ordinal() + i);
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
