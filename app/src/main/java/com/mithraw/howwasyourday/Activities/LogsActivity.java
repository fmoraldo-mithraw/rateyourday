package com.mithraw.howwasyourday.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.mithraw.howwasyourday.R;
import com.mithraw.howwasyourday.Tools.LogsAdapter;
import com.mithraw.howwasyourday.databases.Day;
import com.mithraw.howwasyourday.databases.DaysDatabase;

import java.util.List;

public class LogsActivity extends AppCompatActivity {
    public enum MSG_ID {
        DAYS_RECEIVED
    }

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private LogsAdapter mAdapter;
    private Handler handler;
    private Context mContext;
    private AppCompatActivity mActivity;

    protected DaysDatabase db;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        mContext = this;
        mActivity = this;
        //Get the DB
        db = DaysDatabase.getInstance(getApplicationContext());
        //Add the back button to the actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        // TODO initialize my dataset

        //Initialize the recyclerView things
        mRecyclerView.setHasFixedSize(true);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_ID.DAYS_RECEIVED.ordinal()) {
                    List<Day> myDataset = (List<Day>) msg.obj;
                    // use a linear layout manager
                    mLayoutManager = new LinearLayoutManager(mContext);
                    mRecyclerView.setLayoutManager(mLayoutManager);

                    // specify an adapter (see also next example)
                    mAdapter = new LogsAdapter(myDataset, mActivity);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread() {
            @Override
            public void run() {
                List<Day> days = db.dayDao().getAll();
                Message msg_rating = Message.obtain();
                msg_rating.what = MSG_ID.DAYS_RECEIVED.ordinal();
                msg_rating.obj = days;
                handler.sendMessage(msg_rating);
            }
        }.start();
    }

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
