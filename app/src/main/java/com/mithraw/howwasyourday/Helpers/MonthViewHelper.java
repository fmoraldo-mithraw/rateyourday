package com.mithraw.howwasyourday.Helpers;

import android.os.Build;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mithraw.howwasyourday.R;

import java.util.Locale;

/*
Month helper for diagrams
On click show the numbers
 */
public class MonthViewHelper {
    public enum MONTHS {JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER}

    private View mView;
    boolean mShowNumbers = false;
    String formatFloat = "%.1f";

    public MonthViewHelper(View view) {
        mView = view;
        updateVisibilityTexts();
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShowNumbers = !mShowNumbers;
                updateVisibilityTexts();
            }
        });
    }

    public void setTitle(String title) {
        TextView tv = mView.findViewById(R.id.title);
        tv.setText(title);

    }

    private void updateVisibilityTexts() {
        for (MONTHS d : MONTHS.values()) {
            if (mShowNumbers) {
                if (!getTextView(d).getText().equals(String.format(Locale.ROOT, formatFloat, (float) 0)))
                    getTextView(d).setVisibility(View.VISIBLE);
            } else
                getTextView(d).setVisibility(View.GONE);
        }
    }

    public void updateMonths(float jan, float feb, float mar, float apr, float may, float jun, float jul, float aug, float sep, float oct, float nov, float dec) {
        updateMonth(MONTHS.JANUARY, jan);
        updateMonth(MONTHS.FEBRUARY, feb);
        updateMonth(MONTHS.MARCH, mar);
        updateMonth(MONTHS.APRIL, apr);
        updateMonth(MONTHS.MAY, may);
        updateMonth(MONTHS.JUNE, jun);
        updateMonth(MONTHS.JULY, jul);
        updateMonth(MONTHS.AUGUST, aug);
        updateMonth(MONTHS.SEPTEMBER, sep);
        updateMonth(MONTHS.OCTOBER, oct);
        updateMonth(MONTHS.NOVEMBER, nov);
        updateMonth(MONTHS.DECEMBER, dec);

    }

    private ProgressBar getProgressBar(MONTHS d) {
        ProgressBar pb;
        switch (d) {
            case JANUARY:
                pb = (ProgressBar) mView.findViewById(R.id.vertical_progressbar_january);
                break;
            case FEBRUARY:
                pb = (ProgressBar) mView.findViewById(R.id.vertical_progressbar_february);
                break;
            case MARCH:
                pb = (ProgressBar) mView.findViewById(R.id.vertical_progressbar_march);
                break;
            case APRIL:
                pb = (ProgressBar) mView.findViewById(R.id.vertical_progressbar_april);
                break;
            case MAY:
                pb = (ProgressBar) mView.findViewById(R.id.vertical_progressbar_may);
                break;
            case JUNE:
                pb = (ProgressBar) mView.findViewById(R.id.vertical_progressbar_june);
                break;
            case JULY:
                pb = (ProgressBar) mView.findViewById(R.id.vertical_progressbar_july);
                break;
            case AUGUST:
                pb = (ProgressBar) mView.findViewById(R.id.vertical_progressbar_august);
                break;
            case SEPTEMBER:
                pb = (ProgressBar) mView.findViewById(R.id.vertical_progressbar_september);
                break;
            case OCTOBER:
                pb = (ProgressBar) mView.findViewById(R.id.vertical_progressbar_october);
                break;
            case NOVEMBER:
                pb = (ProgressBar) mView.findViewById(R.id.vertical_progressbar_november);
                break;
            case DECEMBER:
                pb = (ProgressBar) mView.findViewById(R.id.vertical_progressbar_december);
                break;
            default:
                pb = pb = (ProgressBar) mView.findViewById(R.id.vertical_progressbar_january);
        }
        return pb;
    }

    private TextView getTextView(MONTHS d) {
        TextView pb;
        switch (d) {
            case JANUARY:
                pb = (TextView) mView.findViewById(R.id.text_view_january);
                break;
            case FEBRUARY:
                pb = (TextView) mView.findViewById(R.id.text_view_february);
                break;
            case MARCH:
                pb = (TextView) mView.findViewById(R.id.text_view_march);
                break;
            case APRIL:
                pb = (TextView) mView.findViewById(R.id.text_view_april);
                break;
            case MAY:
                pb = (TextView) mView.findViewById(R.id.text_view_may);
                break;
            case JUNE:
                pb = (TextView) mView.findViewById(R.id.text_view_june);
                break;
            case JULY:
                pb = (TextView) mView.findViewById(R.id.text_view_july);
                break;
            case AUGUST:
                pb = (TextView) mView.findViewById(R.id.text_view_august);
                break;
            case SEPTEMBER:
                pb = (TextView) mView.findViewById(R.id.text_view_september);
                break;
            case OCTOBER:
                pb = (TextView) mView.findViewById(R.id.text_view_october);
                break;
            case NOVEMBER:
                pb = (TextView) mView.findViewById(R.id.text_view_november);
                break;
            case DECEMBER:
                pb = (TextView) mView.findViewById(R.id.text_view_december);
                break;
            default:
                pb = pb = (TextView) mView.findViewById(R.id.text_view_january);
        }
        return pb;
    }

    public void updateMonth(MONTHS d, float value) {
        updateMonth(getProgressBar(d), value);
        getTextView(d).setText(String.format(Locale.ROOT, formatFloat, value));
    }

    public void updateMonth(ProgressBar pb, float value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            pb.setProgress((int) (value * 20), true);
        else
            pb.setProgress((int) (value * 20));
    }
}
