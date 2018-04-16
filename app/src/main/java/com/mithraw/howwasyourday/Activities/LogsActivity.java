package com.mithraw.howwasyourday.Activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mithraw.howwasyourday.App;
import com.mithraw.howwasyourday.Dialogs.TipsDialog;
import com.mithraw.howwasyourday.R;
import com.mithraw.howwasyourday.Tools.LogsAdapter;
import com.mithraw.howwasyourday.Tools.SwipeableRecyclerViewTouchListener;
import com.mithraw.howwasyourday.Tools.UnderlinedCheckTextView;
import com.mithraw.howwasyourday.databases.Day;
import com.mithraw.howwasyourday.databases.DaysDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/*
Show the logs of days with filters showed on the action
 */
public class LogsActivity extends AppCompatActivity {
    public enum MSG_ID {
        DAYS_RECEIVED,
        QUERY_START,
        QUERY_END
    }

    private enum TYPE_DATE {START_DATE, END_DATE}

    private DatePickerDialog startDatePickerDialog = null;
    private DatePickerDialog endDatePickerDialog = null;
    private Date startDate;
    private Date endDate;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private LogsAdapter mAdapter = null;
    private static Handler handler;
    private Context mContext;
    private Day mLastDayRemoved = null;
    private int mLastPositionRemoved = 0;
    private AppCompatActivity mActivity;
    protected List<Day> mDataset = null;
    protected DaysDatabase db;
    LinearLayout mLayout;
    ProgressBar mProgressBar;

    public class UndoListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            mDataset.add(mLastPositionRemoved, mLastDayRemoved);
            mAdapter.notifyDataSetChanged();
            new Thread() {
                @Override
                public void run() {
                    db.dayDao().insertDay(mLastDayRemoved);
                }
            }.start();
        }
    }

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


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Init inner variables
        mContext = this;
        mActivity = this;
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(mLayoutManager);

        String preferenceName = "tip_logs_showed";
        //PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(preferenceName, false).apply(); // TODO to remove outside tests
        if ((!(PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean(preferenceName, false)))&&
                (PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean("show_tips", true))){
            ArrayList<Integer> iList = new ArrayList<Integer>();
            iList.add(R.layout.tips_fragment_logs_top_screen);
            iList.add(R.layout.tips_fragment_logs_cards);
            Bundle bundl = new Bundle();
            bundl.putIntegerArrayList("listView", iList);
            bundl.putString("preference", preferenceName);
            bundl.putInt("title", R.string.tips_logs_title);
            DialogFragment newFragment = new TipsDialog();
            newFragment.setArguments(bundl);
            newFragment.show(getSupportFragmentManager(), preferenceName);
        }

        //Get the DB
        db = DaysDatabase.getInstance(getApplicationContext());

        //Add the back button to the actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configure the progressbar
        mProgressBar = findViewById(R.id.progress_bar);
        //Configure the scrollAppBar
        mLayout = findViewById(R.id.layout_scroll_app_bar);
        AppBarLayout mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    mLayout.setVisibility(View.GONE);

                } else {
                    mLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        //Init the recycler view
        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(mRecyclerView,
                        new SwipeableRecyclerViewTouchListener.SwipeListener() {

                            @Override
                            public boolean canSwipeLeft(int position) {
                                return true;
                            }

                            @Override
                            public boolean canSwipeRight(int position) {
                                return true;
                            }

                            private void removeItem(int[] reverseSortedPositions) {
                                Resources res = App.getApplication().getResources();
                                for (int position : reverseSortedPositions) {
                                    mLastDayRemoved = mDataset.get(position);
                                    mDataset.remove(position);
                                    mAdapter.notifyItemRemoved(position);
                                    mLastPositionRemoved = position;
                                    new Thread() {
                                        @Override
                                        public void run() {
                                            db.dayDao().delete(mLastDayRemoved);
                                        }
                                    }.start();
                                }
                                mAdapter.notifyDataSetChanged();
                                android.support.design.widget.CoordinatorLayout cl = findViewById(R.id.coordinator_log_view);
                                Snackbar.make(cl, res.getString(R.string.entry_removed), 10000).setAction(R.string.undo, new UndoListener()).show();
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                removeItem(reverseSortedPositions);
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                removeItem(reverseSortedPositions);
                            }
                        });
        mRecyclerView.addOnItemTouchListener(swipeTouchListener);

        //Initialize the recyclerView things (throught the handler)
        mRecyclerView.setHasFixedSize(true);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_ID.QUERY_START.ordinal()) {
                    showRecyclerView(false);
                }
                if (msg.what == MSG_ID.QUERY_END.ordinal()) {
                    mDataset = (List<Day>) msg.obj;
                    if(mAdapter == null){
                        mAdapter = new LogsAdapter(mDataset, mActivity);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                    // use a linear layout manager
                    mAdapter.updateDataSet(mDataset);
                    showRecyclerView(true);
                }
            }
        };

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

        //set daysListeners
        View.OnClickListener _wrappedOnClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                updateDataSet();
            }
        };
        ((UnderlinedCheckTextView)findViewById(R.id.checked_text_view_monday)).setOnClickListener(_wrappedOnClickListener);
        ((UnderlinedCheckTextView)findViewById(R.id.checked_text_view_tuesday)).setOnClickListener(_wrappedOnClickListener);
        ((UnderlinedCheckTextView)findViewById(R.id.checked_text_view_wednesday)).setOnClickListener(_wrappedOnClickListener);
        ((UnderlinedCheckTextView)findViewById(R.id.checked_text_view_thursday)).setOnClickListener(_wrappedOnClickListener);
        ((UnderlinedCheckTextView)findViewById(R.id.checked_text_view_friday)).setOnClickListener(_wrappedOnClickListener);
        ((UnderlinedCheckTextView)findViewById(R.id.checked_text_view_saturday)).setOnClickListener(_wrappedOnClickListener);
        ((UnderlinedCheckTextView)findViewById(R.id.checked_text_view_sunday)).setOnClickListener(_wrappedOnClickListener);
    }

    private void updateDataSet() {
        new Thread() {
            @Override
            public void run() {
                handler.sendEmptyMessage(MSG_ID.QUERY_START.ordinal());
                Calendar cal = Calendar.getInstance();
                cal.setTime(startDate);
                long startDateInt = cal.getTimeInMillis();
                Logger.getLogger("LogsAdapter").log(new LogRecord(Level.INFO, "FMORALDO : LogsAdapter date : " + startDateInt));
                cal.setTime(endDate);
                cal.add(Calendar.DAY_OF_MONTH,1);
                long endDateInt = cal.getTimeInMillis();

                boolean monday = ((UnderlinedCheckTextView) findViewById(R.id.checked_text_view_monday)).isChecked();
                boolean tuesday = ((UnderlinedCheckTextView) findViewById(R.id.checked_text_view_tuesday)).isChecked();
                boolean wednesday = ((UnderlinedCheckTextView) findViewById(R.id.checked_text_view_wednesday)).isChecked();
                boolean thursday = ((UnderlinedCheckTextView) findViewById(R.id.checked_text_view_thursday)).isChecked();
                boolean friday = ((UnderlinedCheckTextView) findViewById(R.id.checked_text_view_friday)).isChecked();
                boolean saturday = ((UnderlinedCheckTextView) findViewById(R.id.checked_text_view_saturday)).isChecked();
                boolean sunday = ((UnderlinedCheckTextView) findViewById(R.id.checked_text_view_sunday)).isChecked();
                int size = (monday ? 1 : 0) + (tuesday ? 1 : 0) + (wednesday ? 1 : 0) + (thursday ? 1 : 0) + (friday ? 1 : 0) + (saturday ? 1 : 0) + (sunday ? 1 : 0);
                int count = 0;
                int[] ids = new int[size];
                if (monday) ids[count++] = Calendar.MONDAY;
                if (tuesday) ids[count++] = Calendar.TUESDAY;
                if (wednesday) ids[count++] = Calendar.WEDNESDAY;
                if (thursday) ids[count++] = Calendar.THURSDAY;
                if (friday) ids[count++] = Calendar.FRIDAY;
                if (saturday) ids[count++] = Calendar.SATURDAY;
                if (sunday) ids[count++] = Calendar.SUNDAY;

                List<Day> days = db.dayDao().getByBoundsAndAdditions(startDateInt, endDateInt, ids);
                Message msg_rating = Message.obtain();
                msg_rating.what = MSG_ID.QUERY_END.ordinal();
                msg_rating.obj = days;
                handler.sendMessage(msg_rating);
            }
        }.start();
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

    private void showRecyclerView(boolean show) {
        if (show) {
            mProgressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDataSet();
    }

    public void onBackPressed() {
        finish();
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }

        return true;
    }
}
