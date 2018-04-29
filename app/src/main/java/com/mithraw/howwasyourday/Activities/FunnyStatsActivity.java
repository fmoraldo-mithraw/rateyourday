package com.mithraw.howwasyourday.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mithraw.howwasyourday.App;
import com.mithraw.howwasyourday.Dialogs.TipsDialog;
import com.mithraw.howwasyourday.Helpers.FunnyStatsHelper;
import com.mithraw.howwasyourday.Helpers.Statistics.DayHelper;
import com.mithraw.howwasyourday.Helpers.Statistics.MonthHelper;
import com.mithraw.howwasyourday.Helpers.Statistics.Statistics;
import com.mithraw.howwasyourday.Helpers.Statistics.StatisticsAdds;
import com.mithraw.howwasyourday.Helpers.Statistics.StatisticsDatas;
import com.mithraw.howwasyourday.Helpers.Statistics.StatisticsHelper;
import com.mithraw.howwasyourday.Helpers.Statistics.YearHelper;
import com.mithraw.howwasyourday.R;
import com.mithraw.howwasyourday.Tools.MathTool;
import com.mithraw.howwasyourday.databases.Day;
import com.mithraw.howwasyourday.databases.DaysDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/*
Show interesting stats
 */
public class FunnyStatsActivity extends AppCompatActivity {
    private DaysDatabase db;
    protected static Handler handler;
    FunnyStatsHelper mStatHelperNothing;
    FunnyStatsHelper mStatHelperLastWeek;
    FunnyStatsHelper mStatHelperCurrentMonth;
    FunnyStatsHelper mStatHelperLastMonth;
    FunnyStatsHelper mStatHelperCurrentYear;
    FunnyStatsHelper mStatHelperLastYear;
    FunnyStatsHelper mStatHelperAllTime;
    FunnyStatsHelper mStatHelperBadAvg;
    FunnyStatsHelper mStatHelperExtrem;
    FunnyStatsHelper mStatHelperBigGap;
    private String[] listProperties = {"stats_show_nothing_interresting",
            "stats_show_last_week",
            "stats_show_current_month",
            "stats_show_last_month",
            "stats_show_current_year",
            "stats_show_last_year",
            "stats_trophy",
            "stats_extreme",
            "stats_big_gap",
            "stats_bad_avg",
            "stats_show_all_time"
    };

    private enum MSG_ID {LAST_WEEK, CURRENT_MONTH, LAST_MONTH, CURRENT_YEAR, LAST_YEAR, ALL_TIME, TROPHY}


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funny_stats);
        //Get the DB
        db = DaysDatabase.getInstance(getApplicationContext());

        //Add the back button to the actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mStatHelperNothing = new FunnyStatsHelper((CardView) findViewById(R.id.card_view_nothing_interesting), this, "stats_show_nothing_interresting", true);
        mStatHelperLastWeek = new FunnyStatsHelper((CardView)findViewById(R.id.card_view_last_week),this,"stats_show_last_week");
        mStatHelperCurrentMonth = new FunnyStatsHelper((CardView)findViewById(R.id.card_view_current_month),this,"stats_show_current_month");
        mStatHelperLastMonth = new FunnyStatsHelper((CardView)findViewById(R.id.card_view_last_month),this,"stats_show_last_month");
        mStatHelperCurrentYear = new FunnyStatsHelper((CardView)findViewById(R.id.card_view_current_year),this,"stats_show_current_year");
        mStatHelperLastYear = new FunnyStatsHelper((CardView)findViewById(R.id.card_view_last_year),this,"stats_show_last_year");
        mStatHelperAllTime = new FunnyStatsHelper((CardView)findViewById(R.id.card_view_all_time),this,"stats_show_all_time");
        mStatHelperBadAvg = new FunnyStatsHelper((CardView) findViewById(R.id.card_view_bad_avg), this, "stats_show_bad_avg");
        mStatHelperBigGap = new FunnyStatsHelper((CardView) findViewById(R.id.card_view_big_gap), this, "stats_show_big_gap");
        mStatHelperExtrem = new FunnyStatsHelper((CardView) findViewById(R.id.card_view_extreme), this, "stats_show_extreme");

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_ID.TROPHY.ordinal()) {
                    boolean showCard = PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean("stats_trophy", true);
                    int days = (int) msg.obj;
                    if (showCard) {
                        CardView cvTrophy = findViewById(R.id.card_view_trophy);
                        TextView tvTrophy = findViewById(R.id.text_view_trophy);
                        ImageView ivTrophy = findViewById(R.id.imageViewTrophy);
                        if (days > 30) {
                            mStatHelperNothing.forceHide();
                            cvTrophy.setVisibility(View.VISIBLE);
                            if (days > 90) {
                                if (days > 180) {
                                    tvTrophy.setText(R.string.gold_text);
                                    ivTrophy.setImageResource(R.drawable.ic_badge_gold);
                                } else {
                                    tvTrophy.setText(R.string.silver_text);
                                    ivTrophy.setImageResource(R.drawable.ic_badge_silver);
                                }
                            } else {
                                tvTrophy.setText(R.string.bronze_text);
                                ivTrophy.setImageResource(R.drawable.ic_badge_bronze);
                            }
                        }

                    }
                }
                if (msg.what == MSG_ID.LAST_WEEK.ordinal()) {
                    Statistics datas = (Statistics) msg.obj;
                    if ((datas != null) && (datas.getFavoriteDay() != null) && (datas.getWorstDay() != null)) {
                        mStatHelperNothing.forceHide();
                        mStatHelperLastWeek.show();
                        TextView favDay = findViewById(R.id.text_view_last_week_favorite_day);
                        favDay.setText(DayHelper.getInstance().format(datas.getFavoriteDay(), 0));
                        TextView worstDay = findViewById(R.id.text_view_last_week_worst_day);
                        worstDay.setText(DayHelper.getInstance().format(datas.getWorstDay(), 0));
                    }
                }
                if (msg.what == MSG_ID.CURRENT_MONTH.ordinal()) {
                    Statistics datas = (Statistics) msg.obj;
                    if ((datas != null) && (datas.getFavoriteDay() != null) && (datas.getWorstDay() != null)) {
                        mStatHelperNothing.forceHide();
                        mStatHelperCurrentMonth.show();
                        TextView title = findViewById(R.id.current_month_text_view);
                        title.setText(new SimpleDateFormat("MMMM").format(new java.util.Date(Calendar.getInstance().getTimeInMillis())));
                        TextView favDay = findViewById(R.id.text_view_current_month_favorite_day);
                        favDay.setText(DayHelper.getInstance().format(datas.getFavoriteDay(), 1));
                        TextView worstDay = findViewById(R.id.text_view_current_month_worst_day);
                        worstDay.setText(DayHelper.getInstance().format(datas.getWorstDay(), 1));
                        if (datas.getStatisticsAdds() != null) {
                            CardView cv = findViewById(R.id.card_view_current_month);
                            StatisticsAdds adds = datas.getStatisticsAdds();
                            ((TextView) cv.findViewById(R.id.text_view_all_time_days_rated)).setText(String.valueOf(adds.getNumberOfRatedDays()));
                            ((TextView) cv.findViewById(R.id.text_view_all_time_average_rate)).setText(StatisticsHelper.floatFormat(adds.getAverageDay(), 2));
                            if (!adds.getBadAvgQuote().equals("")) {
                                mStatHelperBadAvg.show();
                                mStatHelperBadAvg.setQuote(adds.getBadAvgQuote());
                            }
                            ((TextView) cv.findViewById(R.id.number_of_days_rated_1)).setText(String.valueOf(adds.getNumDayRated1()));
                            ((TextView) cv.findViewById(R.id.number_of_days_rated_2)).setText(String.valueOf(adds.getNumDayRated2()));
                            ((TextView) cv.findViewById(R.id.number_of_days_rated_3)).setText(String.valueOf(adds.getNumDayRated3()));
                            ((TextView) cv.findViewById(R.id.number_of_days_rated_4)).setText(String.valueOf(adds.getNumDayRated4()));
                            ((TextView) cv.findViewById(R.id.number_of_days_rated_5)).setText(String.valueOf(adds.getNumDayRated5()));
                            if (!adds.getExtremQuote().equals("")) {
                                mStatHelperExtrem.show();
                                mStatHelperExtrem.setQuote(adds.getExtremQuote());
                            }
                            ((TextView) cv.findViewById(R.id.monday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgMonday(), 2));
                            ((TextView) cv.findViewById(R.id.tuesday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgTuesday(), 2));
                            ((TextView) cv.findViewById(R.id.wednesday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgWednesday(), 2));
                            ((TextView) cv.findViewById(R.id.thursday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgThursday(), 2));
                            ((TextView) cv.findViewById(R.id.friday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgFriday(), 2));
                            ((TextView) cv.findViewById(R.id.saturday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgSaturday(), 2));
                            ((TextView) cv.findViewById(R.id.sunday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgSunday(), 2));
                            if (!adds.getBigGapQuote().equals("")) {
                                mStatHelperBigGap.show();
                                mStatHelperBigGap.setQuote(adds.getBigGapQuote());
                            }
                        }
                    }
                }
                if (msg.what == MSG_ID.LAST_MONTH.ordinal()) {
                    Statistics datas = (Statistics) msg.obj;
                    if ((datas != null) && (datas.getFavoriteDay() != null) && (datas.getWorstDay() != null)) {
                        mStatHelperNothing.forceHide();
                        mStatHelperLastMonth.show();
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.MONTH,-1);

                        TextView title = findViewById(R.id.last_month_text_view);
                        title.setText(new SimpleDateFormat("MMMM").format(new java.util.Date(cal.getTimeInMillis())));
                        TextView favDay = findViewById(R.id.text_view_last_month_favorite_day);
                        favDay.setText(DayHelper.getInstance().format(datas.getFavoriteDay(), 1));
                        TextView worstDay = findViewById(R.id.text_view_last_month_worst_day);
                        worstDay.setText(DayHelper.getInstance().format(datas.getWorstDay(), 1));
                        if (datas.getStatisticsAdds() != null) {
                            CardView cv = findViewById(R.id.card_view_last_month);
                            StatisticsAdds adds = datas.getStatisticsAdds();
                            ((TextView) cv.findViewById(R.id.text_view_all_time_days_rated)).setText(String.valueOf(adds.getNumberOfRatedDays()));
                            ((TextView) cv.findViewById(R.id.text_view_all_time_average_rate)).setText(StatisticsHelper.floatFormat(adds.getAverageDay(), 2));
                            if (!adds.getBadAvgQuote().equals("")) {
                                mStatHelperBadAvg.show();
                                mStatHelperBadAvg.setQuote(adds.getBadAvgQuote());
                            }
                            ((TextView) cv.findViewById(R.id.number_of_days_rated_1)).setText(String.valueOf(adds.getNumDayRated1()));
                            ((TextView) cv.findViewById(R.id.number_of_days_rated_2)).setText(String.valueOf(adds.getNumDayRated2()));
                            ((TextView) cv.findViewById(R.id.number_of_days_rated_3)).setText(String.valueOf(adds.getNumDayRated3()));
                            ((TextView) cv.findViewById(R.id.number_of_days_rated_4)).setText(String.valueOf(adds.getNumDayRated4()));
                            ((TextView) cv.findViewById(R.id.number_of_days_rated_5)).setText(String.valueOf(adds.getNumDayRated5()));
                            if (!adds.getExtremQuote().equals("")) {
                                mStatHelperExtrem.show();
                                mStatHelperExtrem.setQuote(adds.getExtremQuote());
                            }
                            ((TextView) cv.findViewById(R.id.monday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgMonday(), 2));
                            ((TextView) cv.findViewById(R.id.tuesday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgTuesday(), 2));
                            ((TextView) cv.findViewById(R.id.wednesday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgWednesday(), 2));
                            ((TextView) cv.findViewById(R.id.thursday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgThursday(), 2));
                            ((TextView) cv.findViewById(R.id.friday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgFriday(), 2));
                            ((TextView) cv.findViewById(R.id.saturday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgSaturday(), 2));
                            ((TextView) cv.findViewById(R.id.sunday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgSunday(), 2));
                            if (!adds.getBigGapQuote().equals("")) {
                                mStatHelperBigGap.show();
                                mStatHelperBigGap.setQuote(adds.getBigGapQuote());
                            }
                        }

                    }
                }
                if (msg.what == MSG_ID.CURRENT_YEAR.ordinal()) {
                    Statistics datas = (Statistics) msg.obj;
                    if ((datas != null) &&
                            (datas.getFavoriteDay() != null) &&
                            (datas.getWorstDay() != null) &&
                            (datas.getFavoriteMonth() != null) &&
                            (datas.getWorstMonth() != null)) {
                        mStatHelperNothing.forceHide();
                        mStatHelperCurrentYear.show();
                        TextView title = findViewById(R.id.current_year_text_view);
                        title.setText(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
                        TextView favDay = findViewById(R.id.text_view_current_year_favorite_day);
                        favDay.setText(DayHelper.getInstance().format(datas.getFavoriteDay(), 2));
                        TextView worstDay = findViewById(R.id.text_view_current_year_worst_day);
                        worstDay.setText(DayHelper.getInstance().format(datas.getWorstDay(), 2));
                        TextView favMonth = findViewById(R.id.text_view_current_year_favorite_month);
                        favMonth.setText(MonthHelper.getInstance().format(datas.getFavoriteMonth(), 2));
                        TextView worstMonth = findViewById(R.id.text_view_current_year_worst_month);
                        worstMonth.setText(MonthHelper.getInstance().format(datas.getWorstMonth(), 2));
                        if (datas.getStatisticsAdds() != null) {
                            CardView cv = findViewById(R.id.card_view_current_year);
                            StatisticsAdds adds = datas.getStatisticsAdds();
                            ((TextView) cv.findViewById(R.id.text_view_all_time_days_rated)).setText(String.valueOf(adds.getNumberOfRatedDays()));
                            ((TextView) cv.findViewById(R.id.text_view_all_time_average_rate)).setText(StatisticsHelper.floatFormat(adds.getAverageDay(), 2));
                            ((TextView) cv.findViewById(R.id.number_of_days_rated_1)).setText(String.valueOf(adds.getNumDayRated1()));
                            ((TextView) cv.findViewById(R.id.number_of_days_rated_2)).setText(String.valueOf(adds.getNumDayRated2()));
                            ((TextView) cv.findViewById(R.id.number_of_days_rated_3)).setText(String.valueOf(adds.getNumDayRated3()));
                            ((TextView) cv.findViewById(R.id.number_of_days_rated_4)).setText(String.valueOf(adds.getNumDayRated4()));
                            ((TextView) cv.findViewById(R.id.number_of_days_rated_5)).setText(String.valueOf(adds.getNumDayRated5()));
                            ((TextView) cv.findViewById(R.id.monday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgMonday(), 2));
                            ((TextView) cv.findViewById(R.id.tuesday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgTuesday(), 2));
                            ((TextView) cv.findViewById(R.id.wednesday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgWednesday(), 2));
                            ((TextView) cv.findViewById(R.id.thursday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgThursday(), 2));
                            ((TextView) cv.findViewById(R.id.friday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgFriday(), 2));
                            ((TextView) cv.findViewById(R.id.saturday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgSaturday(), 2));
                            ((TextView) cv.findViewById(R.id.sunday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgSunday(), 2));
                        }
                    }
                }
                if (msg.what == MSG_ID.LAST_YEAR.ordinal()) {
                    Statistics datas = (Statistics) msg.obj;
                    if ((datas != null) &&
                            (datas.getFavoriteDay() != null) &&
                            (datas.getWorstDay() != null) &&
                            (datas.getFavoriteMonth() != null) &&
                            (datas.getWorstMonth() != null)) {
                        mStatHelperNothing.forceHide();
                        mStatHelperLastYear.show();
                        TextView title = findViewById(R.id.last_year_text_view);
                        title.setText(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)-1));
                        TextView favDay = findViewById(R.id.text_view_last_year_favorite_day);
                        favDay.setText(DayHelper.getInstance().format(datas.getFavoriteDay(), 2));
                        TextView worstDay = findViewById(R.id.text_view_last_year_worst_day);
                        worstDay.setText(DayHelper.getInstance().format(datas.getWorstDay(), 2));
                        TextView favMonth = findViewById(R.id.text_view_last_year_favorite_month);
                        favMonth.setText(MonthHelper.getInstance().format(datas.getFavoriteMonth(), 2));
                        TextView worstMonth = findViewById(R.id.text_view_last_year_worst_month);
                        worstMonth.setText(MonthHelper.getInstance().format(datas.getWorstMonth(), 2));
                        if (datas.getStatisticsAdds() != null) {
                            CardView cv = findViewById(R.id.card_view_last_year);
                            StatisticsAdds adds = datas.getStatisticsAdds();
                            ((TextView) cv.findViewById(R.id.text_view_all_time_days_rated)).setText(String.valueOf(adds.getNumberOfRatedDays()));
                            ((TextView) cv.findViewById(R.id.text_view_all_time_average_rate)).setText(StatisticsHelper.floatFormat(adds.getAverageDay(), 2));
                            ((TextView) cv.findViewById(R.id.number_of_days_rated_1)).setText(String.valueOf(adds.getNumDayRated1()));
                            ((TextView) cv.findViewById(R.id.number_of_days_rated_2)).setText(String.valueOf(adds.getNumDayRated2()));
                            ((TextView) cv.findViewById(R.id.number_of_days_rated_3)).setText(String.valueOf(adds.getNumDayRated3()));
                            ((TextView) cv.findViewById(R.id.number_of_days_rated_4)).setText(String.valueOf(adds.getNumDayRated4()));
                            ((TextView) cv.findViewById(R.id.number_of_days_rated_5)).setText(String.valueOf(adds.getNumDayRated5()));
                            ((TextView) cv.findViewById(R.id.monday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgMonday(), 2));
                            ((TextView) cv.findViewById(R.id.tuesday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgTuesday(), 2));
                            ((TextView) cv.findViewById(R.id.wednesday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgWednesday(), 2));
                            ((TextView) cv.findViewById(R.id.thursday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgThursday(), 2));
                            ((TextView) cv.findViewById(R.id.friday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgFriday(), 2));
                            ((TextView) cv.findViewById(R.id.saturday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgSaturday(), 2));
                            ((TextView) cv.findViewById(R.id.sunday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgSunday(), 2));
                        }
                    }
                }
                if (msg.what == MSG_ID.ALL_TIME.ordinal()) {
                    Statistics datas = (Statistics) msg.obj;
                    if ((datas != null) &&
                            (datas.getFavoriteDay() != null) &&
                            (datas.getWorstDay() != null) &&
                            (datas.getFavoriteMonth() != null) &&
                            (datas.getWorstMonth() != null) &&
                            (datas.getFavoriteYear() != null) &&
                            (datas.getWorstYear() != null)) {

                        mStatHelperNothing.forceHide();
                        mStatHelperAllTime.show();
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
                        CardView cv = findViewById(R.id.card_view_all_time);
                        if(datas.getStatisticsAdds() != null){
                            StatisticsAdds adds = datas.getStatisticsAdds();
                            ((TextView) cv.findViewById(R.id.text_view_all_time_days_rated)).setText(String.valueOf(adds.getNumberOfRatedDays()));
                            ((TextView) cv.findViewById(R.id.text_view_all_time_average_rate)).setText(StatisticsHelper.floatFormat(adds.getAverageDay(), 2));
                            ((TextView) cv.findViewById(R.id.number_of_days_rated_1)).setText(String.valueOf(adds.getNumDayRated1()));
                            ((TextView) cv.findViewById(R.id.number_of_days_rated_2)).setText(String.valueOf(adds.getNumDayRated2()));
                            ((TextView) cv.findViewById(R.id.number_of_days_rated_3)).setText(String.valueOf(adds.getNumDayRated3()));
                            ((TextView) cv.findViewById(R.id.number_of_days_rated_4)).setText(String.valueOf(adds.getNumDayRated4()));
                            ((TextView) cv.findViewById(R.id.number_of_days_rated_5)).setText(String.valueOf(adds.getNumDayRated5()));
                            ((TextView) cv.findViewById(R.id.monday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgMonday(), 2));
                            ((TextView) cv.findViewById(R.id.tuesday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgTuesday(), 2));
                            ((TextView) cv.findViewById(R.id.wednesday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgWednesday(), 2));
                            ((TextView) cv.findViewById(R.id.thursday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgThursday(), 2));
                            ((TextView) cv.findViewById(R.id.friday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgFriday(), 2));
                            ((TextView) cv.findViewById(R.id.saturday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgSaturday(), 2));
                            ((TextView) cv.findViewById(R.id.sunday_rate)).setText(StatisticsHelper.floatFormat(adds.getAvgSunday(), 2));
                        }
                    }
                }
            }
        };

        //Display tips
        String preferenceName = "tip_funnystats_showed";
        if ((PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean(preferenceName, false) == false)&&
                (PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean("show_tips", true) == true)){
            ArrayList<Integer> iList = new ArrayList<Integer>();
            iList.add(R.layout.tips_fragment_funnystats_cards);
            Bundle bundl = new Bundle();
            bundl.putIntegerArrayList("listView", iList);
            bundl.putString("preference", preferenceName);
            bundl.putInt("title", R.string.tips_funstats_title);
            DialogFragment newFragment = new TipsDialog();
            newFragment.setArguments(bundl);
            newFragment.show(getSupportFragmentManager(), preferenceName);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        CardView cvTrophy = findViewById(R.id.card_view_trophy);
        cvTrophy.setVisibility(View.GONE);
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
                    StatisticsAdds adds = new StatisticsAdds();
                    adds.setNumDayRated1(db.dayDao().getNumberOfDayByRateByMonthAndYear(1, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
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
                    stats.setStatisticsAdds(adds);
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
                    StatisticsAdds adds = new StatisticsAdds();
                    adds.setNumDayRated1(db.dayDao().getNumberOfDayByRateByMonthAndYear(1, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
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
                    stats.setStatisticsAdds(adds);
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
                    StatisticsAdds adds = new StatisticsAdds();
                    adds.setNumDayRated1(db.dayDao().getNumberOfDayByRateByYear(1, calendar.get(Calendar.YEAR)))
                            .setNumDayRated2(db.dayDao().getNumberOfDayByRateByYear(2, calendar.get(Calendar.YEAR)))
                            .setNumDayRated3(db.dayDao().getNumberOfDayByRateByYear(3, calendar.get(Calendar.YEAR)))
                            .setNumDayRated4(db.dayDao().getNumberOfDayByRateByYear(4, calendar.get(Calendar.YEAR)))
                            .setNumDayRated5(db.dayDao().getNumberOfDayByRateByYear(5, calendar.get(Calendar.YEAR)))
                            .setAverageDay(db.dayDao().getAverageDayByYear(calendar.get(Calendar.YEAR)))
                            .setNumberOfRatedDays(db.dayDao().getNumberOfDaysByYear(calendar.get(Calendar.YEAR)))
                            .setAvgMonday(db.dayDao().getAverageRatingPerDayOfTheWeekAndYear(Calendar.MONDAY, calendar.get(Calendar.YEAR)))
                            .setAvgTuesday(db.dayDao().getAverageRatingPerDayOfTheWeekAndYear(Calendar.TUESDAY, calendar.get(Calendar.YEAR)))
                            .setAvgWednesday(db.dayDao().getAverageRatingPerDayOfTheWeekAndYear(Calendar.WEDNESDAY, calendar.get(Calendar.YEAR)))
                            .setAvgThursday(db.dayDao().getAverageRatingPerDayOfTheWeekAndYear(Calendar.THURSDAY, calendar.get(Calendar.YEAR)))
                            .setAvgFriday(db.dayDao().getAverageRatingPerDayOfTheWeekAndYear(Calendar.FRIDAY, calendar.get(Calendar.YEAR)))
                            .setAvgSaturday(db.dayDao().getAverageRatingPerDayOfTheWeekAndYear(Calendar.SATURDAY, calendar.get(Calendar.YEAR)))
                            .setAvgSunday(db.dayDao().getAverageRatingPerDayOfTheWeekAndYear(Calendar.SUNDAY, calendar.get(Calendar.YEAR)))
                            .init();
                    stats.setStatisticsAdds(adds);
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
                    StatisticsAdds adds = new StatisticsAdds();
                    adds.setNumDayRated1(db.dayDao().getNumberOfDayByRateByYear(1, calendar.get(Calendar.YEAR)-1))
                            .setNumDayRated2(db.dayDao().getNumberOfDayByRateByYear(2, calendar.get(Calendar.YEAR)-1))
                            .setNumDayRated3(db.dayDao().getNumberOfDayByRateByYear(3, calendar.get(Calendar.YEAR)-1))
                            .setNumDayRated4(db.dayDao().getNumberOfDayByRateByYear(4, calendar.get(Calendar.YEAR)-1))
                            .setNumDayRated5(db.dayDao().getNumberOfDayByRateByYear(5, calendar.get(Calendar.YEAR)-1))
                            .setAverageDay(db.dayDao().getAverageDayByYear(calendar.get(Calendar.YEAR)-1))
                            .setNumberOfRatedDays(db.dayDao().getNumberOfDaysByYear(calendar.get(Calendar.YEAR)-1))
                            .setAvgMonday(db.dayDao().getAverageRatingPerDayOfTheWeekAndYear(Calendar.MONDAY, calendar.get(Calendar.YEAR)-1))
                            .setAvgTuesday(db.dayDao().getAverageRatingPerDayOfTheWeekAndYear(Calendar.TUESDAY, calendar.get(Calendar.YEAR)-1))
                            .setAvgWednesday(db.dayDao().getAverageRatingPerDayOfTheWeekAndYear(Calendar.WEDNESDAY, calendar.get(Calendar.YEAR)-1))
                            .setAvgThursday(db.dayDao().getAverageRatingPerDayOfTheWeekAndYear(Calendar.THURSDAY, calendar.get(Calendar.YEAR)-1))
                            .setAvgFriday(db.dayDao().getAverageRatingPerDayOfTheWeekAndYear(Calendar.FRIDAY, calendar.get(Calendar.YEAR)-1))
                            .setAvgSaturday(db.dayDao().getAverageRatingPerDayOfTheWeekAndYear(Calendar.SATURDAY, calendar.get(Calendar.YEAR)-1))
                            .setAvgSunday(db.dayDao().getAverageRatingPerDayOfTheWeekAndYear(Calendar.SUNDAY, calendar.get(Calendar.YEAR)-1))
                            .init();
                    stats.setStatisticsAdds(adds);
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

                //Set the object to send
                int nbDays = db.dayDao().getNumberOfDays();
                if(nbDays > 30){
                    Message msg_rating = Message.obtain();
                    msg_rating.what = MSG_ID.TROPHY.ordinal();
                    msg_rating.obj = nbDays;
                    handler.sendMessage(msg_rating);
                }
                Statistics stats = new Statistics();
                StatisticsAdds adds = new StatisticsAdds();
                adds.setNumDayRated1(db.dayDao().getNumberOfDayByRate(1))
                        .setNumDayRated2(db.dayDao().getNumberOfDayByRate(2))
                        .setNumDayRated3(db.dayDao().getNumberOfDayByRate(3))
                        .setNumDayRated4(db.dayDao().getNumberOfDayByRate(4))
                        .setNumDayRated5(db.dayDao().getNumberOfDayByRate(5))
                        .setAverageDay(db.dayDao().getAverageDay())
                        .setNumberOfRatedDays(nbDays)
                        .setAvgMonday(db.dayDao().getAverageRatingPerDayOfTheWeekAllTime(Calendar.MONDAY))
                        .setAvgTuesday(db.dayDao().getAverageRatingPerDayOfTheWeekAllTime(Calendar.TUESDAY))
                        .setAvgWednesday(db.dayDao().getAverageRatingPerDayOfTheWeekAllTime(Calendar.WEDNESDAY))
                        .setAvgThursday(db.dayDao().getAverageRatingPerDayOfTheWeekAllTime(Calendar.THURSDAY))
                        .setAvgFriday(db.dayDao().getAverageRatingPerDayOfTheWeekAllTime(Calendar.FRIDAY))
                        .setAvgSaturday(db.dayDao().getAverageRatingPerDayOfTheWeekAllTime(Calendar.SATURDAY))
                        .setAvgSunday(db.dayDao().getAverageRatingPerDayOfTheWeekAllTime(Calendar.SUNDAY))
                        .init();
                stats.setStatisticsAdds(adds);
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
        }.

                start();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
