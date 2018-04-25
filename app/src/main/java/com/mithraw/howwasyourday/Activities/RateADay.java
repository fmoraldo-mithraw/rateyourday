package com.mithraw.howwasyourday.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mithraw.howwasyourday.App;
import com.mithraw.howwasyourday.Dialogs.TipsDialog;
import com.mithraw.howwasyourday.Helpers.BitmapHelper;
import com.mithraw.howwasyourday.Helpers.RateViewHelper;
import com.mithraw.howwasyourday.R;
import com.mithraw.howwasyourday.Tools.Coordinate;
import com.mithraw.howwasyourday.Tools.MyInt;
import com.mithraw.howwasyourday.Tools.MyLocationManager;
import com.mithraw.howwasyourday.databases.Day;
import com.mithraw.howwasyourday.databases.DaysDatabase;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/*
RateADay Activity shows a screen that permit you to save informations on the day
Saved on a ratingView click or when returned
 */
public class RateADay extends AppCompatActivity {
    private enum MSG_ID {MSG_RATING, MSG_TITLE, MSG_LOG, MSG_EMPTY, MSG_LOCATION}

    private enum ACTIVITY_RESULTS {GALLERY_ID, CAMERA_ID, PLACE_ID}
    protected final java.util.Calendar m_calendar = java.util.Calendar.getInstance();
    protected DaysDatabase db;
    protected static Handler handler;
    public static final String EXTRA_DATE_DAY = "extra_date_day";
    public static final String EXTRA_DATE_MONTH = "extra_date_month";
    public static final String EXTRA_DATE_YEAR = "extra_date_year";
    EditText mTitleText;
    EditText mLogText;
    ImageButton mImageAdder;
    ImageButton mCameraAdder;
    ImageButton mPlaceAdder;
    RateViewHelper mRateView;
    int mFlagsTitle;
    int mFlagsLog;
    Bitmap mBitmap;
    MyInt[] arrayInt = {new MyInt(0)};
    LinearLayout mButtonsLinearLayout;
    int mHeight;
    MyLocationManager mLocManager;
    Coordinate mLastCoordinate = new Coordinate();
    private TextWatcher watcher = new TextWatcher() {
        private int spanLength = -1;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (start == 0) return;
            if (count > after) {
                ImageSpan[] spans = mLogText.getEditableText().getSpans(start + count, start + count, ImageSpan.class);
                if (spans == null || spans.length == 0) return;
                for (int i = 0; i < spans.length; i++) {
                    int end = mLogText.getEditableText().getSpanEnd(spans[i]);
                    if (end != start + count) continue;
                    String text = spans[i].getSource();
                    spanLength = text.length() - 1;
                    mLogText.getEditableText().removeSpan(spans[i]);
                }
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (spanLength > -1) {
                int length = spanLength;
                spanLength = -1;
                mLogText.getEditableText().replace(start - length, start, "");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };


    @SuppressLint({"HandlerLeak", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_aday);
        // Retreive the informations of the date from the main activity and fill the calendar with them
        // Acquire a reference to the system Location Manager

        Intent intent = getIntent();
        m_calendar.set(java.util.Calendar.DAY_OF_MONTH, intent.getIntExtra(EXTRA_DATE_DAY, 0));
        m_calendar.set(java.util.Calendar.MONTH, intent.getIntExtra(EXTRA_DATE_MONTH, 0));
        m_calendar.set(java.util.Calendar.YEAR, intent.getIntExtra(EXTRA_DATE_YEAR, 0));
        //Add the back button to the activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLocManager = new MyLocationManager(this);

        //Display tips
        String preferenceName = "tip_rate_showed";
        if ((!(PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean(preferenceName, false))) &&
                (PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean("show_tips", true))) {
            ArrayList<Integer> iList = new ArrayList<Integer>();
            iList.add(R.layout.tips_fragment_rate);
            Bundle bundl = new Bundle();
            bundl.putIntegerArrayList("listView", iList);
            bundl.putString("preference", preferenceName);
            bundl.putInt("title", R.string.tips_rate_title);
            DialogFragment newFragment = new TipsDialog();
            newFragment.setArguments(bundl);
            newFragment.show(getSupportFragmentManager(), preferenceName);
        }
        //Retrieve the database
        db = DaysDatabase.getInstance(getApplicationContext());
        mImageAdder = findViewById(R.id.image_adder);
        mCameraAdder = findViewById(R.id.camera_adder);
        mPlaceAdder = findViewById(R.id.place_adder);
        mTitleText = findViewById(R.id.titleTextRate);
        mFlagsTitle = mTitleText.getInputType();
        mLogText = findViewById(R.id.logTextRate);
        mLogText.addTextChangedListener(watcher);
        mFlagsLog = mLogText.getInputType();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_ID.MSG_RATING.ordinal()) {
                    mRateView.setRating((Integer) (msg.obj));
                    allowFocusOnTexts(true);
                } else if (msg.what == MSG_ID.MSG_LOG.ordinal()) {
                    mLogText.setText(BitmapHelper.parseStringWithBitmaps(m_calendar, (String) (msg.obj), arrayInt));
                    allowFocusOnTexts(true);

                } else if (msg.what == MSG_ID.MSG_TITLE.ordinal()) {
                    mTitleText.setText((String) (msg.obj));
                    allowFocusOnTexts(true);
                } else if (msg.what == MSG_ID.MSG_LOCATION.ordinal()) {
                    if ((msg.obj == null) || (((Coordinate) msg.obj).getLatitude() == 0) && (((Coordinate) msg.obj).getLongitude() == 0)) {
                        mLastCoordinate = new Coordinate();
                    } else {
                        mLastCoordinate = (Coordinate) msg.obj;
                    }
                }
                if (msg.what == MSG_ID.MSG_EMPTY.ordinal()) {
                    mTitleText.setText("");
                    mLogText.setText("");
                    mRateView.setRating(0);
                    mLastCoordinate = new Coordinate();
                    allowFocusOnTexts(false);
                }
            }
        };


        // Adjusting the controls of the page

        mTitleText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mRateView.getRating() == 0) {
                    ((EditText) v).setText("");
                    Toast.makeText(getBaseContext(), R.string.cant_click, Toast.LENGTH_LONG).show();
                } else {
                    allowFocusOnTexts(true);
                }

                return false;
            }
        });

        mLogText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mRateView.getRating() == 0) {
                    ((EditText) v).setText("");
                    Toast.makeText(getBaseContext(), R.string.cant_click, Toast.LENGTH_LONG).show();
                } else {
                    allowFocusOnTexts(true);
                }
                return false;
            }

        });


        TextView dateText = (TextView) findViewById(R.id.dateTextView);
        mRateView = new RateViewHelper((View) findViewById(R.id.ratingBar));
        mRateView.setOnRateChanged(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowFocusOnTexts(true);
                saveDay();
            }
        });
        allowFocusOnTexts(false);

        mImageAdder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
                galleryIntent.setType("image/*");
                galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(galleryIntent, ACTIVITY_RESULTS.GALLERY_ID.ordinal());
            }
        });
        mCameraAdder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, ACTIVITY_RESULTS.CAMERA_ID.ordinal());

            }
        });
        mPlaceAdder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent placeIntent = new Intent(getApplicationContext(),MapPickActivity.class);
                placeIntent.putExtra(MapPickActivity.EXTRA_PARAM_TITLE, mTitleText.getText().toString());
                placeIntent.putExtra(MapPickActivity.EXTRA_PARAM_LONGITUDE, mLastCoordinate.getLongitude());
                placeIntent.putExtra(MapPickActivity.EXTRA_PARAM_LATITUDE, mLastCoordinate.getLatitude());
                placeIntent.putExtra(MapPickActivity.EXTRA_PARAM_RATE, mRateView.getRating());
                startActivityForResult(placeIntent, ACTIVITY_RESULTS.PLACE_ID.ordinal());

            }
        });
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mHeight = displayMetrics.heightPixels;

        mButtonsLinearLayout = findViewById(R.id.linearLayout);
        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (mHeight <= 1280) {
                            if (isOpen) {
                                mButtonsLinearLayout.setVisibility(View.GONE);
                            } else {
                                mButtonsLinearLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
        //Fill the controls with the correct informations
        fillTheInformations();
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        dateText.setText(dateFormat.format(m_calendar.getTime()));
    }

    private void allowFocusOnTexts(boolean allow) {
        mImageAdder.setEnabled(allow);
        mCameraAdder.setEnabled(allow);
        mPlaceAdder.setEnabled(allow);
        if (allow) {
            mTitleText.setInputType(mFlagsTitle);
            mLogText.setInputType(mFlagsLog);
        } else {
            mTitleText.setInputType(InputType.TYPE_NULL);
            mLogText.setInputType(InputType.TYPE_NULL);
        }
    }

    protected void fillTheInformations() {
        new Thread() {
            @Override
            public void run() {
                List<Day> days = db.dayDao().getAllByDate(m_calendar.get(java.util.Calendar.DAY_OF_MONTH), m_calendar.get(java.util.Calendar.MONTH), m_calendar.get(java.util.Calendar.YEAR));
                if (days.isEmpty()) {
                    handler.sendEmptyMessage(MSG_ID.MSG_EMPTY.ordinal());
                } else {
                    Message msg_rating = Message.obtain();
                    msg_rating.what = MSG_ID.MSG_RATING.ordinal();
                    msg_rating.obj = days.get(0).getRating();
                    handler.sendMessage(msg_rating);

                    Message msg_title = Message.obtain();
                    msg_title.what = MSG_ID.MSG_TITLE.ordinal();
                    msg_title.obj = days.get(0).getTitleText();
                    handler.sendMessage(msg_title);

                    Message msg_log = Message.obtain();
                    msg_log.what = MSG_ID.MSG_LOG.ordinal();
                    msg_log.obj = days.get(0).getLog();
                    handler.sendMessage(msg_log);

                    Coordinate coordinate = new Coordinate();
                    coordinate.setLatitude(days.get(0).getLatitude());
                    coordinate.setLongitude(days.get(0).getLongitude());
                    Message msg_location = Message.obtain();
                    msg_location.what = MSG_ID.MSG_LOCATION.ordinal();
                    msg_location.obj = coordinate;
                    handler.sendMessage(msg_location);
                }
            }
        }.start();
    }

    protected boolean saveDay() {
        new Thread() {
            @Override
            public void run() {
                if (mRateView.getRating() == 0)
                    return;
                double latitude = 0;
                double longitude = 0;
                if ((mLastCoordinate == null) || ((mLastCoordinate.getLongitude() == 0) && (mLastCoordinate.getLatitude() == 0))) {
                    Location objLocation = mLocManager.getLocation();
                    if (objLocation != null) {
                        if (objLocation.getAccuracy() != 0) {
                            latitude = mLocManager.getLocation().getLatitude();
                            longitude = mLocManager.getLocation().getLongitude();
                        }
                    }
                } else {
                    latitude = mLastCoordinate.getLatitude();
                    longitude = mLastCoordinate.getLongitude();
                }
                Day d = new Day(m_calendar.get(Calendar.DAY_OF_WEEK),
                        m_calendar.get(Calendar.DAY_OF_MONTH),
                        m_calendar.get(Calendar.MONTH),
                        m_calendar.get(Calendar.YEAR),
                        m_calendar.get(Calendar.WEEK_OF_YEAR),
                        m_calendar.getTimeInMillis(),
                        (int) (mRateView.getRating()),
                        mTitleText.getText().toString(),
                        mLogText.getText().toString(),
                        latitude,
                        longitude,
                        false);
                db.dayDao().insertDay(d);
            }
        }.start();
        if (mRateView.getRating() == 0)
            return false;
        return true;
    }

    private void endActivity() {
        Intent myIntent = getIntent();
        if(saveDay())
            setResult((int) mRateView.getRating(), myIntent);
        else
            setResult(Activity.RESULT_CANCELED,myIntent);
        BitmapHelper.cleanDatas(m_calendar, mLogText.getText().toString());
        finish();
    }

    @Override
    public void onPause(){
        saveDay();
        mLocManager.clean();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        endActivity();
        return;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        endActivity();
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (((requestCode == ACTIVITY_RESULTS.GALLERY_ID.ordinal()) && (resultCode == RESULT_OK)) ||
                ((requestCode == ACTIVITY_RESULTS.CAMERA_ID.ordinal()) && (resultCode == RESULT_OK))) {
            if (data.getData() != null) {
                try {
                    if (mBitmap != null) {
                        mBitmap.recycle();
                    }

                    InputStream stream = getContentResolver().openInputStream(data.getData());
                    mBitmap = BitmapFactory.decodeStream(stream);
                    stream.close();
                    saveAndDisplay(mBitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                mBitmap = (Bitmap) data.getExtras().get("data");
                saveAndDisplay(mBitmap);
            }
            super.onActivityResult(requestCode, resultCode, data);
        }else if ((requestCode == ACTIVITY_RESULTS.PLACE_ID.ordinal()) && (resultCode == RESULT_OK)){
            double latitude = data.getDoubleExtra(MapPickActivity.EXTRA_PARAM_LATITUDE,0);
            double longitude = data.getDoubleExtra(MapPickActivity.EXTRA_PARAM_LONGITUDE,0);
            if((latitude != 0) && (longitude != 0)){
                mLastCoordinate = new Coordinate();
                mLastCoordinate.setLatitude(latitude);
                mLastCoordinate.setLongitude(longitude);
                Toast.makeText(getBaseContext(), R.string.new_coordinate_saved, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveAndDisplay(Bitmap bitmap) {
        Bitmap resizeBitmap = Bitmap.createScaledBitmap(bitmap, 600, bitmap.getHeight() * 600 / bitmap.getWidth(), true);
        bitmap.recycle();
        File imagePath = new File(BitmapHelper.getDayImageDir(m_calendar));
        arrayInt[0].setValue(arrayInt[0].getValue() + 1);
        File newFile = new File(imagePath, "image" + arrayInt[0].getValue() + ".png");
        try {
            FileOutputStream stream = new FileOutputStream(newFile);
            resizeBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String newString = BitmapHelper.addImageToString("image" + (arrayInt[0].getValue()) + ".png", mLogText.getText().toString(), mLogText.getSelectionStart());
        mLogText.setText(BitmapHelper.parseStringWithBitmaps(m_calendar, newString, arrayInt));
    }
    @Override
    public void onResume(){
        super.onResume();
        mLocManager.init();
    }
}
