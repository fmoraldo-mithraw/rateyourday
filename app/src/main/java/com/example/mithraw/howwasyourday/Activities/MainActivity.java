package com.example.mithraw.howwasyourday.Activities;

import android.annotation.SuppressLint;



import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;

import com.example.mithraw.howwasyourday.Helpers.NotificationHelper;
import com.example.mithraw.howwasyourday.R;
import com.example.mithraw.howwasyourday.databases.Day;
import com.example.mithraw.howwasyourday.databases.DaysDatabase;

import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private enum MSG_ID {MSG_RATING, MSG_TITLE, MSG_LOG, MSG_EMPTY, MSG_SENT}
    private enum ACTIVITY_ID {ACTIVITY_RATE_A_DAY, ACTIVITY_SETTINGS}
    public static final String EXTRA_DATE_DAY = "extra_date_day";
    public static final String EXTRA_DATE_MONTH = "extra_date_month";
    public static final String EXTRA_DATE_YEAR = "extra_date_year";
    private static Context mContext;
    private DatePickerDialog datePickerDialog = null;
    protected DaysDatabase db;
    protected static Handler handler;
    protected final java.util.Calendar m_calendar = java.util.Calendar.getInstance();
    private ShareActionProvider mShareActionProvider = null;
    private Intent shareIntent;

    public static Context getContext(){
        return mContext;
    }
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        // Setup the notifications
        NotificationHelper.buildChannel();
        NotificationHelper.setupNotificationStatus();

        //Setup the database
        db = DaysDatabase.getInstance(getApplicationContext());

        //Setup the sharing intent
        shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        //Setup the ActionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        //Setup the navigation view (whatever it is)
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Setup the thread message handler
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                RatingBar rab = (RatingBar) findViewById(R.id.ratingBar);
                EditText titleText = (EditText) findViewById(R.id.titleText);
                EditText logText = (EditText) findViewById(R.id.logText);
                EditText edittext = (EditText) findViewById(R.id.dateTextView);
                if (msg.what == MSG_ID.MSG_RATING.ordinal()) {
                    rab.setRating((Integer) (msg.obj));
                } else if (msg.what == MSG_ID.MSG_LOG.ordinal()) {
                    logText.setText((String) (msg.obj));
                } else if (msg.what == MSG_ID.MSG_TITLE.ordinal()) {
                    titleText.setText((String) (msg.obj));
                }
                if (msg.what == MSG_ID.MSG_EMPTY.ordinal()) {
                    rab.setRating(0);
                    logText.setText("");
                    titleText.setText("");
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, "");
                    if (mShareActionProvider != null)
                        mShareActionProvider.setShareIntent(sharingIntent);
                    launchActivityRateADay();
                }
                if (msg.what == MSG_ID.MSG_SENT.ordinal()) {
                    String title = edittext.getText().toString() + " - " + titleText.getText().toString();
                    String shareBody = title + "\n" + (int) (rab.getRating()) + "/5\n" + logText.getText().toString();
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    if (mShareActionProvider != null)
                        mShareActionProvider.setShareIntent(sharingIntent);
                }
            }
        };

        //Setup the Edit button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivityRateADay();
            }
        });

        //Setup the main controls
        //  The Rating Bar
        RatingBar rab = (RatingBar) findViewById(R.id.ratingBar);
        rab.setRating(0);
        rab.setMax(5);

        //  The the date controler
        EditText edittext = (EditText) findViewById(R.id.dateTextView);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                m_calendar.set(java.util.Calendar.YEAR, year);
                m_calendar.set(java.util.Calendar.MONTH, monthOfYear);
                m_calendar.set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
        edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Show the date picker, we configure it here
                DatePickerDialog datePickerDialog = getDatePickerDialog();
                if (datePickerDialog != null) {
                    datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTime().getTime());
                    datePickerDialog.show();
                }
                // Hide the keyboard
                View focused = getCurrentFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(focused.getWindowToken(), 0);
            }
        });


        //Create the DatePicker
        datePickerDialog = new DatePickerDialog(MainActivity.this, date, m_calendar
                .get(java.util.Calendar.YEAR), m_calendar.get(java.util.Calendar.MONTH),
                m_calendar.get(java.util.Calendar.DAY_OF_MONTH));

    }

    private void updateLabel() {
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        EditText edittext = (EditText) findViewById(R.id.dateTextView);
        edittext.setText(dateFormat.format(m_calendar.getTime()));
        //Fill the controls with the correct infos
        fillTheInformations(false);
    }

    private void launchActivityRateADay() {
        Intent rateADayIntent = new Intent(getApplicationContext(), RateADay.class);
        // Send the date informations to the next activity
        rateADayIntent.putExtra(EXTRA_DATE_DAY, m_calendar.get(java.util.Calendar.DAY_OF_MONTH));
        rateADayIntent.putExtra(EXTRA_DATE_MONTH, m_calendar.get(java.util.Calendar.MONTH));
        rateADayIntent.putExtra(EXTRA_DATE_YEAR, m_calendar.get(java.util.Calendar.YEAR));
        startActivityForResult(rateADayIntent, ACTIVITY_ID.ACTIVITY_RATE_A_DAY.ordinal());
    }

    private void launchActivitySettings() {
        Intent settingIntent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivityForResult(settingIntent, ACTIVITY_ID.ACTIVITY_SETTINGS.ordinal());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == ACTIVITY_ID.ACTIVITY_RATE_A_DAY.ordinal()) {
            // Make sure the request was successful
            if (resultCode != 0) {
                Resources res = getResources();
                if (resultCode > 2)
                    Snackbar.make(getCurrentFocus(), res.getString(R.string.save_the_day_good), 5000).show();
                else
                    Snackbar.make(getCurrentFocus(), res.getString(R.string.save_the_day_bad), 5000).show();
            }
        } else if(requestCode == ACTIVITY_ID.ACTIVITY_SETTINGS.ordinal()) {
            //TODO Make something
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
        getMenuInflater().inflate(R.menu.main, menu);
        getMenuInflater().inflate(R.menu.share_button, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        //Initialize the labels
        updateLabel();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            launchActivitySettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {
            launchActivitySettings();
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
        EditText titleText = (EditText) findViewById(R.id.titleText);
        titleText.setEnabled(false);
        EditText logText = (EditText) findViewById(R.id.logText);
        logText.setEnabled(false);
        fillTheInformations(true);
    }

    protected void fillTheInformations(final boolean isResume) {
        new Thread() {
            @Override
            public void run() {
                RatingBar rab = (RatingBar) findViewById(R.id.ratingBar);
                EditText titleText = (EditText) findViewById(R.id.titleText);
                EditText logText = (EditText) findViewById(R.id.logText);
                List<Day> days = db.dayDao().loadAllByDate(m_calendar.get(java.util.Calendar.DAY_OF_MONTH), m_calendar.get(java.util.Calendar.MONTH), m_calendar.get(java.util.Calendar.YEAR));
                if (days.isEmpty()) {
                    if (isResume == false)
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
                    handler.sendEmptyMessage(MSG_ID.MSG_SENT.ordinal());
                }
            }
        }.start();
    }

    public DatePickerDialog getDatePickerDialog() {
        return datePickerDialog;
    }
}
