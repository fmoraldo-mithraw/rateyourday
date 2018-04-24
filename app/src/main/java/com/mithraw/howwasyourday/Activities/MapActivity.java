package com.mithraw.howwasyourday.Activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mithraw.howwasyourday.App;
import com.mithraw.howwasyourday.R;
import com.mithraw.howwasyourday.Tools.UnderlinedCheckTextView;
import com.mithraw.howwasyourday.databases.Day;
import com.mithraw.howwasyourday.databases.DaysDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private DatePickerDialog startDatePickerDialog = null;
    private DatePickerDialog endDatePickerDialog = null;
    private Date startDate;
    private Date endDate;
    private MapView mMapView;
    LinearLayout startDateLayout;
    LinearLayout endDateLayout;
    DaysDatabase db;
    GoogleMap map;
    BitmapDescriptor icon1;
    BitmapDescriptor icon2;
    BitmapDescriptor icon3;
    BitmapDescriptor icon4;
    BitmapDescriptor icon5;
    private static Handler handler;

    public enum MSG_ID {
        QUERY_START,
        QUERY_END
    }
    LinearLayout ratingLayout1, ratingLayout2, ratingLayout3, ratingLayout4, ratingLayout5;
    RatingBar ratingBar1, ratingBar2, ratingBar3, ratingBar4, ratingBar5;
    UnderlinedCheckTextView mCheckTextViewMonday, mCheckTextViewTuesday, mCheckTextViewWednesday, mCheckTextViewThursday, mCheckTextViewFriday, mCheckTextViewSaturday, mCheckTextViewSunday;
    View.OnClickListener _wrappedOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateDataSet();
        }
    };
    View.OnClickListener _wrappedOnClickListenerRatingBar1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ratingBar1.setEnabled(!ratingBar1.isEnabled());
            updateDataSet();
        }
    };
    View.OnClickListener _wrappedOnClickListenerRatingBar2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ratingBar2.setEnabled(!ratingBar2.isEnabled());
            updateDataSet();
        }
    };
    View.OnClickListener _wrappedOnClickListenerRatingBar3 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ratingBar3.setEnabled(!ratingBar3.isEnabled());
            updateDataSet();
        }
    };
    View.OnClickListener _wrappedOnClickListenerRatingBar4 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ratingBar4.setEnabled(!ratingBar4.isEnabled());
            updateDataSet();
        }
    };
    View.OnClickListener _wrappedOnClickListenerRatingBar5 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ratingBar5.setEnabled(!ratingBar5.isEnabled());
            updateDataSet();
        }
    };
    View.OnClickListener startClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DatePickerDialog datePickerDialog = getStartDatePickerDialog();
            if (datePickerDialog != null) {
                datePickerDialog.show();
            }
        }
    };
    View.OnClickListener endClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DatePickerDialog datePickerDialog = getEndDatePickerDialog();
            if (datePickerDialog != null) {
                datePickerDialog.show();
            }
        }
    };
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private enum TYPE_DATE {START_DATE, END_DATE}

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Get the DB
        db = DaysDatabase.getInstance(getApplicationContext());

        mMapView = findViewById(R.id.mapViewBig);
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);

        icon1 = BitmapDescriptorFactory.fromResource(R.drawable.ic_place_black);
        icon2 = BitmapDescriptorFactory.fromResource(R.drawable.ic_place_orange);
        icon3 = BitmapDescriptorFactory.fromResource(R.drawable.ic_place_yellow);
        icon4 = BitmapDescriptorFactory.fromResource(R.drawable.ic_place_light_green);
        icon5 = BitmapDescriptorFactory.fromResource(R.drawable.ic_place_solid_green);
        //Setup the date listener
        //StarDate
        startDate = new Date(118, 0, 1);
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        updateDateTextView((TextView) findViewById(R.id.text_view_end_date), startDate);
        final DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                updateDate(year, monthOfYear, dayOfMonth, MapActivity.TYPE_DATE.START_DATE);
            }

        };
        startDatePickerDialog = new DatePickerDialog(this, startDateListener, cal
                .get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.MONTH),
                cal.get(java.util.Calendar.DAY_OF_MONTH));

        //EndDate
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.DAY_OF_MONTH, 1);
        endDate = cal.getTime();
        updateDateTextView((TextView) findViewById(R.id.text_view_end_date), endDate);
        final DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                updateDate(year, monthOfYear, dayOfMonth, MapActivity.TYPE_DATE.END_DATE);
            }

        };
        endDatePickerDialog = new DatePickerDialog(this, endDateListener, cal
                .get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.MONTH),
                cal.get(java.util.Calendar.DAY_OF_MONTH));

        //set daysListeners

        (mCheckTextViewMonday = (UnderlinedCheckTextView) findViewById(R.id.checked_text_view_monday)).setOnClickListener(_wrappedOnClickListener);
        (mCheckTextViewTuesday = (UnderlinedCheckTextView) findViewById(R.id.checked_text_view_tuesday)).setOnClickListener(_wrappedOnClickListener);
        (mCheckTextViewWednesday = (UnderlinedCheckTextView) findViewById(R.id.checked_text_view_wednesday)).setOnClickListener(_wrappedOnClickListener);
        (mCheckTextViewThursday = (UnderlinedCheckTextView) findViewById(R.id.checked_text_view_thursday)).setOnClickListener(_wrappedOnClickListener);
        (mCheckTextViewFriday = (UnderlinedCheckTextView) findViewById(R.id.checked_text_view_friday)).setOnClickListener(_wrappedOnClickListener);
        (mCheckTextViewSaturday = (UnderlinedCheckTextView) findViewById(R.id.checked_text_view_saturday)).setOnClickListener(_wrappedOnClickListener);
        (mCheckTextViewSunday = (UnderlinedCheckTextView) findViewById(R.id.checked_text_view_sunday)).setOnClickListener(_wrappedOnClickListener);
        startDateLayout = findViewById(R.id.startDateLayout);
        endDateLayout = findViewById(R.id.endDateLayout);
        ratingBar1 = findViewById(R.id.ratingBar1);
        ratingBar2 = findViewById(R.id.ratingBar2);
        ratingBar3 = findViewById(R.id.ratingBar3);
        ratingBar4 = findViewById(R.id.ratingBar4);
        ratingBar5 = findViewById(R.id.ratingBar5);
        ratingLayout1 = findViewById(R.id.ratingLayout1);
        ratingLayout2 = findViewById(R.id.ratingLayout2);
        ratingLayout3 = findViewById(R.id.ratingLayout3);
        ratingLayout4 = findViewById(R.id.ratingLayout4);
        ratingLayout5 = findViewById(R.id.ratingLayout5);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_ID.QUERY_END.ordinal()) {
                    List<Day> days = (List<Day>) msg.obj;
                    map.clear();
                    for (Day d : days) {
                        BitmapDescriptor bm;
                        switch (d.getRating()) {
                            case 1:
                                bm = icon1;
                                break;
                            case 2:
                                bm = icon2;
                                break;
                            case 3:
                                bm = icon3;
                                break;
                            case 4:
                                bm = icon4;
                                break;
                            default:
                                bm = icon5;
                                break;
                        }
                        map.addMarker(new MarkerOptions().position(new LatLng(d.getLatitude(), d.getLongitude())).title(d.getTitleText() + " " + d.getRating() + "/5").icon(bm));
                    }
                }
            }
        };
        mapReady(false);
    }

    private void mapReady(boolean isReady) {
        startDateLayout.setOnClickListener(isReady ? startClickListener : null);
        endDateLayout.setOnClickListener(isReady ? endClickListener : null);
        ratingLayout1.setOnClickListener(isReady ? _wrappedOnClickListenerRatingBar1 : null);
        ratingLayout2.setOnClickListener(isReady ? _wrappedOnClickListenerRatingBar2 : null);
        ratingLayout3.setOnClickListener(isReady ? _wrappedOnClickListenerRatingBar3 : null);
        ratingLayout4.setOnClickListener(isReady ? _wrappedOnClickListenerRatingBar4 : null);
        ratingLayout5.setOnClickListener(isReady ? _wrappedOnClickListenerRatingBar5 : null);
        mCheckTextViewMonday.setEnabled(isReady);
        mCheckTextViewTuesday.setEnabled(isReady);
        mCheckTextViewWednesday.setEnabled(isReady);
        mCheckTextViewThursday.setEnabled(isReady);
        mCheckTextViewFriday.setEnabled(isReady);
        mCheckTextViewSaturday.setEnabled(isReady);
        mCheckTextViewSunday.setEnabled(isReady);
        if (isReady) updateDataSet();
    }

    private void updateDataSet() {
        new Thread() {
            @Override
            public void run() {
                handler.sendEmptyMessage(MSG_ID.QUERY_START.ordinal());
                Calendar cal = Calendar.getInstance();
                cal.setTime(startDate);
                long startDateInt = cal.getTimeInMillis();
                cal.setTime(endDate);
                cal.add(Calendar.DAY_OF_MONTH, 1);
                long endDateInt = cal.getTimeInMillis();

                boolean monday = mCheckTextViewMonday.isChecked();
                boolean tuesday = mCheckTextViewTuesday.isChecked();
                boolean wednesday = mCheckTextViewWednesday.isChecked();
                boolean thursday = mCheckTextViewThursday.isChecked();
                boolean friday = mCheckTextViewFriday.isChecked();
                boolean saturday = mCheckTextViewSaturday.isChecked();
                boolean sunday = mCheckTextViewSunday.isChecked();
                int size = (monday ? 1 : 0) + (tuesday ? 1 : 0) + (wednesday ? 1 : 0) + (thursday ? 1 : 0) + (friday ? 1 : 0) + (saturday ? 1 : 0) + (sunday ? 1 : 0);
                int count = 0;
                int[] idsDays = new int[size];
                if (monday) idsDays[count++] = Calendar.MONDAY;
                if (tuesday) idsDays[count++] = Calendar.TUESDAY;
                if (wednesday) idsDays[count++] = Calendar.WEDNESDAY;
                if (thursday) idsDays[count++] = Calendar.THURSDAY;
                if (friday) idsDays[count++] = Calendar.FRIDAY;
                if (saturday) idsDays[count++] = Calendar.SATURDAY;
                if (sunday) idsDays[count++] = Calendar.SUNDAY;
                boolean rate1 = ratingBar1.isEnabled();
                boolean rate2 = ratingBar2.isEnabled();
                boolean rate3 = ratingBar3.isEnabled();
                boolean rate4 = ratingBar4.isEnabled();
                boolean rate5 = ratingBar5.isEnabled();
                size = (rate1 ? 1 : 0) + (rate2 ? 1 : 0) + (rate3 ? 1 : 0) + (rate4 ? 1 : 0) + (rate5 ? 1 : 0);
                int[] idsRate = new int[size];
                count = 0;
                if (rate1) idsRate[count++] = 1;
                if (rate2) idsRate[count++] = 2;
                if (rate3) idsRate[count++] = 3;
                if (rate4) idsRate[count++] = 4;
                if (rate5) idsRate[count++] = 5;

                List<Day> days = db.dayDao().getByBoundsAndDaysAndRatingNoBadLocations(startDateInt, endDateInt, idsDays, idsRate);
                Message msg_rating = Message.obtain();
                msg_rating.what = MSG_ID.QUERY_END.ordinal();
                msg_rating.obj = days;
                handler.sendMessage(msg_rating);
            }
        }.start();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        mapReady(true);
        updateDataSet();
    }

    private void updateDateTextView(TextView tv, Date d) {
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(App.getApplication());
        tv.setText(dateFormat.format(d));
    }

    private void updateDate(int year, int month, int day, MapActivity.TYPE_DATE typeDate) {
        if (typeDate == MapActivity.TYPE_DATE.START_DATE) {
            startDate = new Date(year - 1900, month, day);
            updateDateTextView((TextView) findViewById(R.id.text_view_start_date), startDate);
        } else {
            endDate = new Date(year - 1900, month, day);
            updateDateTextView((TextView) findViewById(R.id.text_view_end_date), endDate);
        }
        updateDataSet();
    }

    public DatePickerDialog getStartDatePickerDialog() {
        return startDatePickerDialog;
    }

    public DatePickerDialog getEndDatePickerDialog() {
        return endDatePickerDialog;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
