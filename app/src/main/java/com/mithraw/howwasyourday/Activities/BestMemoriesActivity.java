package com.mithraw.howwasyourday.Activities;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.mithraw.howwasyourday.R;
import com.mithraw.howwasyourday.Tools.LogsAdapter;
import com.mithraw.howwasyourday.databases.Day;
import com.mithraw.howwasyourday.databases.DaysDatabase;

import java.util.Calendar;
import java.util.List;

public class BestMemoriesActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    enum MSG_ID {QUERY_END}
    Handler handler;
    private LogsAdapter mAdapter = null;
    protected List<Day> mDataset = null;
    private AppCompatActivity mActivity;
    DaysDatabase db;
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_memories);
        //Add the back button to the activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = DaysDatabase.getInstance(this);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.best_memories_reclycler_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //Initialize the recyclerView things (throught the handler)
        mRecyclerView.setHasFixedSize(true);
        mActivity = this;
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                CardView nothingInteresting = findViewById(R.id.card_view_nothing_interesting_best_memories);
                if (msg.what == MSG_ID.QUERY_END.ordinal()) {
                    mDataset = (List<Day>) msg.obj;
                    nothingInteresting.setVisibility(View.VISIBLE);
                    if(mDataset.size() > 0)
                        nothingInteresting.setVisibility(View.GONE);
                    if(mAdapter == null){
                        mAdapter = new LogsAdapter(mDataset, mActivity);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                    // use a linear layout manager
                    mAdapter.updateDataSet(mDataset);
                }
            }
        };
    }

    @Override
    public void onResume(){
        super.onResume();
        updateDataSet();
    }
    private void updateDataSet() {
        new Thread() {
            @Override
            public void run() {
                List<Day> days = db.dayDao().getAllGoodMemories();
                if((days == null)|| (days.size() == 0)){
                    days = db.dayDao().getAllGoodMemories4();
                }
                Message msg_rating = Message.obtain();
                msg_rating.what = MSG_ID.QUERY_END.ordinal();
                msg_rating.obj = days;
                handler.sendMessage(msg_rating);
            }
        }.start();
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
