package com.mithraw.howwasyourday.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.mithraw.howwasyourday.App;
import com.mithraw.howwasyourday.R;
import com.mithraw.howwasyourday.Tools.LogsAdapter;
import com.mithraw.howwasyourday.Tools.SwipeableRecyclerViewTouchListener;
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
    private static Handler handler;
    private Context mContext;
    private Day mLastDayRemoved = null;
    private int mLastPositionRemoved = 0;
    private AppCompatActivity mActivity;
    protected List<Day> mDataset = null;
    protected DaysDatabase db;

    public class UndoListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            mDataset.add(mLastPositionRemoved,mLastDayRemoved);
            mAdapter.notifyDataSetChanged();
            new Thread() {
                @Override
                public void run() {
                    db.dayDao().insertDay(mLastDayRemoved);
                }
            }.start();
        }
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        // Init inner variables
        mContext = this;
        mActivity = this;


        //Get the DB
        db = DaysDatabase.getInstance(getApplicationContext());

        //Add the back button to the actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Init the recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
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
                                Snackbar.make(getCurrentFocus(), res.getString(R.string.entry_removed), 10000).setAction(R.string.undo, new UndoListener()).show();
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
                if (msg.what == MSG_ID.DAYS_RECEIVED.ordinal()) {
                    mDataset = (List<Day>) msg.obj;
                    // use a linear layout manager
                    mLayoutManager = new LinearLayoutManager(mContext);
                    mRecyclerView.setLayoutManager(mLayoutManager);

                    // specify an adapter (see also next example)
                    mAdapter = new LogsAdapter(mDataset, mActivity);
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
                //Retreive all days and send them to the adapter throught messages
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
