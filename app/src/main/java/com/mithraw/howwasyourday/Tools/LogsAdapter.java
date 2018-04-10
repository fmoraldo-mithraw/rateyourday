package com.mithraw.howwasyourday.Tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mithraw.howwasyourday.Activities.RateADay;
import com.mithraw.howwasyourday.App;
import com.mithraw.howwasyourday.R;
import com.mithraw.howwasyourday.databases.Day;
import com.mithraw.howwasyourday.databases.DaysDatabase;

import java.util.Calendar;
import java.util.List;


public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private List<Day> mDataSet;
    private Activity mParent;
    static Handler handler;
    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public enum MSG_ID {REMOVE_LOG}

        TextView logCardDate;
        RatingBar logCardRating;
        TextView logCardTitle;
        TextView logCardText;
        Day mDay;
        Button btnEdit;
        Button btnRemove;
        Activity mParent;
        View mView;
        int mPosition;
        LinearLayout btnLayout;
        boolean isExtented = false;
        View logCardDividerText;


        public ViewHolder(View v, Activity parent) {
            super(v);
            mView = v;
            mParent = parent;
            isExtented = false;
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isExtented = !isExtented;
                    setBtnLayoutExtented(isExtented);
                }
            });
            logCardDate = (TextView) v.findViewById(R.id.log_card_date);
            logCardRating = (RatingBar) v.findViewById(R.id.log_card_rating);
            logCardTitle = (TextView) v.findViewById(R.id.log_card_title);
            logCardText = (TextView) v.findViewById(R.id.log_card_text);
            btnLayout = (LinearLayout) v.findViewById(R.id.button_layout);
            logCardDividerText = (View) v.findViewById(R.id.log_card_divider_text);

            btnEdit = (Button) v.findViewById(R.id.log_card_button_edit);
            btnRemove = (Button) v.findViewById(R.id.log_card_button_remove);
            //TODO set onClick methods

        }

        public View getLogCardDividerText() {
            return logCardDividerText;
        }

        public TextView getLogCardDate() {
            return logCardDate;
        }

        public RatingBar getLogCardRating() {
            return logCardRating;
        }

        public TextView getLogCardTitle() {
            return logCardTitle;
        }

        public TextView getLogCardText() {
            return logCardText;
        }

        public void setBtnLayoutExtented(boolean extented) {
            isExtented = extented;
            if (isExtented)
                btnLayout.setVisibility(View.VISIBLE);
            else
                btnLayout.setVisibility(View.GONE);
        }

        public void setPosition(int position) {
            mPosition = position;
        }

        public void setDay(final Day day) {
            mDay = day;
            btnEdit.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent rateADayIntent = new Intent(App.getContext(), RateADay.class);
                    // Send the date informations to the next activity
                    rateADayIntent.putExtra(RateADay.EXTRA_DATE_DAY, day.getDay());
                    rateADayIntent.putExtra(RateADay.EXTRA_DATE_MONTH, day.getMonth());
                    rateADayIntent.putExtra(RateADay.EXTRA_DATE_YEAR, day.getYear());
                    mParent.startActivity(rateADayIntent);

                }
            });
            btnRemove.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                    alertDialogBuilder.setMessage(R.string.log_removed)
                            .setTitle(R.string.log_removed_title);
                    alertDialogBuilder.setPositiveButton(R.string.log_removed_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            new Thread() {
                                @Override
                                public void run() {
                                    DaysDatabase db = DaysDatabase.getInstance(App.getApplication().getApplicationContext());
                                    db.dayDao().delete(day);
                                    Message msg_rating = Message.obtain();
                                    msg_rating.what = MSG_ID.REMOVE_LOG.ordinal();
                                    msg_rating.obj = mPosition;
                                    handler.sendMessage(msg_rating);
                                }
                            }.start();
                        }
                    });
                    alertDialogBuilder.setNegativeButton(R.string.log_removed_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    AlertDialog dialog = alertDialogBuilder.create();
                    dialog.show();


                }
            });
        }

    }

    public void removeElement(int position) {
        mDataSet.remove(position);
        notifyDataSetChanged();
        notifyItemRangeChanged(position, mDataSet.size());
    }

    public void updateDataSet(List<Day> mDataSet) {
        this.mDataSet = mDataSet;
        notifyDataSetChanged();
    }
    // END_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public LogsAdapter(List<Day> dataSet, Activity parent) {
        mDataSet = dataSet;
        mParent = parent;
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @SuppressLint("HandlerLeak")
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == ViewHolder.MSG_ID.REMOVE_LOG.ordinal()) {
                    int position = (int) msg.obj;
                    removeElement(position);
                }
            }
        };

        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_log_cardview, viewGroup, false);

        return new ViewHolder(v, mParent);
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        //TODO Initialize my CARD
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, mDataSet.get(position).getDay());
        cal.set(Calendar.MONTH, mDataSet.get(position).getMonth());
        cal.set(Calendar.YEAR, mDataSet.get(position).getYear());
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(App.getApplication());
        java.util.Date d = new java.util.Date(cal.getTimeInMillis());
        viewHolder.getLogCardDate().setText(dateFormat.format(d));
        viewHolder.getLogCardRating().setRating(mDataSet.get(position).getRating());
        String logText = mDataSet.get(position).getLog();
        if ("".equals(logText)) {
            viewHolder.getLogCardDividerText().setVisibility(View.GONE);
            viewHolder.getLogCardText().setVisibility(View.GONE);
        } else {
            viewHolder.getLogCardText().setText(logText);
            viewHolder.getLogCardDividerText().setVisibility(View.VISIBLE);
            viewHolder.getLogCardText().setVisibility(View.VISIBLE);
        }
        viewHolder.getLogCardTitle().setText(mDataSet.get(position).getTitleText());
        viewHolder.setBtnLayoutExtented(false);
        viewHolder.setDay(mDataSet.get(position));
        viewHolder.setPosition(position);
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}

