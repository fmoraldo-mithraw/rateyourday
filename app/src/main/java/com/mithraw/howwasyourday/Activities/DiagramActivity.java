package com.mithraw.howwasyourday.Activities;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.mithraw.howwasyourday.R;

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

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagram);
        View view = findViewById(R.id.all_time_week);
        ProgressBar pb1 = view.findViewById(R.id.vertical_progressbar_monday);
        pb1.setProgress(50);
        ProgressBar pb2 = view.findViewById(R.id.vertical_progressbar_tuesday);
        pb2.setProgress(50);
        ProgressBar pb3 = view.findViewById(R.id.vertical_progressbar_wednesday);
        pb3.setProgress(50);
        ProgressBar pb4 = view.findViewById(R.id.vertical_progressbar_thursday);
        pb4.setProgress(50);
        ProgressBar pb5 = view.findViewById(R.id.vertical_progressbar_friday);
        pb5.setProgress(50);
        ProgressBar pb6 = view.findViewById(R.id.vertical_progressbar_saturday);
        pb6.setProgress(50);
        ProgressBar pb7 = view.findViewById(R.id.vertical_progressbar_sunday);
        pb7.setProgress(50);
        View view2 = findViewById(R.id.current_year_week);
        ProgressBar pb8 = view2.findViewById(R.id.vertical_progressbar_monday);
        pb8.setProgress(75);
        ProgressBar pb9 = view2.findViewById(R.id.vertical_progressbar_tuesday);
        pb9.setProgress(75);
        ProgressBar pb10 = view2.findViewById(R.id.vertical_progressbar_wednesday);
        pb10.setProgress(75);
        ProgressBar pb11 = view2.findViewById(R.id.vertical_progressbar_thursday);
        pb11.setProgress(75);
        ProgressBar pb12 = view2.findViewById(R.id.vertical_progressbar_friday);
        pb12.setProgress(75);
        ProgressBar pb13 = view2.findViewById(R.id.vertical_progressbar_saturday);
        pb13.setProgress(75);
        ProgressBar pb14 = view2.findViewById(R.id.vertical_progressbar_sunday);
        pb14.setProgress(75);

        /*handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ProgressBar progressBarMonday = (ProgressBar) findViewById(R.id.vertical_progressbar_monday);
                if (msg.what == MSG_ID.MONDAY_CURRENT_YEAR.ordinal()) {
                    progressBarMonday.setProgress((Integer) (msg.obj));
                }
            }
        };*/
    }
}
