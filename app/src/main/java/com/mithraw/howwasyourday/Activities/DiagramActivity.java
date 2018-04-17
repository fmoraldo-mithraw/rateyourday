package com.mithraw.howwasyourday.Activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mithraw.howwasyourday.App;
import com.mithraw.howwasyourday.Dialogs.TipsDialog;
import com.mithraw.howwasyourday.Helpers.MonthViewHelper;
import com.mithraw.howwasyourday.Helpers.SharingHelper;
import com.mithraw.howwasyourday.Helpers.WeekViewHelper;
import com.mithraw.howwasyourday.R;
import com.mithraw.howwasyourday.databases.DaysDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/*
Provide diagrams to the user with the help of weekview and monthview
 */
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
    SharingHelper mSharingHelperCustom ;
    SharingHelper mSharingHelperWeekCurYear;
    SharingHelper mSharingHelperWeekAllYear;
    SharingHelper mSharingHelperMonthCurYear;
    SharingHelper mSharingHelperMonthAllYear;
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

        //Init layouts
        View myView = findViewById(R.id.custom_week);
        ((TextView)myView.findViewById(R.id.title)).setText(R.string.custom_diagram_title);
        TextView title2 = findViewById(R.id.title2);
        CardView cvCustom = findViewById(R.id.cardViewCustom);
        ImageButton ibCustom= myView.findViewById(R.id.share_diagram);
        mSharingHelperCustom =  new SharingHelper(cvCustom,this,
                ibCustom, title2);
        mSharingHelperCustom.attachToImageButton(ibCustom);

        View viewWeekCurYear = findViewById(R.id.current_year_week);
        WeekViewHelper viewCur = new WeekViewHelper(viewWeekCurYear);
        viewCur.setTitle(res.getString(R.string.current_diagram_title));
        CardView cvWeekCurYear = findViewById(R.id.cardViewWeekCurYear);
        ImageButton ibWeekCurYear= viewWeekCurYear.findViewById(R.id.share_diagram);
        mSharingHelperWeekCurYear =  new SharingHelper(cvWeekCurYear,this,
                ibWeekCurYear);
        mSharingHelperWeekCurYear.attachToImageButton(ibWeekCurYear);

        View viewWeekAllYear = findViewById(R.id.all_time_week);
        WeekViewHelper viewAll = new WeekViewHelper(viewWeekAllYear);
        viewAll.setTitle(res.getString(R.string.all_years_diagram_title));
        CardView cvWeekAllYear = findViewById(R.id.cardViewWeekAllYear);
        ImageButton ibWeekAllYear= viewWeekAllYear.findViewById(R.id.share_diagram);
        mSharingHelperWeekAllYear =  new SharingHelper(cvWeekAllYear,this,
                ibWeekAllYear);
        mSharingHelperWeekAllYear.attachToImageButton(ibWeekAllYear);

        View viewMonthCurrentYear = findViewById(R.id.current_months);
        MonthViewHelper viewYearCur = new MonthViewHelper(viewMonthCurrentYear);
        viewYearCur.setTitle(res.getString(R.string.current_months_diagram_title));
        CardView cvMonthCurYear = findViewById(R.id.cardViewMonthCurYear);
        ImageButton ibMonthCurYear= viewMonthCurrentYear.findViewById(R.id.share_diagram);
        mSharingHelperMonthCurYear =  new SharingHelper(cvMonthCurYear,this,
                ibMonthCurYear);
        mSharingHelperMonthCurYear.attachToImageButton(ibMonthCurYear);

        View viewMonthAllYear = findViewById(R.id.all_time_months);
        MonthViewHelper viewYearAll = new MonthViewHelper(viewMonthAllYear);
        viewYearAll.setTitle(res.getString(R.string.all_years_months_diagram_title));
        CardView cvMonthAllYear = findViewById(R.id.cardViewMonthAllYear);
        ImageButton ibMonthAllYear= viewMonthAllYear.findViewById(R.id.share_diagram);
        mSharingHelperMonthAllYear =  new SharingHelper(cvMonthAllYear,this,
                ibMonthAllYear);
        mSharingHelperMonthAllYear.attachToImageButton(ibMonthAllYear);

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
                WeekViewHelper viewCur = new WeekViewHelper((View) findViewById(R.id.current_year_week));
                WeekViewHelper viewCustom = new WeekViewHelper((View) findViewById(R.id.custom_week));
                WeekViewHelper viewAll = new WeekViewHelper((View) findViewById(R.id.all_time_week));
                MonthViewHelper viewYearCur = new MonthViewHelper((View) findViewById(R.id.current_months));
                MonthViewHelper viewYearAll = new MonthViewHelper((View) findViewById(R.id.all_time_months));

                if (msg.what == MSG_ID.MONDAY_CUSTOM.ordinal()) {
                    viewCustom.updateDay(WeekViewHelper.DAYS.MONDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.TUESDAY_CUSTOM.ordinal()) {
                    viewCustom.updateDay(WeekViewHelper.DAYS.TUESDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.WEDNESDAY_CUSTOM.ordinal()) {
                    viewCustom.updateDay(WeekViewHelper.DAYS.WEDNESDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.THURSDAY_CUSTOM.ordinal()) {
                    viewCustom.updateDay(WeekViewHelper.DAYS.THURSDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.FRIDAY_CUSTOM.ordinal()) {
                    viewCustom.updateDay(WeekViewHelper.DAYS.FRIDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.SATURDAY_CUSTOM.ordinal()) {
                    viewCustom.updateDay(WeekViewHelper.DAYS.SATURDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.SUNDAY_CUSTOM.ordinal()) {
                    viewCustom.updateDay(WeekViewHelper.DAYS.SUNDAY, (float) msg.obj);
                }else if (msg.what == MSG_ID.MONDAY_CURRENT_YEAR.ordinal()) {
                    viewCur.updateDay(WeekViewHelper.DAYS.MONDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.TUESDAY_CURRENT_YEAR.ordinal()) {
                    viewCur.updateDay(WeekViewHelper.DAYS.TUESDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.WEDNESDAY_CURRENT_YEAR.ordinal()) {
                    viewCur.updateDay(WeekViewHelper.DAYS.WEDNESDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.THURSDAY_CURRENT_YEAR.ordinal()) {
                    viewCur.updateDay(WeekViewHelper.DAYS.THURSDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.FRIDAY_CURRENT_YEAR.ordinal()) {
                    viewCur.updateDay(WeekViewHelper.DAYS.FRIDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.SATURDAY_CURRENT_YEAR.ordinal()) {
                    viewCur.updateDay(WeekViewHelper.DAYS.SATURDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.SUNDAY_CURRENT_YEAR.ordinal()) {
                    viewCur.updateDay(WeekViewHelper.DAYS.SUNDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.MONDAY_ALL_YEARS.ordinal()) {
                    viewAll.updateDay(WeekViewHelper.DAYS.MONDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.TUESDAY_ALL_YEARS.ordinal()) {
                    viewAll.updateDay(WeekViewHelper.DAYS.TUESDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.WEDNESDAY_ALL_YEARS.ordinal()) {
                    viewAll.updateDay(WeekViewHelper.DAYS.WEDNESDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.THURSDAY_ALL_YEARS.ordinal()) {
                    viewAll.updateDay(WeekViewHelper.DAYS.THURSDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.FRIDAY_ALL_YEARS.ordinal()) {
                    viewAll.updateDay(WeekViewHelper.DAYS.FRIDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.SATURDAY_ALL_YEARS.ordinal()) {
                    viewAll.updateDay(WeekViewHelper.DAYS.SATURDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.SUNDAY_ALL_YEARS.ordinal()) {
                    viewAll.updateDay(WeekViewHelper.DAYS.SUNDAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.JANUARY_CURRENT_YEAR.ordinal()) {
                    viewYearCur.updateMonth(MonthViewHelper.MONTHS.JANUARY, (float) msg.obj);
                } else if (msg.what == MSG_ID.FEBRUARY_CURRENT_YEAR.ordinal()) {
                    viewYearCur.updateMonth(MonthViewHelper.MONTHS.FEBRUARY, (float) msg.obj);
                } else if (msg.what == MSG_ID.MARCH_CURRENT_YEAR.ordinal()) {
                    viewYearCur.updateMonth(MonthViewHelper.MONTHS.MARCH, (float) msg.obj);
                } else if (msg.what == MSG_ID.APRIL_CURRENT_YEAR.ordinal()) {
                    viewYearCur.updateMonth(MonthViewHelper.MONTHS.APRIL, (float) msg.obj);
                } else if (msg.what == MSG_ID.MAY_CURRENT_YEAR.ordinal()) {
                    viewYearCur.updateMonth(MonthViewHelper.MONTHS.MAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.JUNE_CURRENT_YEAR.ordinal()) {
                    viewYearCur.updateMonth(MonthViewHelper.MONTHS.JUNE, (float) msg.obj);
                } else if (msg.what == MSG_ID.JULY_CURRENT_YEAR.ordinal()) {
                    viewYearCur.updateMonth(MonthViewHelper.MONTHS.JULY, (float) msg.obj);
                } else if (msg.what == MSG_ID.AUGUST_CURRENT_YEAR.ordinal()) {
                    viewYearCur.updateMonth(MonthViewHelper.MONTHS.AUGUST, (float) msg.obj);
                } else if (msg.what == MSG_ID.SEPTEMBER_CURRENT_YEAR.ordinal()) {
                    viewYearCur.updateMonth(MonthViewHelper.MONTHS.SEPTEMBER, (float) msg.obj);
                } else if (msg.what == MSG_ID.OCTOBER_CURRENT_YEAR.ordinal()) {
                    viewYearCur.updateMonth(MonthViewHelper.MONTHS.OCTOBER, (float) msg.obj);
                } else if (msg.what == MSG_ID.NOVEMBER_CURRENT_YEAR.ordinal()) {
                    viewYearCur.updateMonth(MonthViewHelper.MONTHS.NOVEMBER, (float) msg.obj);
                } else if (msg.what == MSG_ID.DECEMBER_CURRENT_YEAR.ordinal()) {
                    viewYearCur.updateMonth(MonthViewHelper.MONTHS.DECEMBER, (float) msg.obj);
                } else if (msg.what == MSG_ID.JANUARY_ALL_TIME.ordinal()) {
                    viewYearAll.updateMonth(MonthViewHelper.MONTHS.JANUARY, (float) msg.obj);
                } else if (msg.what == MSG_ID.FEBRUARY_ALL_TIME.ordinal()) {
                    viewYearAll.updateMonth(MonthViewHelper.MONTHS.FEBRUARY, (float) msg.obj);
                } else if (msg.what == MSG_ID.MARCH_ALL_TIME.ordinal()) {
                    viewYearAll.updateMonth(MonthViewHelper.MONTHS.MARCH, (float) msg.obj);
                } else if (msg.what == MSG_ID.APRIL_ALL_TIME.ordinal()) {
                    viewYearAll.updateMonth(MonthViewHelper.MONTHS.APRIL, (float) msg.obj);
                } else if (msg.what == MSG_ID.MAY_ALL_TIME.ordinal()) {
                    viewYearAll.updateMonth(MonthViewHelper.MONTHS.MAY, (float) msg.obj);
                } else if (msg.what == MSG_ID.JUNE_ALL_TIME.ordinal()) {
                    viewYearAll.updateMonth(MonthViewHelper.MONTHS.JUNE, (float) msg.obj);
                } else if (msg.what == MSG_ID.JULY_ALL_TIME.ordinal()) {
                    viewYearAll.updateMonth(MonthViewHelper.MONTHS.JULY, (float) msg.obj);
                } else if (msg.what == MSG_ID.AUGUST_ALL_TIME.ordinal()) {
                    viewYearAll.updateMonth(MonthViewHelper.MONTHS.AUGUST, (float) msg.obj);
                } else if (msg.what == MSG_ID.SEPTEMBER_ALL_TIME.ordinal()) {
                    viewYearAll.updateMonth(MonthViewHelper.MONTHS.SEPTEMBER, (float) msg.obj);
                } else if (msg.what == MSG_ID.OCTOBER_ALL_TIME.ordinal()) {
                    viewYearAll.updateMonth(MonthViewHelper.MONTHS.OCTOBER, (float) msg.obj);
                } else if (msg.what == MSG_ID.NOVEMBER_ALL_TIME.ordinal()) {
                    viewYearAll.updateMonth(MonthViewHelper.MONTHS.NOVEMBER, (float) msg.obj);
                } else if (msg.what == MSG_ID.DECEMBER_ALL_TIME.ordinal()) {
                    viewYearAll.updateMonth(MonthViewHelper.MONTHS.DECEMBER, (float) msg.obj);
                }
            }
        };

        //Display tips
        String preferenceName = "tip_diagrams_showed";
        if ((PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean(preferenceName, false) == false)&&
                (PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean("show_tips", true) == true)){
            ArrayList<Integer> iList = new ArrayList<Integer>();
            iList.add(R.layout.tips_fragment_diagrams_custom);
            iList.add(R.layout.tips_fragment_diagrams_weeks);
            iList.add(R.layout.tips_fragment_diagrams_months);
            Bundle bundl = new Bundle();
            bundl.putIntegerArrayList("listView", iList);
            bundl.putString("preference", preferenceName);
            bundl.putInt("title", R.string.tips_diagrams_title);
            DialogFragment newFragment = new TipsDialog();
            newFragment.setArguments(bundl);
            newFragment.show(getSupportFragmentManager(), preferenceName);
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
