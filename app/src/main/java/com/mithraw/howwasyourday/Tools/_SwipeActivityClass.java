package com.mithraw.howwasyourday.Tools;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;

public abstract class _SwipeActivityClass extends AppCompatActivity
{
    private static final int SWIPE_X_MIN_DISTANCE = 120;
    private static final int SWIPE_Y_MIN_DISTANCE = 250;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        gestureDetector = new GestureDetector( this, new SwipeDetector());
    }

    protected abstract void onSwipeRight();
    protected abstract void onSwipeLeft();
    protected abstract void onSwipeUp();
    protected abstract void onSwipeDown();

    public class SwipeDetector extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
            {
                if (e2.getY() > e1.getY()) {
                    if (e2.getY() - e1.getY() > SWIPE_Y_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                        onSwipeDown();
                        return true;
                    }
                }

                if (e1.getY() > e2.getY()) {
                    if (e1.getY() - e2.getY() > SWIPE_Y_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                        onSwipeUp();
                        return true;
                    }
                }
            }else {
                if (e2.getX() > e1.getX()) {
                    if (e2.getX() - e1.getX() > SWIPE_X_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        onSwipeRight();
                        return true;
                    }
                }
                if (e1.getX() > e2.getX()) {
                    if (e1.getX() - e2.getX() > SWIPE_X_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        onSwipeLeft();
                        return true;
                    }
                }
            }
            return false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        // TouchEvent dispatcher.
        if (gestureDetector != null)
        {
            if (gestureDetector.onTouchEvent(ev))
                // If the gestureDetector handles the event, a swipe has been
                // executed and no more needs to be done.
                return true;
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return gestureDetector.onTouchEvent(event);
    }
}
