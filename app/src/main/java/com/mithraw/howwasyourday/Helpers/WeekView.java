package com.mithraw.howwasyourday.Helpers;

import android.os.Build;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mithraw.howwasyourday.R;

public class WeekView {
    public enum DAYS{MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY}
    private View mView;
    public WeekView(View view) {
        mView = view;
    }
    public void setTitle(String title){
        TextView tv = mView.findViewById(R.id.title);
        tv.setText(title);
    }
    public void updateDays(float mon, float tue, float wed, float thu, float fri, float sat,float sun){
        updateDay(DAYS.MONDAY,mon);
        updateDay(DAYS.TUESDAY,tue);
        updateDay(DAYS.WEDNESDAY,wed);
        updateDay(DAYS.THURSDAY,thu);
        updateDay(DAYS.FRIDAY,fri);
        updateDay(DAYS.SATURDAY,sat);
        updateDay(DAYS.SUNDAY,sun);

    }
    private ProgressBar getProgressBar(DAYS d){
        ProgressBar pb;
        switch (d) {
            case MONDAY:
                pb = (ProgressBar)mView.findViewById(R.id.vertical_progressbar_monday);
                break;
            case TUESDAY:
                pb = (ProgressBar)mView.findViewById(R.id.vertical_progressbar_tuesday);
                break;
            case WEDNESDAY:
                pb = (ProgressBar)mView.findViewById(R.id.vertical_progressbar_wednesday);
                break;
            case THURSDAY:
                pb = (ProgressBar)mView.findViewById(R.id.vertical_progressbar_thursday);
                break;
            case FRIDAY:
                pb = (ProgressBar)mView.findViewById(R.id.vertical_progressbar_friday);
                break;
            case SATURDAY:
                pb = (ProgressBar)mView.findViewById(R.id.vertical_progressbar_saturday);
                break;
            case SUNDAY:
                pb = (ProgressBar)mView.findViewById(R.id.vertical_progressbar_sunday);
                break;
            default:
                pb = pb = (ProgressBar)mView.findViewById(R.id.vertical_progressbar_monday);
        }
        return pb;
    }
    public void updateDay(DAYS d, float value){
        updateDay(getProgressBar(d),value);
    }
    public void updateDay(ProgressBar pb, float value){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            pb.setProgress((int)(value*20),true);
        else
            pb.setProgress((int)(value*20));
    }
}
