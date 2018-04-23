package com.mithraw.howwasyourday.Activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mithraw.howwasyourday.Helpers.BitmapHelper;
import com.mithraw.howwasyourday.R;
import com.mithraw.howwasyourday.Helpers.SharingHelper;
import com.mithraw.howwasyourday.Tools.MyInt;

import java.util.Calendar;

/*
Show an expanded view of the Day
 */
public class ExpandedDayActivity extends AppCompatActivity implements OnMapReadyCallback{
    public final static String EXTRA_PARAM_DATE = "ExpandedDayActivity_Date";
    public final static String EXTRA_PARAM_RATE = "ExpandedDayActivity_Rate";
    public final static String EXTRA_PARAM_TITLE = "ExpandedDayActivity_Title";
    public final static String EXTRA_PARAM_LOG = "ExpandedDayActivity_Log";
    public final static String EXTRA_PARAM_DATETIME = "ExpandedDayActivity_DATETIME";
    public final static String EXTRA_PARAM_LONGITUDE = "ExpandedDayActivity_LONGITUDE";
    public final static String EXTRA_PARAM_LATITUDE = "ExpandedDayActivity_LATITUDE";
    private MapView mMapView;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    double latitude = 0;
    double longitude = 0;

    MyInt[] arrayInt = {new MyInt(0)};
    private SharingHelper mSharingHelper;
    // View name for the transition
    public final static String VIEW_NAME = "cardview";
    Calendar cal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expanded_day);
        //Add the back button to the activity

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CardView cv = findViewById(R.id.cardViewExpand);
        // Retreive the informations of the date from the main activity and fill the calendar with them
        Intent intent = getIntent();
        cal = Calendar.getInstance();
        cal.setTimeInMillis(intent.getLongExtra(EXTRA_PARAM_DATETIME,System.currentTimeMillis()));
        ((TextView)findViewById(R.id.dateTextView)).setText(intent.getStringExtra(EXTRA_PARAM_DATE));
        TextView titleTextView = findViewById(R.id.titleText);
        mMapView = (MapView) findViewById(R.id.mapView);
        latitude = intent.getDoubleExtra(EXTRA_PARAM_LATITUDE,0);
        longitude = intent.getDoubleExtra(EXTRA_PARAM_LONGITUDE,0);
        if((latitude == 0) && (longitude == 0)){
            mMapView.setVisibility(View.GONE);
        }else{
            mMapView.setVisibility(View.VISIBLE);
        }
        String title = intent.getStringExtra(EXTRA_PARAM_TITLE);
        if (title.equals("")) {
            titleTextView.setVisibility(View.GONE);
            titleTextView.setText("");
        }else {
            titleTextView.setVisibility(View.VISIBLE);
            titleTextView.setText(title);
        }
        TextView logTextView = findViewById(R.id.logText);
        String log = intent.getStringExtra(EXTRA_PARAM_LOG);
        if (log.equals("")) {
            logTextView.setVisibility(View.GONE);
            logTextView.setText("");
        }else {
            logTextView.setVisibility(View.VISIBLE);
            logTextView.setText(BitmapHelper.parseStringWithBitmaps(cal, log, arrayInt));
        }
        ((RatingBar)findViewById(R.id.ratingBar)).setRating(intent.getFloatExtra(EXTRA_PARAM_RATE,0));
        ViewCompat.setTransitionName(cv, VIEW_NAME);
        //Init the share helper
        mSharingHelper = new SharingHelper(cv,this,
                this.findViewById(R.id.share_view));
        mSharingHelper.attachToImageButton((ImageButton)this.findViewById(R.id.share_view));
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
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
    public void onMapReady(GoogleMap map) {
        Resources res = getResources();
        LatLng latLng = new LatLng(latitude, longitude);
        map.addMarker(new MarkerOptions().position(latLng).title(res.getString(R.string.maps_marker)));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        map.animateCamera(cameraUpdate);
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
