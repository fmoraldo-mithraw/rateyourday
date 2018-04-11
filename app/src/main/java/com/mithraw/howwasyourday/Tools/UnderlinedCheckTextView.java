package com.mithraw.howwasyourday.Tools;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;


public class UnderlinedCheckTextView extends AppCompatTextView implements View.OnClickListener {

    boolean isChecked = true;


    public UnderlinedCheckTextView(Context context) {
        super(context);
        super.setOnClickListener(this);
        setPaintFlags(getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public UnderlinedCheckTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        super.setOnClickListener(this);
        setPaintFlags(getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public UnderlinedCheckTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setOnClickListener(this);
        setPaintFlags(getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
        if (isChecked) {
            setPaintFlags(getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            setTypeface(Typeface.create(getTypeface(), Typeface.BOLD), Typeface.BOLD);
        }
        else {
            setPaintFlags(getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
            setTypeface(Typeface.create(getTypeface(), Typeface.NORMAL), Typeface.NORMAL);
        }
    }
    OnClickListener _wrappedOnClickListener;
    @Override
    public void onClick(View v) {
        setChecked(!isChecked());
        if (_wrappedOnClickListener != null)
            _wrappedOnClickListener.onClick(v);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        _wrappedOnClickListener = l;
    }
}
