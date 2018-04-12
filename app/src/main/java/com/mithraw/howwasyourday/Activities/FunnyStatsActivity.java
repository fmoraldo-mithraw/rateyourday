package com.mithraw.howwasyourday.Activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mithraw.howwasyourday.App;
import com.mithraw.howwasyourday.Helpers.Statistics.DayHelper;
import com.mithraw.howwasyourday.Helpers.Statistics.MonthHelper;
import com.mithraw.howwasyourday.Helpers.Statistics.Statistics;
import com.mithraw.howwasyourday.Helpers.Statistics.StatisticsDatas;
import com.mithraw.howwasyourday.Helpers.Statistics.YearHelper;
import com.mithraw.howwasyourday.R;
import com.mithraw.howwasyourday.Tools.MathTool;
import com.mithraw.howwasyourday.databases.Day;
import com.mithraw.howwasyourday.databases.DaysDatabase;

import java.util.Calendar;
import java.util.List;


public class FunnyStatsActivity extends AppCompatActivity {
    private DaysDatabase db;
    protected static Handler handler;
    CardView lastCardviewRemoved = null;
    String lastKeyRemoved = "";
    private String[] listProperties = {"stats_show_nothing_interresting",
            "stats_show_last_week",
            "stats_show_current_month",
            "stats_show_last_month",
            "stats_show_current_year",
            "stats_show_last_year",
            "stats_show_all_time"
    };

    private enum MSG_ID {LAST_WEEK, CURRENT_MONTH, LAST_MONTH, CURRENT_YEAR, LAST_YEAR, ALL_TIME}


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funny_stats);
        //Get the DB
        db = DaysDatabase.getInstance(getApplicationContext());

        //Add the back button to the actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageButton btn = findViewById(R.id.nothing_interresting_button);
        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resources res = App.getApplication().getResources();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                alertDialogBuilder.setMessage(R.string.stat_removed)
                        .setTitle(R.string.stat_removed_title);
                alertDialogBuilder.setPositiveButton(R.string.stat_removed_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        lastCardviewRemoved = findViewById(R.id.card_view_nothing_interesting);
                        lastCardviewRemoved.setVisibility(View.GONE);
                        lastKeyRemoved = "stats_show_nothing_interresting";
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

        btn = findViewById(R.id.last_week_button);
        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resources res = App.getApplication().getResources();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                alertDialogBuilder.setMessage(R.string.stat_removed)
                        .setTitle(R.string.stat_removed_title);
                alertDialogBuilder.setPositiveButton(R.string.stat_removed_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        lastCardviewRemoved = findViewById(R.id.card_view_last_week);
                        lastCardviewRemoved.setVisibility(View.GONE);
                        lastKeyRemoved = "stats_show_last_week";
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
        btn = findViewById(R.id.current_month_button);
        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resources res = App.getApplication().getResources();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                alertDialogBuilder.setMessage(R.string.stat_removed)
                        .setTitle(R.string.stat_removed_title);
                alertDialogBuilder.setPositiveButton(R.string.stat_removed_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        lastCardviewRemoved = findViewById(R.id.card_view_current_month);
                        lastCardviewRemoved.setVisibility(View.GONE);
                        lastKeyRemoved = "stats_show_current_month";
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
        btn = findViewById(R.id.last_month_button);
        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resources res = App.getApplication().getResources();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                alertDialogBuilder.setMessage(R.string.stat_removed)
                        .setTitle(R.string.stat_removed_title);
                alertDialogBuilder.setPositiveButton(R.string.stat_removed_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        lastCardviewRemoved = findViewById(R.id.card_view_last_month);
                        lastCardviewRemoved.setVisibility(View.GONE);
                        lastKeyRemoved = "stats_show_last_month";
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
        btn = findViewById(R.id.current_year_button);
        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resources res = App.getApplication().getResources();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                alertDialogBuilder.setMessage(R.string.stat_removed)
                        .setTitle(R.string.stat_removed_title);
                alertDialogBuilder.setPositiveButton(R.string.stat_removed_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        lastCardviewRemoved = findViewById(R.id.card_view_current_year);
                        lastCardviewRemoved.setVisibility(View.GONE);
                        lastKeyRemoved = "stats_show_current_year";
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
        btn = findViewById(R.id.last_year_button);
        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resources res = App.getApplication().getResources();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                alertDialogBuilder.setMessage(R.string.stat_removed)
                        .setTitle(R.string.stat_removed_title);
                alertDialogBuilder.setPositiveButton(R.string.stat_removed_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        lastCardviewRemoved = findViewById(R.id.card_view_last_year);
                        lastCardviewRemoved.setVisibility(View.GONE);
                        lastKeyRemoved = "stats_show_last_year";
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
        btn = findViewById(R.id.all_time_button);
        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resources res = App.getApplication().getResources();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                alertDialogBuilder.setMessage(R.string.stat_removed)
                        .setTitle(R.string.stat_removed_title);
                alertDialogBuilder.setPositiveButton(R.string.stat_removed_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        lastCardviewRemoved = findViewById(R.id.card_view_all_time);
                        lastCardviewRemoved.setVisibility(View.GONE);
                        lastKeyRemoved = "stats_show_all_time";
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

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                CardView cv_nothing_interesting = findViewById(R.id.card_view_nothing_interesting);
                if (msg.what == MSG_ID.LAST_WEEK.ordinal()) {
                    boolean showCard = PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean("stats_show_last_week", true);
                    Statistics datas = (Statistics) msg.obj;
                    if ((showCard) && (datas != null) && (datas.getFavoriteDay() != null) && (datas.getWorstDay() != null)) {
                        cv_nothing_interesting.setVisibility(View.GONE);
                        CardView cvLastWeek = findViewById(R.id.card_view_last_week);
                        cvLastWeek.setVisibility(View.VISIBLE);
                        TextView favDay = findViewById(R.id.text_view_last_week_favorite_day);
                        favDay.setText(DayHelper.getInstance().format(datas.getFavoriteDay(), 0));
                        TextView worstDay = findViewById(R.id.text_view_last_week_worst_day);
                        worstDay.setText(DayHelper.getInstance().format(datas.getWorstDay(), 0));
                    }
                }
                if (msg.what == MSG_ID.CURRENT_MONTH.ordinal()) {
                    boolean showCard = PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean("stats_show_current_month", true);
                    Statistics datas = (Statistics) msg.obj;
                    if ((showCard) && (datas != null) && (datas.getFavoriteDay() != null) && (datas.getWorstDay() != null)) {
                        cv_nothing_interesting.setVisibility(View.GONE);
                        CardView cvLastWeek = findViewById(R.id.card_view_current_month);
                        cvLastWeek.setVisibility(View.VISIBLE);
                        TextView favDay = findViewById(R.id.text_view_current_month_favorite_day);
                        favDay.setText(DayHelper.getInstance().format(datas.getFavoriteDay(), 1));
                        TextView worstDay = findViewById(R.id.text_view_current_month_worst_day);
                        worstDay.setText(DayHelper.getInstance().format(datas.getWorstDay(), 1));
                    }
                }
                if (msg.what == MSG_ID.LAST_MONTH.ordinal()) {
                    boolean showCard = PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean("stats_show_last_month", true);
                    Statistics datas = (Statistics) msg.obj;
                    if ((showCard) && (datas != null) && (datas.getFavoriteDay() != null) && (datas.getWorstDay() != null)) {
                        cv_nothing_interesting.setVisibility(View.GONE);
                        CardView cvLastWeek = findViewById(R.id.card_view_last_month);
                        cvLastWeek.setVisibility(View.VISIBLE);
                        TextView favDay = findViewById(R.id.text_view_last_month_favorite_day);
                        favDay.setText(DayHelper.getInstance().format(datas.getFavoriteDay(), 1));
                        TextView worstDay = findViewById(R.id.text_view_last_month_worst_day);
                        worstDay.setText(DayHelper.getInstance().format(datas.getWorstDay(), 1));
                    }
                }
                if (msg.what == MSG_ID.CURRENT_YEAR.ordinal()) {
                    boolean showCard = PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean("stats_show_current_year", true);
                    Statistics datas = (Statistics) msg.obj;
                    if ((showCard) &&
                            (datas != null) &&
                            (datas.getFavoriteDay() != null) &&
                            (datas.getWorstDay() != null) &&
                            (datas.getFavoriteMonth() != null) &&
                            (datas.getWorstMonth() != null)) {

                        cv_nothing_interesting.setVisibility(View.GONE);
                        CardView cvLastWeek = findViewById(R.id.card_view_current_year);
                        cvLastWeek.setVisibility(View.VISIBLE);
                        TextView favDay = findViewById(R.id.text_view_current_year_favorite_day);
                        favDay.setText(DayHelper.getInstance().format(datas.getFavoriteDay(), 2));
                        TextView worstDay = findViewById(R.id.text_view_current_year_worst_day);
                        worstDay.setText(DayHelper.getInstance().format(datas.getWorstDay(), 2));
                        TextView favMonth = findViewById(R.id.text_view_current_year_favorite_month);
                        favMonth.setText(MonthHelper.getInstance().format(datas.getFavoriteMonth(), 2));
                        TextView worstMonth = findViewById(R.id.text_view_current_year_worst_month);
                        worstMonth.setText(MonthHelper.getInstance().format(datas.getWorstMonth(), 2));
                    }
                }
                if (msg.what == MSG_ID.LAST_YEAR.ordinal()) {
                    boolean showCard = PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean("stats_show_last_year", true);
                    Statistics datas = (Statistics) msg.obj;
                    if ((showCard) &&
                            (datas != null) &&
                            (datas.getFavoriteDay() != null) &&
                            (datas.getWorstDay() != null) &&
                            (datas.getFavoriteMonth() != null) &&
                            (datas.getWorstMonth() != null)) {

                        cv_nothing_interesting.setVisibility(View.GONE);
                        CardView cvLastWeek = findViewById(R.id.card_view_last_year);
                        cvLastWeek.setVisibility(View.VISIBLE);
                        TextView favDay = findViewById(R.id.text_view_last_year_favorite_day);
                        favDay.setText(DayHelper.getInstance().format(datas.getFavoriteDay(), 2));
                        TextView worstDay = findViewById(R.id.text_view_last_year_worst_day);
                        worstDay.setText(DayHelper.getInstance().format(datas.getWorstDay(), 2));
                        TextView favMonth = findViewById(R.id.text_view_last_year_favorite_month);
                        favMonth.setText(MonthHelper.getInstance().format(datas.getFavoriteMonth(), 2));
                        TextView worstMonth = findViewById(R.id.text_view_last_year_worst_month);
                        worstMonth.setText(MonthHelper.getInstance().format(datas.getWorstMonth(), 2));
                    }
                }
                if (msg.what == MSG_ID.ALL_TIME.ordinal()) {
                    boolean showCard = PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean("stats_show_all_time", true);
                    Statistics datas = (Statistics) msg.obj;
                    if ((showCard) &&
                            (datas != null) &&
                            (datas.getFavoriteDay() != null) &&
                            (datas.getWorstDay() != null) &&
                            (datas.getFavoriteMonth() != null) &&
                            (datas.getWorstMonth() != null) &&
                            (datas.getFavoriteYear() != null) &&
                            (datas.getWorstYear() != null)) {

                        cv_nothing_interesting.setVisibility(View.GONE);
                        CardView cvLastWeek = findViewById(R.id.card_view_all_time);
                        cvLastWeek.setVisibility(View.VISIBLE);
                        TextView favDay = findViewById(R.id.text_view_all_time_favorite_day);
                        favDay.setText(DayHelper.getInstance().format(datas.getFavoriteDay(), 2));
                        TextView worstDay = findViewById(R.id.text_view_all_time_worst_day);
                        worstDay.setText(DayHelper.getInstance().format(datas.getWorstDay(), 2));
                        TextView favMonth = findViewById(R.id.text_view_all_time_favorite_month);
                        favMonth.setText(MonthHelper.getInstance().format(datas.getFavoriteMonth(), 2));
                        TextView worstMonth = findViewById(R.id.text_view_all_time_worst_month);
                        worstMonth.setText(MonthHelper.getInstance().format(datas.getWorstMonth(), 2));
                        TextView favYear = findViewById(R.id.text_view_all_time_favorite_year);
                        favYear.setText(YearHelper.getInstance().format(datas.getFavoriteYear(), 2));
                        TextView worstYear = findViewById(R.id.text_view_all_time_worst_year);
                        worstYear.setText(YearHelper.getInstance().format(datas.getWorstYear(), 2));
                    }
                }
            }
        };
    }

    private void CheckHidingStatuses() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        int curMonth = cal.get(Calendar.MONTH);
        int curYear = cal.get(Calendar.YEAR);
        for (String propertyKey : listProperties) {
            boolean showCard = PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean(propertyKey, true);
            int showCardMonth = PreferenceManager.getDefaultSharedPreferences(App.getContext()).getInt(propertyKey + "_month", 0);
            int showCardYear = PreferenceManager.getDefaultSharedPreferences(App.getContext()).getInt(propertyKey + "_year", 0);
            if ((showCard == false) && (((curMonth > showCardMonth) && (curYear >= showCardYear)) || (curYear > showCardYear))) {
                PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean(propertyKey, true).apply();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CardView cv = findViewById(R.id.card_view_nothing_interesting);
        CheckHidingStatuses();
        boolean showCard = PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean("stats_show_nothing_interresting", true);
        if (showCard)
            cv.setVisibility(View.VISIBLE);
        CardView cvLastWeek = findViewById(R.id.card_view_last_week);
        cvLastWeek.setVisibility(View.GONE);
        CardView cvCurMonth = findViewById(R.id.card_view_current_month);
        cvCurMonth.setVisibility(View.GONE);
        CardView cvLastMonth = findViewById(R.id.card_view_last_month);
        cvLastMonth.setVisibility(View.GONE);
        CardView cvCurYear = findViewById(R.id.card_view_current_year);
        cvCurYear.setVisibility(View.GONE);
        CardView cvLastYear = findViewById(R.id.card_view_last_year);
        cvLastYear.setVisibility(View.GONE);
        CardView cvAllTime = findViewById(R.id.card_view_all_time);
        cvAllTime.setVisibility(View.GONE);
        new Thread() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());

                //Last week
                int firstDayOfTheWeek = calendar.getFirstDayOfWeek();
                int currentDayOfTheWeek = calendar.get(Calendar.DAY_OF_WEEK);
                int stepToBeFirstDayOfTheWeek = MathTool.floorMod(currentDayOfTheWeek - firstDayOfTheWeek, 7);
                StatisticsDatas favoriteStatDay = new StatisticsDatas();
                StatisticsDatas worstStatDay = new StatisticsDatas();
                Day favoriteDay = null;
                Day worstDay = null;
                int dayToRemove = stepToBeFirstDayOfTheWeek;
                calendar.add(Calendar.DAY_OF_MONTH, -(dayToRemove));
                //Get days of the last week
                int dayCount = 0;
                for (int i = 1; i < 8; i++) {
                    int curId = MathTool.floorMod((firstDayOfTheWeek - i - 1), 7) + 1;
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    List<Day> dList = db.dayDao().getDay(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
                    if (dList.size() == 1) {
                        Day d = dList.get(0);
                        dayCount++;
                        if ((favoriteDay == null) || (d.getRating() > favoriteDay.getRating())) {
                            favoriteDay = d;
                            favoriteStatDay.setRate(d.getRating()).setId(curId);
                        }
                        if ((worstDay == null) || (d.getRating() < worstDay.getRating())) {
                            worstDay = d;
                            worstStatDay.setRate(d.getRating()).setId(curId);
                        }

                    }
                }
                //If there is more than one day get the best and the worst
                if (dayCount > 1) {
                    //Set the object to send
                    Statistics stats = new Statistics();
                    stats.setFavoriteDay(favoriteStatDay);
                    stats.setWorstDay(worstStatDay);
                    //Send the message
                    Message msg_rating = Message.obtain();
                    msg_rating.what = MSG_ID.LAST_WEEK.ordinal();
                    msg_rating.obj = stats;
                    handler.sendMessage(msg_rating);
                }

                //Current month
                //Get current month averages of each days
                favoriteStatDay = new StatisticsDatas();
                worstStatDay = new StatisticsDatas();
                float maxRate = 0;
                float minRate = 5;
                dayCount = 0;
                calendar.setTimeInMillis(System.currentTimeMillis());
                for (int i = 1; i < 8; i++) {
                    float rate = db.dayDao().getAverageRatingPerDayOfTheWeekPerMonthAndYear(i, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
                    if (rate >= 1) {
                        dayCount++;
                        if (rate > maxRate) {
                            maxRate = rate;
                            favoriteStatDay.setRate(rate).setId(i);
                        }
                        if (rate < minRate) {
                            minRate = rate;
                            worstStatDay.setRate(rate).setId(i);
                        }
                    }
                }
                //If there is more than one day get the best and the worst
                if (dayCount > 1) {
                    //Set the object to send
                    Statistics stats = new Statistics();
                    stats.setFavoriteDay(favoriteStatDay);
                    stats.setWorstDay(worstStatDay);
                    //Send the message
                    Message msg_rating = Message.obtain();
                    msg_rating.what = MSG_ID.CURRENT_MONTH.ordinal();
                    msg_rating.obj = stats;
                    handler.sendMessage(msg_rating);
                }

                //Last month
                //Get last month averages of each days
                favoriteStatDay = new StatisticsDatas();
                worstStatDay = new StatisticsDatas();
                maxRate = 0;
                minRate = 5;
                dayCount = 0;
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.add(Calendar.MONTH, -1);
                for (int i = 1; i < 8; i++) {
                    float rate = db.dayDao().getAverageRatingPerDayOfTheWeekPerMonthAndYear(i, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
                    if (rate >= 1) {
                        dayCount++;
                        if (rate > maxRate) {
                            maxRate = rate;
                            favoriteStatDay.setRate(rate).setId(i);
                        }
                        if (rate < minRate) {
                            minRate = rate;
                            worstStatDay.setRate(rate).setId(i);
                        }
                    }
                }
                //If there is more than one day get the best and the worst
                if (dayCount > 1) {
                    //Set the object to send
                    Statistics stats = new Statistics();
                    stats.setFavoriteDay(favoriteStatDay);
                    stats.setWorstDay(worstStatDay);
                    //Send the message
                    Message msg_rating = Message.obtain();
                    msg_rating.what = MSG_ID.LAST_MONTH.ordinal();
                    msg_rating.obj = stats;
                    handler.sendMessage(msg_rating);
                }

                //Current year
                //Get last month averages of each days
                StatisticsDatas favoriteStatMonth = new StatisticsDatas();
                StatisticsDatas worstStatMonth = new StatisticsDatas();
                favoriteStatDay = new StatisticsDatas();
                worstStatDay = new StatisticsDatas();
                float maxRateMonth = 0;
                float minRateMonth = 5;
                maxRate = 0;
                minRate = 5;
                int monthCount = 0;
                dayCount = 0;
                calendar.setTimeInMillis(System.currentTimeMillis());

                for (int i = 1; i < 8; i++) {
                    float rate = db.dayDao().getAverageRatingPerDayOfTheWeekAndYear(i, calendar.get(Calendar.YEAR));
                    if (rate >= 1) {
                        dayCount++;
                        if (rate > maxRate) {
                            maxRate = rate;
                            favoriteStatDay.setRate(rate).setId(i);
                        }
                        if (rate < minRate) {
                            minRate = rate;
                            worstStatDay.setRate(rate).setId(i);
                        }
                    }
                }
                for (int i = 0; i < 12; i++) {
                    float rate = db.dayDao().getAverageRatingPerMonthAndYear(i, calendar.get(Calendar.YEAR));
                    if (rate >= 1) {
                        monthCount++;
                        if (rate > maxRateMonth) {
                            maxRateMonth = rate;
                            favoriteStatMonth.setRate(rate).setId(i);
                        }
                        if (rate < minRateMonth) {
                            minRateMonth = rate;
                            worstStatMonth.setRate(rate).setId(i);
                        }
                    }
                }
                //If there is more than one day get the best and the worst
                if ((dayCount > 1) && (monthCount > 1)) {
                    //Set the object to send
                    Statistics stats = new Statistics();
                    stats.setFavoriteDay(favoriteStatDay);
                    stats.setWorstDay(worstStatDay);
                    stats.setFavoriteMonth(favoriteStatMonth);
                    stats.setWorstMonth(worstStatMonth);
                    //Send the message
                    Message msg_rating = Message.obtain();
                    msg_rating.what = MSG_ID.CURRENT_YEAR.ordinal();
                    msg_rating.obj = stats;
                    handler.sendMessage(msg_rating);
                }

                //Last year
                //Get last month averages of each days
                favoriteStatMonth = new StatisticsDatas();
                worstStatMonth = new StatisticsDatas();
                favoriteStatDay = new StatisticsDatas();
                worstStatDay = new StatisticsDatas();
                monthCount = 0;
                dayCount = 0;
                maxRateMonth = 0;
                minRateMonth = 5;
                maxRate = 0;
                minRate = 5;
                calendar.setTimeInMillis(System.currentTimeMillis());
                for (int i = 1; i < 8; i++) {
                    float rate = db.dayDao().getAverageRatingPerDayOfTheWeekAndYear(i, calendar.get(Calendar.YEAR) - 1);
                    if (rate >= 1) {
                        dayCount++;
                        if (rate > maxRate) {
                            maxRate = rate;
                            favoriteStatDay.setRate(rate).setId(i);
                        }
                        if (rate < minRate) {
                            minRate = rate;
                            worstStatDay.setRate(rate).setId(i);
                        }
                    }
                }
                for (int i = 0; i < 12; i++) {
                    float rate = db.dayDao().getAverageRatingPerMonthAndYear(i, calendar.get(Calendar.YEAR) - 1);
                    if (rate >= 1) {
                        monthCount++;
                        if (rate > maxRateMonth) {
                            maxRateMonth = rate;
                            favoriteStatMonth.setRate(rate).setId(i);
                        }
                        if (rate < minRateMonth) {
                            minRateMonth = rate;
                            worstStatMonth.setRate(rate).setId(i);
                        }
                    }
                }
                //If there is more than one day get the best and the worst
                if ((dayCount > 1) && (monthCount > 1)) {
                    //Set the object to send
                    Statistics stats = new Statistics();
                    stats.setFavoriteDay(favoriteStatDay);
                    stats.setWorstDay(worstStatDay);
                    stats.setFavoriteMonth(favoriteStatMonth);
                    stats.setWorstMonth(worstStatMonth);
                    //Send the message
                    Message msg_rating = Message.obtain();
                    msg_rating.what = MSG_ID.LAST_YEAR.ordinal();
                    msg_rating.obj = stats;
                    handler.sendMessage(msg_rating);
                }

                //All time
                StatisticsDatas favoriteStatYear = new StatisticsDatas();
                StatisticsDatas worstStatYear = new StatisticsDatas();
                favoriteStatMonth = new StatisticsDatas();
                worstStatMonth = new StatisticsDatas();
                favoriteStatDay = new StatisticsDatas();
                worstStatDay = new StatisticsDatas();
                float maxRateYear = 0;
                float minRateYear = 5;
                maxRateMonth = 0;
                minRateMonth = 5;
                maxRate = 0;
                minRate = 5;
                int yearCount = 0;
                monthCount = 0;
                dayCount = 0;
                calendar.setTimeInMillis(System.currentTimeMillis());
                for (int i = 1; i < 8; i++) {
                    float rate = db.dayDao().getAverageRatingPerDayOfTheWeekAllTime(i);
                    if (rate >= 1) {
                        dayCount++;
                        if (rate > maxRate) {
                            maxRate = rate;
                            favoriteStatDay.setRate(rate).setId(i);
                        }
                        if (rate < minRate) {
                            minRate = rate;
                            worstStatDay.setRate(rate).setId(i);
                        }
                    }
                }
                for (int i = 0; i < 12; i++) {
                    float rate = db.dayDao().getAverageRatingPerMonthAllTime(i);
                    if (rate >= 1) {
                        monthCount++;
                        if (rate > maxRateMonth) {
                            maxRateMonth = rate;
                            favoriteStatMonth.setRate(rate).setId(i);
                        }
                        if (rate < minRateMonth) {
                            minRateMonth = rate;
                            worstStatMonth.setRate(rate).setId(i);
                        }
                    }
                }
                List<Day> DayForYears = db.dayDao().getYearsRated();
                for (Day d : DayForYears) {
                    float rate = db.dayDao().getAverageRatingPerYear(d.getYear());
                    if (rate >= 1) {
                        yearCount++;
                        if (rate > maxRateYear) {
                            maxRateYear = rate;
                            favoriteStatYear.setRate(rate).setId(d.getYear());
                        }
                        if (rate < minRateYear) {
                            minRateYear = rate;
                            worstStatYear.setRate(rate).setId(d.getYear());
                        }
                    }
                }
                //If there is more than one day get the best and the worst
                if ((dayCount > 1) && (monthCount > 1) && (yearCount > 1)) {
                    //Set the object to send
                    Statistics stats = new Statistics();
                    stats.setFavoriteDay(favoriteStatDay);
                    stats.setWorstDay(worstStatDay);
                    stats.setFavoriteMonth(favoriteStatMonth);
                    stats.setWorstMonth(worstStatMonth);
                    stats.setFavoriteYear(favoriteStatYear);
                    stats.setWorstYear(worstStatYear);
                    //Send the message
                    Message msg_rating = Message.obtain();
                    msg_rating.what = MSG_ID.ALL_TIME.ordinal();
                    msg_rating.obj = stats;
                    handler.sendMessage(msg_rating);
                }
            }
        }.

                start();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
