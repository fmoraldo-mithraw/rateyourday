package com.mithraw.howwasyourday.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mithraw.howwasyourday.R;

/*
Show an expanded view of the Day
 */
public class ExpandedDayActivity extends AppCompatActivity {
    public final static String EXTRA_PARAM_DATE = "ExpandedDayActivity_Date";
    public final static String EXTRA_PARAM_RATE = "ExpandedDayActivity_Rate";
    public final static String EXTRA_PARAM_TITLE = "ExpandedDayActivity_Title";
    public final static String EXTRA_PARAM_LOG = "ExpandedDayActivity_Log";

    // View name for the transition
    public final static String VIEW_NAME = "cardview";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expanded_day);
        //Add the back button to the activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CardView cv = findViewById(R.id.cardViewExpand);
        // Retreive the informations of the date from the main activity and fill the calendar with them
        Intent intent = getIntent();
        ((TextView)findViewById(R.id.dateTextView)).setText(intent.getStringExtra(EXTRA_PARAM_DATE));
        TextView titleTextView = findViewById(R.id.titleText);
        String title = intent.getStringExtra(EXTRA_PARAM_TITLE);
        if (title.equals(""))
            titleTextView.setVisibility(View.GONE);
        else
            titleTextView.setText(title);
        TextView logTextView = findViewById(R.id.logText);
        String log = intent.getStringExtra(EXTRA_PARAM_LOG);
        if (title.equals(""))
            logTextView.setVisibility(View.GONE);
        else
            logTextView.setText(log);
        ((RatingBar)findViewById(R.id.ratingBar)).setRating(intent.getFloatExtra(EXTRA_PARAM_RATE,0));
        ViewCompat.setTransitionName(cv, VIEW_NAME);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return true;
    }
}
