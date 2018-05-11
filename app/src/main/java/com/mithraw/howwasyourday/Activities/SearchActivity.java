package com.mithraw.howwasyourday.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.view.View;

import com.mithraw.howwasyourday.R;
import com.mithraw.howwasyourday.Tools.LogsAdapter;
import com.mithraw.howwasyourday.databases.Day;
import com.mithraw.howwasyourday.databases.DaysDatabase;

import java.util.List;

public class SearchActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;

    enum MSG_ID {QUERY_END}

    Handler handler;
    private LogsAdapter mAdapter = null;
    protected List<Day> mDataSet = null;
    private AppCompatActivity mActivity;
    DaysDatabase db;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //Add the back button to the activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = DaysDatabase.getInstance(this);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.search_reclycler_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //Initialize the recyclerView things (throught the handler)
        mRecyclerView.setHasFixedSize(true);
        mActivity = this;
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_ID.QUERY_END.ordinal()) {
                    mDataSet = (List<Day>) msg.obj;
                    if (mAdapter == null) {
                        mAdapter = new LogsAdapter(mDataSet, mActivity);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                    // use a linear layout manager
                    mAdapter.updateDataSet(mDataSet);
                }
            }
        };
        final SearchView searchView = findViewById(R.id.search);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                updateDataSet(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 3) {
                    updateDataSet(newText);
                }
                return false;
            }
        });
    }

    private void updateDataSet(final String query) {
        new Thread() {
            @Override
            public void run() {
                List<Day> days = db.dayDao().search(query);
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
