package com.mithraw.howwasyourday.Helpers;

import android.view.View;
import android.widget.ToggleButton;

import com.mithraw.howwasyourday.R;

/*
Manage the way the rateview layout behave
 */
public class RateViewHelper {
    private int mRating = 0;
    private View mView;

    public int getRating() {
        return mRating;
    }

    public void setRating(int mRating) {
        this.mRating = mRating;
        star1.setChecked(false);
        star2.setChecked(false);
        star3.setChecked(false);
        star4.setChecked(false);
        star5.setChecked(false);
        if (mRating > 0)
            star1.setChecked(true);
        if (mRating > 1)
            star2.setChecked(true);
        if (mRating > 2)
            star3.setChecked(true);
        if (mRating > 3)
            star4.setChecked(true);
        if (mRating > 4)
            star5.setChecked(true);
    }

    ToggleButton star1;
    ToggleButton star2;
    ToggleButton star3;
    ToggleButton star4;
    ToggleButton star5;
    View.OnClickListener _wrappedOnClickListener;

    public RateViewHelper(View view) {
        mView = view;
        star1 = view.findViewById(R.id.star1);
        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRating = 1;
                star1.setChecked(true);
                star2.setChecked(false);
                star3.setChecked(false);
                star4.setChecked(false);
                star5.setChecked(false);
                if (_wrappedOnClickListener != null)
                    _wrappedOnClickListener.onClick(v);
            }
        });
        star2 = view.findViewById(R.id.star2);
        star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRating = 2;
                star1.setChecked(true);
                star2.setChecked(true);
                star3.setChecked(false);
                star4.setChecked(false);
                star5.setChecked(false);
                if (_wrappedOnClickListener != null)
                    _wrappedOnClickListener.onClick(v);
            }
        });
        star3 = view.findViewById(R.id.star3);
        star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRating = 3;
                star1.setChecked(true);
                star2.setChecked(true);
                star3.setChecked(true);
                star4.setChecked(false);
                star5.setChecked(false);
                if (_wrappedOnClickListener != null)
                    _wrappedOnClickListener.onClick(v);
            }
        });
        star4 = view.findViewById(R.id.star4);
        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRating = 4;
                star1.setChecked(true);
                star2.setChecked(true);
                star3.setChecked(true);
                star4.setChecked(true);
                star5.setChecked(false);
                if (_wrappedOnClickListener != null)
                    _wrappedOnClickListener.onClick(v);
            }
        });
        star5 = view.findViewById(R.id.star5);
        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRating = 5;
                star1.setChecked(true);
                star2.setChecked(true);
                star3.setChecked(true);
                star4.setChecked(true);
                star5.setChecked(true);
                if (_wrappedOnClickListener != null)
                    _wrappedOnClickListener.onClick(v);
            }
        });
    }

    public void setOnRateChanged(View.OnClickListener wrappedOnClickListener) {
        _wrappedOnClickListener = wrappedOnClickListener;
    }

}
