package com.mithraw.howwasyourday.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mithraw.howwasyourday.App;
import com.mithraw.howwasyourday.BuildConfig;
import com.mithraw.howwasyourday.Dialogs.FirstUseDialog;
import com.mithraw.howwasyourday.Dialogs.TipsDialog;
import com.mithraw.howwasyourday.Helpers.BitmapHelper;
import com.mithraw.howwasyourday.Helpers.GoogleSignInHelper;
import com.mithraw.howwasyourday.Helpers.NotificationHelper;
import com.mithraw.howwasyourday.Helpers.Statistics.StatisticsAdds;
import com.mithraw.howwasyourday.Helpers.SyncLauncher;
import com.mithraw.howwasyourday.Helpers.ThreadSyncDatas;
import com.mithraw.howwasyourday.R;
import com.mithraw.howwasyourday.Tools.Coordinate;
import com.mithraw.howwasyourday.Tools.MyInt;
import com.mithraw.howwasyourday.databases.Day;
import com.mithraw.howwasyourday.databases.DaysDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.MarkStyle;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.vo.DateData;

/*
Main Activity, show a calendar view and a card with the day selected (default today)
Manage the menu
 */


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private enum MSG_ID {MSG_RATING, MSG_TITLE, MSG_LOG, MSG_EMPTY, MSG_SENT, MSG_UPDATE, MSG_LOCATION, MSG_LIST_RATED, MSG_INTERRESTING}

    public enum ACTIVITY_ID {ACTIVITY_RATE_A_DAY, ACTIVITY_SETTINGS, ACTIVITY_DIAGRAMS, ACTIVITY_LOGS, ACTIVITY_STATS}

    private static Context mContext;
    private static Activity mActivity;
    protected DaysDatabase db;
    protected static Handler handler;
    protected static final java.util.Calendar m_calendar = java.util.Calendar.getInstance();
    private boolean dateChangedByUser = false;
    private Day mDay;
    private static CardView mCardView;
    Coordinate mLastCoordinate = new Coordinate();
    DateData mLastDate;
    MyInt[] arrayInt = {new MyInt(0)};
    List<DateData> listDaysRated;
    public static Context getContext() {
        return mContext;
    }

    public static Activity getmActivity() {
        return mActivity;
    }

    private void setLogText(SpannableStringBuilder value) {
        TextView text = findViewById(R.id.logText);
        ScrollView scrollViewMain = findViewById(R.id.scrollViewMain);
        if ((scrollViewMain != null) && (text != null)) {
            if (value.equals("")) {
                text.setText("");
                scrollViewMain.setVisibility(View.GONE);
            } else {
                scrollViewMain.setVisibility(View.VISIBLE);
                text.setText(value);
            }
        }
    }
    private void clearLogText() {
        TextView text = findViewById(R.id.logText);
        text.setText("");
        ScrollView scrollViewMain = findViewById(R.id.scrollViewMain);
    }
    private void clearTitleText() {
        TextView text = (TextView) findViewById(R.id.titleText);
        text.setText("");
    }
    private void setTitleText(String value) {
        TextView text = (TextView) findViewById(R.id.titleText);
        if (text != null) {
            if (value.equals("")) {
                text.setText("");
                text.setVisibility(View.GONE);
            } else {
                text.setVisibility(View.VISIBLE);
                text.setText(value);
            }
        }
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.activity_main);
        mContext = this;
        mCardView = this.findViewById(R.id.cardViewLittle);
        listDaysRated = new ArrayList<>();
        //Display the first use Screen
        if (!(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("first_use_screen_showed", false))) {
            DialogFragment newFragment = new FirstUseDialog();
            newFragment.show(getSupportFragmentManager(), "first_use_fragment");
        }

        //Display tips
        String preferenceName = "tip_main_showed";
        if ((!(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(preferenceName, false)))&&
                (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("show_tips", true))){
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


        //Connect to GoogleSignIn
        boolean firstTimeScreenShowed = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("first_use_screen_showed", false);
        String timeSync = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("sync_frequency","180");
        if ((!timeSync.equals("0")) && (firstTimeScreenShowed == true)) { // If the first use screen is currently shown we don't do the GoogleSignIn, the first use screen will do it
            if (System.currentTimeMillis() > PreferenceManager.getDefaultSharedPreferences(App.getContext()).getLong("time_next_sync", 0))
                GoogleSignInHelper.getInstance(this).doSignIn(new SyncLauncher());
            else
                ThreadSyncDatas.reSchedule(PreferenceManager.getDefaultSharedPreferences(App.getContext()).getLong("time_next_sync", 0),false);
        }

        // Setup the notifications
        NotificationHelper.buildChannel();
        if (firstTimeScreenShowed == true) // If the first use screen is currently shown we don't setup the notifications, the first use screen will do it
            NotificationHelper.setupNotificationStatus();

        //Setup the database
        db = DaysDatabase.getInstance(getApplicationContext());
        //TODO WARNING REMOVE THAT ON PRODUTION
        //fillDbWithJunk();

        //Setup the ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        //Setup the navigation view (whatever it is)
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Fill the version number
        View headerView = navigationView.getHeaderView(0);
        TextView version = headerView.findViewById(R.id.textViewVersion);
        version.setText(BuildConfig.VERSION_NAME);

        //Setup the thread message handler
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ImageView loc = findViewById(R.id.location_view);
                RatingBar rab = findViewById(R.id.ratingBar);
                Button removeButton = findViewById(R.id.main_button_remove);
                Button editButton = findViewById(R.id.main_button_edit);
                LinearLayout nothingLayout = findViewById(R.id.nothing_layout);
                LinearLayout rateLayout = findViewById(R.id.rate_layout);
                if (msg.what == MSG_ID.MSG_RATING.ordinal()) {
                    if ((rab != null) && (msg.obj != null))
                        rab.setRating((Integer) (msg.obj));
                } else if (msg.what == MSG_ID.MSG_LOG.ordinal()) {
                    if (msg.obj != null) {
                        setLogText(BitmapHelper.parseStringWithBitmaps(m_calendar, (String) (msg.obj), arrayInt, true));
                        ((TextView)findViewById(R.id.logText)).setMovementMethod(LinkMovementMethod.getInstance());
                    }else
                        clearLogText();
                } else if (msg.what == MSG_ID.MSG_TITLE.ordinal()) {
                    if (msg.obj != null)
                        setTitleText((String) (msg.obj));
                    else
                        clearTitleText();
                } else if (msg.what == MSG_ID.MSG_LOCATION.ordinal()) {
                    if ((msg.obj == null) || (((Coordinate) msg.obj).getLatitude() == 0) && (((Coordinate) msg.obj).getLongitude() == 0)) {
                        mLastCoordinate = new Coordinate();
                        loc.setVisibility(View.GONE);
                    }else{
                        mLastCoordinate = (Coordinate) msg.obj;
                        loc.setVisibility(View.VISIBLE);
                    }
                }
                if(msg.what == MSG_ID.MSG_INTERRESTING.ordinal()){
                    String preferenceName = "tips_snack_show";
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(System.currentTimeMillis());
                    int curMonth = cal.get(Calendar.MONTH);
                    int curYear = cal.get(Calendar.YEAR);
                    PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean(preferenceName, false).apply();
                    PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putInt(preferenceName + "_month", curMonth).apply();
                    PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putInt(preferenceName + "_year", curYear).apply();
                    Snackbar snackbar = Snackbar.make(getCurrentFocus(), R.string.interesting_things, 5000);
                    snackbar.setAction(R.string.interesting_things_button, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            launchActivityFunnyStats();
                        }
                    });
                    snackbar.show();

                }
                if (msg.what == MSG_ID.MSG_EMPTY.ordinal()) {
                    if ((rab != null) &&
                            (removeButton != null) &&
                            (editButton != null) &&
                            (nothingLayout != null) &&
                            (rateLayout != null) &&
                            (loc != null)) {
                        loc.setVisibility(View.GONE);
                        rab.setRating(0);
                        setLogText(new SpannableStringBuilder(""));
                        setTitleText("");
                        removeButton.setEnabled(false);
                        nothingLayout.setVisibility(View.VISIBLE);
                        rateLayout.setVisibility(View.GONE);
                        editButton.setText(R.string.app_name);
                    }
                }
                if (msg.what == MSG_ID.MSG_SENT.ordinal()) {
                    if ((removeButton != null) &&
                            (editButton != null) &&
                            (nothingLayout != null) &&
                            (rateLayout != null)) {
                        removeButton.setEnabled(true);
                        nothingLayout.setVisibility(View.GONE);
                        rateLayout.setVisibility(View.VISIBLE);
                        editButton.setText(R.string.edit_button_text);
                    }
                }
                if (msg.what == MSG_ID.MSG_LIST_RATED.ordinal()) {
                    if (msg.obj != null) {
                        List<Day> days = (List<Day>) msg.obj;
                        listDaysRated.clear();
                        for (Day d : days) {
                            listDaysRated.add(new DateData(d.getYear(), d.getMonth() + 1, d.getDay()));
                        }
                        updateDayRated();
                    }
                }
            }
        };
        //Setup the expand button
        ImageButton expandButton = findViewById(R.id.expand_button);
        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((mDay != null) && (((RatingBar)findViewById(R.id.ratingBar)).getRating() != 0))
                    expandDay(v);
                else
                    Toast.makeText(getBaseContext(), R.string.cant_expand, Toast.LENGTH_SHORT).show();
            }
        });
        //Setup the Edit button
        Button editButton = findViewById(R.id.main_button_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivityRateADay();
            }
        });
        final Button removeButton = findViewById(R.id.main_button_remove);
        removeButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                alertDialogBuilder.setMessage(R.string.log_removed)
                        .setTitle(R.string.log_removed_title);
                alertDialogBuilder.setPositiveButton(R.string.log_removed_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new Thread() {
                            @Override
                            public void run() {
                                DaysDatabase db = DaysDatabase.getInstance(App.getApplication().getApplicationContext());
                                if (mDay != null)
                                    db.dayDao().remove(mDay.setRemoved(true));
                                BitmapHelper.removeImageDir(m_calendar);
                                handler.sendEmptyMessage(MSG_ID.MSG_EMPTY.ordinal());
                            }
                        }.start();
                        removeRateDay(m_calendar);
                    }
                });
                alertDialogBuilder.setNegativeButton(R.string.log_removed_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                AlertDialog dialog = alertDialogBuilder.create();
                dialog.show();


            }
        });

        //Setup the main controls
        //  The Rating Bar
        RatingBar rab = (RatingBar) findViewById(R.id.ratingBar);
        rab.setRating(0);
        rab.setMax(5);

        //  The the date controler
        MCalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(View view, DateData date) {
                m_calendar.set(java.util.Calendar.YEAR, date.getYear());
                m_calendar.set(java.util.Calendar.MONTH, date.getMonth() - 1);
                m_calendar.set(java.util.Calendar.DAY_OF_MONTH, date.getDay());
                //if the date selected is not the date of the day we consider the date has been changed
                Date d = new Date(System.currentTimeMillis());
                if ((d.getDate() == m_calendar.get(java.util.Calendar.DAY_OF_MONTH)) &&
                        (d.getMonth() == m_calendar.get(java.util.Calendar.MONTH)) &&
                        ((d.getYear() + 1900) == m_calendar.get(java.util.Calendar.YEAR))) {
                    dateChangedByUser = false;
                } else {
                    dateChangedByUser = true;
                }
                selectDate(date);
                updateLabel();
            }
        });
        calendarView.travelTo(new DateData(m_calendar.get(Calendar.YEAR), m_calendar.get(Calendar.MONTH) + 1, m_calendar.get(Calendar.DAY_OF_MONTH)));
        calendarView.markDate(new DateData(m_calendar.get(Calendar.YEAR), m_calendar.get(Calendar.MONTH) + 1, m_calendar.get(Calendar.DAY_OF_MONTH))
                .setMarkStyle(new MarkStyle(MarkStyle.BACKGROUND, getResources().getColor(R.color.colorPrimary))));
        selectDate(new DateData(m_calendar.get(Calendar.YEAR), m_calendar.get(Calendar.MONTH) + 1, m_calendar.get(Calendar.DAY_OF_MONTH)));

        updateDateText();
        checkIfSomeThingInterresting();
    }

    private void updateDayRated() {
        MCalendarView calendarView = findViewById(R.id.calendarView);
        for (DateData d : listDaysRated) {
            calendarView.unMarkDate(d);
            calendarView.markDate(d.setMarkStyle(new MarkStyle(MarkStyle.DOT, getResources().getColor(R.color.colorAccent))));
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        DateData daySelected = new DateData(m_calendar.get(Calendar.YEAR), m_calendar.get(Calendar.MONTH) + 1, m_calendar.get(Calendar.DAY_OF_MONTH));
        DateData today = new DateData(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
        calendarView.unMarkDate(today);
        calendarView.markDate(today.setMarkStyle(new MarkStyle(MarkStyle.BACKGROUND, getResources().getColor(R.color.colorAccent))));
        calendarView.unMarkDate(daySelected);
        calendarView.markDate(daySelected.setMarkStyle(new MarkStyle(MarkStyle.BACKGROUND, getResources().getColor(R.color.colorPrimary))));
    }

    private void removeRateDay(Calendar cal) {
        DateData date = new DateData(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
        MCalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.unMarkDate(date);
        calendarView.markDate(date.setMarkStyle(new MarkStyle(MarkStyle.BACKGROUND, getResources().getColor(R.color.colorPrimary))));
        listDaysRated.remove(date);
        updateDayRated();
    }

    private void selectDate(DateData date) {
        MCalendarView calendarView = findViewById(R.id.calendarView);
        if (mLastDate != null) {
            calendarView.unMarkDate(mLastDate);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        DateData today = new DateData(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
        if(!date.equals(today)){
            calendarView.unMarkDate(today);
            calendarView.markDate(today.setMarkStyle(new MarkStyle(MarkStyle.BACKGROUND, getResources().getColor(R.color.colorAccent))));
        }
        mLastDate = date;
    }

    private void checkIfSomeThingInterresting() {
        String preferenceName = "tips_snack_show";
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        int curMonth = cal.get(Calendar.MONTH);
        int curYear = cal.get(Calendar.YEAR);
        boolean showSnack = PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean(preferenceName, true);
        int showCardMonth = PreferenceManager.getDefaultSharedPreferences(App.getContext()).getInt(preferenceName + "_month", 0);
        int showCardYear = PreferenceManager.getDefaultSharedPreferences(App.getContext()).getInt(preferenceName + "_year", 0);
        if ((showSnack == false) && (((curMonth > showCardMonth) && (curYear >= showCardYear)) || (curYear > showCardYear))) {
            showSnack = true;
            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean(preferenceName, showSnack).apply();
        }
        if(showSnack) {

            new Thread() {
                @Override
                public void run() {
                    Calendar calendar = Calendar.getInstance();
                    StatisticsAdds addsCurMonth = new StatisticsAdds();
                    addsCurMonth.setNumDayRated1(db.dayDao().getNumberOfDayByRateByMonthAndYear(1, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
                            .setNumDayRated2(db.dayDao().getNumberOfDayByRateByMonthAndYear(2, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
                            .setNumDayRated3(db.dayDao().getNumberOfDayByRateByMonthAndYear(3, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
                            .setNumDayRated4(db.dayDao().getNumberOfDayByRateByMonthAndYear(4, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
                            .setNumDayRated5(db.dayDao().getNumberOfDayByRateByMonthAndYear(5, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
                            .setAverageDay(db.dayDao().getAverageDayByMonthAndYear(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
                            .setNumberOfRatedDays(db.dayDao().getNumberOfDaysByMonthAndYear(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
                            .setAvgMonday(db.dayDao().getAverageRatingPerDayOfTheWeekByMonthAndYear(Calendar.MONDAY, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
                            .setAvgTuesday(db.dayDao().getAverageRatingPerDayOfTheWeekByMonthAndYear(Calendar.TUESDAY, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
                            .setAvgWednesday(db.dayDao().getAverageRatingPerDayOfTheWeekByMonthAndYear(Calendar.WEDNESDAY, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
                            .setAvgThursday(db.dayDao().getAverageRatingPerDayOfTheWeekByMonthAndYear(Calendar.THURSDAY, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
                            .setAvgFriday(db.dayDao().getAverageRatingPerDayOfTheWeekByMonthAndYear(Calendar.FRIDAY, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
                            .setAvgSaturday(db.dayDao().getAverageRatingPerDayOfTheWeekByMonthAndYear(Calendar.SATURDAY, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
                            .setAvgSunday(db.dayDao().getAverageRatingPerDayOfTheWeekByMonthAndYear(Calendar.SUNDAY, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
                            .init();
                    calendar.add(Calendar.MONTH,-1);
                    StatisticsAdds addsLastMonth = new StatisticsAdds();
                    addsLastMonth.setNumDayRated1(db.dayDao().getNumberOfDayByRateByMonthAndYear(1, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
                            .setNumDayRated2(db.dayDao().getNumberOfDayByRateByMonthAndYear(2, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
                            .setNumDayRated3(db.dayDao().getNumberOfDayByRateByMonthAndYear(3, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
                            .setNumDayRated4(db.dayDao().getNumberOfDayByRateByMonthAndYear(4, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
                            .setNumDayRated5(db.dayDao().getNumberOfDayByRateByMonthAndYear(5, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
                            .setAverageDay(db.dayDao().getAverageDayByMonthAndYear(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
                            .setNumberOfRatedDays(db.dayDao().getNumberOfDaysByMonthAndYear(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
                            .setAvgMonday(db.dayDao().getAverageRatingPerDayOfTheWeekByMonthAndYear(Calendar.MONDAY, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
                            .setAvgTuesday(db.dayDao().getAverageRatingPerDayOfTheWeekByMonthAndYear(Calendar.TUESDAY, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
                            .setAvgWednesday(db.dayDao().getAverageRatingPerDayOfTheWeekByMonthAndYear(Calendar.WEDNESDAY, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
                            .setAvgThursday(db.dayDao().getAverageRatingPerDayOfTheWeekByMonthAndYear(Calendar.THURSDAY, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
                            .setAvgFriday(db.dayDao().getAverageRatingPerDayOfTheWeekByMonthAndYear(Calendar.FRIDAY, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
                            .setAvgSaturday(db.dayDao().getAverageRatingPerDayOfTheWeekByMonthAndYear(Calendar.SATURDAY, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
                            .setAvgSunday(db.dayDao().getAverageRatingPerDayOfTheWeekByMonthAndYear(Calendar.SUNDAY, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
                            .init();
                    if ((!addsCurMonth.getBadAvgQuote().equals("")) ||
                            (!addsCurMonth.getExtremQuote().equals("")) ||
                            (!addsCurMonth.getBigGapQuote().equals("")) ||
                            (!addsLastMonth.getBadAvgQuote().equals("")) ||
                            (!addsLastMonth.getExtremQuote().equals("")) ||
                            (!addsLastMonth.getBigGapQuote().equals(""))){
                        handler.sendEmptyMessage(MSG_ID.MSG_INTERRESTING.ordinal());
                    }
                }
            }.start();
        }
    }

    private void expandDay(View v) {

        Intent intent = new Intent(this, ExpandedDayActivity.class);
        intent.putExtra(ExpandedDayActivity.EXTRA_PARAM_DATETIME, m_calendar.getTimeInMillis());
        intent.putExtra(ExpandedDayActivity.EXTRA_PARAM_DATE, (String)((TextView)this.findViewById(R.id.dateTextView)).getText().toString());
        intent.putExtra(ExpandedDayActivity.EXTRA_PARAM_TITLE, (String)((TextView)this.findViewById(R.id.titleText)).getText().toString());
        intent.putExtra(ExpandedDayActivity.EXTRA_PARAM_LOG, (String)((TextView)this.findViewById(R.id.logText)).getText().toString());
        intent.putExtra(ExpandedDayActivity.EXTRA_PARAM_LONGITUDE, mLastCoordinate.getLongitude());
        intent.putExtra(ExpandedDayActivity.EXTRA_PARAM_LATITUDE, mLastCoordinate.getLatitude());
        intent.putExtra(ExpandedDayActivity.EXTRA_PARAM_RATE, (float) ((RatingBar)this.findViewById(R.id.ratingBar)).getRating());
        CardView cv = this.findViewById(R.id.cardViewLittle);
        Pair<View, String> p = new Pair<View, String>(cv, ExpandedDayActivity.VIEW_NAME);
        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, p);

        // Now we can start the Activity, providing the activity options as a bundle
        ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
        // END_INCLUDE(start_activity)
    }

    private void updateDateText() {
        TextView dateText = findViewById(R.id.dateTextView);
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(App.getApplication());
        java.util.Date d = new java.util.Date(m_calendar.getTimeInMillis());
        dateText.setText(dateFormat.format(d));
    }

    private void fillDbWithJunk() {
        new Thread() {
            @Override
            public void run() {
                //Last year
                Calendar c = Calendar.getInstance();
                c.set(Calendar.DAY_OF_MONTH, 1);
                c.set(Calendar.MONTH, 1);
                c.set(Calendar.YEAR, m_calendar.get(java.util.Calendar.YEAR) - 1);
                for (int i = 0; i < 365; i++) {
                    c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + 1);
                    Random r = new Random();
                    int rate = r.nextInt(4) + 1;
                    Day d = new Day(c.get(Calendar.DAY_OF_WEEK), c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH), c.get(Calendar.YEAR), c.get(Calendar.WEEK_OF_YEAR), c.getTimeInMillis(), rate, "Balbalalala", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat", 0,0, false);
                    db.dayDao().insertDay(d);
                }
                //Current Year
                c.set(Calendar.DAY_OF_MONTH, 1);
                c.set(Calendar.MONTH, 1);
                c.set(Calendar.YEAR, m_calendar.get(java.util.Calendar.YEAR));
                for (int i = 0; i < 365 && (c.getTimeInMillis() < System.currentTimeMillis()); i++) {
                    c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + 1);
                    Random r = new Random();
                    int rate = r.nextInt(5 - 1) + 1;
                    Day d = new Day(c.get(Calendar.DAY_OF_WEEK), c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH), c.get(Calendar.YEAR), c.get(Calendar.WEEK_OF_YEAR), c.getTimeInMillis(), rate, "Balbalalala", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat", 0,0, false);
                    db.dayDao().insertDay(d);
                }
            }
        }.start();
    }

    private void updateLabel() {
        MCalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.travelTo(new DateData(m_calendar.get(Calendar.YEAR), m_calendar.get(Calendar.MONTH) + 1, m_calendar.get(Calendar.DAY_OF_MONTH)));
        updateDateText();
        //Fill the controls with the correct infos
        fillTheInformations();
    }

    private void launchActivityRateADay() {
        Intent rateADayIntent = new Intent(getApplicationContext(), RateADay.class);
        // Send the date informations to the next activity
        rateADayIntent.putExtra(RateADay.EXTRA_DATE_DAY, m_calendar.get(java.util.Calendar.DAY_OF_MONTH));
        rateADayIntent.putExtra(RateADay.EXTRA_DATE_MONTH, m_calendar.get(java.util.Calendar.MONTH));
        rateADayIntent.putExtra(RateADay.EXTRA_DATE_YEAR, m_calendar.get(java.util.Calendar.YEAR));
        startActivityForResult(rateADayIntent, ACTIVITY_ID.ACTIVITY_RATE_A_DAY.ordinal());
    }

    private void launchActivitySettings() {
        Intent settingIntent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivityForResult(settingIntent, ACTIVITY_ID.ACTIVITY_SETTINGS.ordinal());
    }

    private void launchActivityDiagrams() {
        Intent diagramIntent = new Intent(getApplicationContext(), DiagramActivity.class);
        startActivityForResult(diagramIntent, ACTIVITY_ID.ACTIVITY_DIAGRAMS.ordinal());
    }

    private void launchActivityLogs() {
        Intent diagramIntent = new Intent(getApplicationContext(), LogsActivity.class);
        startActivityForResult(diagramIntent, ACTIVITY_ID.ACTIVITY_LOGS.ordinal());
    }

    private void launchActivityFunnyStats() {
        Intent statsIntent = new Intent(getApplicationContext(), FunnyStatsActivity.class);
        startActivityForResult(statsIntent, ACTIVITY_ID.ACTIVITY_STATS.ordinal());
    }

    private void launchActivityDonation() {
        Intent donationIntent = new Intent(getApplicationContext(), DonationActivity.class);
        startActivity(donationIntent);
    }

    private void launchActivityAboutTheAuthor() {
        Intent aboutTheAutorIntent = new Intent(getApplicationContext(), AboutTheAuthorActivity.class);
        startActivity(aboutTheAutorIntent);
    }

    private void launchActivityAboutTheApp() {
        Intent aboutTheAppIntent = new Intent(getApplicationContext(), AboutTheAppActivity.class);
        startActivity(aboutTheAppIntent);
    }

    private void launchActivityImportExport() {
        Intent importExportIntent = new Intent(getApplicationContext(), ImportExportActivity.class);
        startActivity(importExportIntent);
    }

    private void launchActivityMap() {
        Intent mapIntent = new Intent(getApplicationContext(), MapActivity.class);
        startActivity(mapIntent);
    }

    private void launchActivityBestMemories() {
        Intent bestMemoriesIntent = new Intent(getApplicationContext(), BestMemoriesActivity.class);
        startActivity(bestMemoriesIntent);
    }
    private void launchActivitySearch() {
        Intent searchIntent = new Intent(getApplicationContext(), SearchActivity.class);
        startActivity(searchIntent);
    }


    public class ReconnectListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            new Thread() {
                @Override
                public void run() {
                    GoogleSignInHelper.getInstance(mActivity).connectToSignInAccount(new SyncLauncher());
                }
            }.start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == ACTIVITY_ID.ACTIVITY_RATE_A_DAY.ordinal()) {
            // Make sure the request was successful
            if (resultCode != 0) {
                Resources res = getResources();
                if (resultCode > 2)
                    Snackbar.make(getCurrentFocus(), res.getString(R.string.save_the_day_good), 2000).show();
                else
                    Snackbar.make(getCurrentFocus(), res.getString(R.string.save_the_day_bad), 2000).show();
            }
        } else if (requestCode == ACTIVITY_ID.ACTIVITY_SETTINGS.ordinal()) {
            //TODO Make something
        } else if(requestCode == GoogleSignInHelper.GOOGLE_SIGNIN_ACTIVITY_ID){
            Resources res = App.getApplication().getResources();
            if (resultCode == Activity.RESULT_OK) {
                // App is authorized, you can go back to sending the API request
                GoogleSignInHelper.getInstance(this).doSignIn(new SyncLauncher());
            } else {
                Snackbar.make(getCurrentFocus(), res.getString(R.string.issue_google_sign_in), 2000).setAction(R.string.reconnect_google_sign_in, new ReconnectListener()).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.go_to_today, menu);
        MenuItem item = menu.findItem(R.id.action_today);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                goToday();
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {
            launchActivitySettings();
        }
        if (id == R.id.nav_diagrams) {
            launchActivityDiagrams();
        }
        if (id == R.id.nav_entry_logs) {
            launchActivityLogs();
        }
        if (id == R.id.nav_funny_stats) {
            launchActivityFunnyStats();
        }
        if(id == R.id.nav_donate) {
            launchActivityDonation();
        }
        if(id == R.id.nav_about_the_author) {
            launchActivityAboutTheAuthor();
        }
        if(id == R.id.nav_about_the_app) {
            launchActivityAboutTheApp();
        }
        if(id == R.id.nav_import_export) {
            launchActivityImportExport();
        }
        if(id == R.id.nav_map) {
            launchActivityMap();
        }
        if(id == R.id.nav_best_memories) {
            launchActivityBestMemories();
        }
        if(id == R.id.nav_search) {
            launchActivitySearch();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        RatingBar rab = (RatingBar) findViewById(R.id.ratingBar);
        rab.setIsIndicator(true);
        Date d = new Date(System.currentTimeMillis());
        //If day has changed
        if ((!dateChangedByUser) && ((d.getDate() != m_calendar.get(java.util.Calendar.DAY_OF_MONTH)) ||
                (d.getMonth() != m_calendar.get(java.util.Calendar.MONTH)) ||
                ((d.getYear() + 1900) != m_calendar.get(java.util.Calendar.YEAR)))) {
            m_calendar.setTimeInMillis(System.currentTimeMillis());
        }
        MCalendarView calendarView = findViewById(R.id.calendarView);
        if (calendarView.getMarkedDates() != null)
            calendarView.getMarkedDates().removeAdd();
        calendarView.travelTo(new DateData(m_calendar.get(Calendar.YEAR), m_calendar.get(Calendar.MONTH) + 1, m_calendar.get(Calendar.DAY_OF_MONTH)));
        selectDate(new DateData(m_calendar.get(Calendar.YEAR), m_calendar.get(Calendar.MONTH) + 1, m_calendar.get(Calendar.DAY_OF_MONTH)));

        //Initialize the labels
        updateLabel();
    }

    protected void fillTheInformations() {
        new Thread() {
            @Override
            public void run() {
                if (db == null)
                    return;
                List<Day> days = db.dayDao().getAllByDate(m_calendar.get(java.util.Calendar.DAY_OF_MONTH), m_calendar.get(java.util.Calendar.MONTH), m_calendar.get(java.util.Calendar.YEAR));

                if (days.isEmpty()) {
                    mDay = null;
                    handler.sendEmptyMessage(MSG_ID.MSG_EMPTY.ordinal());
                } else {
                    mDay = days.get(0);
                    Message msg_rating = Message.obtain();
                    msg_rating.what = MSG_ID.MSG_RATING.ordinal();
                    msg_rating.obj = days.get(0).getRating();
                    handler.sendMessage(msg_rating);

                    Message msg_title = Message.obtain();
                    msg_title.what = MSG_ID.MSG_TITLE.ordinal();
                    msg_title.obj = days.get(0).getTitleText();
                    handler.sendMessage(msg_title);

                    Coordinate coordinate = new Coordinate();
                    coordinate.setLatitude(days.get(0).getLatitude());
                    coordinate.setLongitude(days.get(0).getLongitude());
                    Message msg_location = Message.obtain();
                    msg_location.what = MSG_ID.MSG_LOCATION.ordinal();
                    msg_location.obj = coordinate;
                    handler.sendMessage(msg_location);

                    Message msg_log = Message.obtain();
                    msg_log.what = MSG_ID.MSG_LOG.ordinal();
                    msg_log.obj = days.get(0).getLog();
                    handler.sendMessage(msg_log);
                    handler.sendEmptyMessage(MSG_ID.MSG_SENT.ordinal());
                }
                List<Day> daysRated = db.dayDao().getAll();
                if (daysRated.size() > 0) {
                    Message msg_log = Message.obtain();
                    msg_log.what = MSG_ID.MSG_LIST_RATED.ordinal();
                    msg_log.obj = daysRated;
                    handler.sendMessage(msg_log);
                }
            }
        }.start();
    }

    protected void goToday() {
        m_calendar.setTimeInMillis(System.currentTimeMillis());
        MCalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.travelTo(new DateData(m_calendar.get(Calendar.YEAR), m_calendar.get(Calendar.MONTH) + 1, m_calendar.get(Calendar.DAY_OF_MONTH)));
        selectDate(new DateData(m_calendar.get(Calendar.YEAR), m_calendar.get(Calendar.MONTH) + 1, m_calendar.get(Calendar.DAY_OF_MONTH)));
        //Initialize the labels
        updateLabel();
        Toast.makeText(getBaseContext(), R.string.main_today, Toast.LENGTH_SHORT).show();
    }
}
