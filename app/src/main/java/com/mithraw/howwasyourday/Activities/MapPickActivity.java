package com.mithraw.howwasyourday.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mithraw.howwasyourday.R;
import com.mithraw.howwasyourday.Tools.Coordinate;
import com.mithraw.howwasyourday.Tools.Map.IconManager;
import com.mithraw.howwasyourday.Tools.MyLocationManager;
import com.mithraw.howwasyourday.databases.Day;
import com.mithraw.howwasyourday.databases.DaysDatabase;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class MapPickActivity extends AppCompatActivity implements OnMapReadyCallback {
    public final static String EXTRA_PARAM_RATE = "ExpandedDayActivity_Rate";
    public final static String EXTRA_PARAM_TITLE = "ExpandedDayActivity_Title";
    public final static String EXTRA_PARAM_LONGITUDE = "ExpandedDayActivity_LONGITUDE";
    public final static String EXTRA_PARAM_LATITUDE = "ExpandedDayActivity_LATITUDE";

    private enum MSG_ID {LAST_LOCATION}
    MyLocationManager mLocManager;
    GoogleMap map;
    MapView mMapView;
    String mTitle = "";
    double mLatitude = 0;
    double mLongitude = 0;
    int mRate = 0;
    boolean hasMovedOnce = false;
    Handler handler;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_pick);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String title = intent.getStringExtra(MapPickActivity.EXTRA_PARAM_TITLE);
        mLongitude = intent.getDoubleExtra(MapPickActivity.EXTRA_PARAM_LONGITUDE, 0);
        mLatitude = intent.getDoubleExtra(MapPickActivity.EXTRA_PARAM_LATITUDE, 0);
        mRate = intent.getIntExtra(MapPickActivity.EXTRA_PARAM_RATE, 0);
        if (((title == null) || title.equals("")) && (mRate != 0)) {
            mTitle = mRate + "/5";
        } else if (((title != null) && !title.equals("")) && (mRate != 0)) {
            mTitle = title + " " + mRate + "/5";
        } else if (((title != null) && !title.equals("")) && (mRate == 0)) {
            mTitle = title;
        }
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                pickLocation(place.getLatLng());
            }

            @Override
            public void onError(Status status) {
            }
        });

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                if ((mLatitude == 0) && (mLongitude == 0)) {
                    if ((map != null) && (!hasMovedOnce)) {
                        if (location != null) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                            map.animateCamera(cameraUpdate);
                            hasMovedOnce = true;
                        }
                    }
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        if ((mLatitude == 0) && (mLongitude == 0)) {
            mLocManager = new MyLocationManager(this, locationListener);//add a location listener that update the map
        }
        mMapView = findViewById(R.id.mapViewPick);
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView.onCreate(mapViewBundle);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_ID.LAST_LOCATION.ordinal()) {
                    Coordinate coord = (Coordinate) msg.obj;
                    if ((coord != null) && (map != null) && (!hasMovedOnce)) {
                        LatLng latLng = new LatLng(coord.getLatitude(), coord.getLongitude());
                        map.clear();
                        map.addMarker(new MarkerOptions().position(latLng).title(mTitle).icon(IconManager.getInstance().getIcon(mRate)));
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                        map.animateCamera(cameraUpdate);
                        mLatitude = latLng.latitude;
                        mLongitude = latLng.longitude;
                    }
                }
            }
        };
        mMapView.getMapAsync(this);


    }

    @Override
    public void onResume() {
        super.onResume();
        if ((mLatitude != 0) && (mLongitude != 0) && (mLocManager != null)) {
            mLocManager.init();
        }
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        endActivity();
        mMapView.onPause();
        super.onPause();
    }

    private void endActivity() {
        if ((mLatitude == 0) && (mLongitude == 0) && (mLocManager != null))
            mLocManager.clean();
        Intent intent = new Intent();
        intent.putExtra(EXTRA_PARAM_LONGITUDE, mLongitude);
        intent.putExtra(EXTRA_PARAM_LATITUDE, mLatitude);
        setResult(RESULT_OK, intent);
    }

    @Override
    public void onBackPressed() {
        endActivity();
        super.onBackPressed();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    private void pickLocation(LatLng latLng) {
        if (map != null) {
            map.clear();
            map.addMarker(new MarkerOptions().position(latLng).title(mTitle).icon(IconManager.getInstance().getIcon(mRate)));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            map.animateCamera(cameraUpdate);
            mLatitude = latLng.latitude;
            mLongitude = latLng.longitude;
        }
    }

    public void getDbLastLocation() {
        new Thread() {
            @Override
            public void run() {
                DaysDatabase db = DaysDatabase.getInstance(getBaseContext());
                //Last year
                List<Day> days = db.dayDao().getLastOneWithCoordinate();
                if(days.size() >0){
                    Coordinate coord = new Coordinate();
                    coord.setLatitude(days.get(0).getLatitude());
                    coord.setLongitude(days.get(0).getLongitude());
                    Message msg = handler.obtainMessage();
                    msg.what = MSG_ID.LAST_LOCATION.ordinal();
                    msg.obj = coord;
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                pickLocation(latLng);
            }
        });
        if ((mLatitude == 0) && (mLongitude == 0) && (mLocManager != null)) {
            Location loc = mLocManager.getLocation();
            if ((loc != null) && (!hasMovedOnce)) {
                LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
                map.clear();
                map.addMarker(new MarkerOptions().position(latLng).title(mTitle).icon(IconManager.getInstance().getIcon(mRate)));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                map.animateCamera(cameraUpdate);
                mLatitude = latLng.latitude;
                mLongitude = latLng.longitude;
                hasMovedOnce = true;
            } else if ((loc == null) && (!hasMovedOnce)) {
                getDbLastLocation();
            }
        } else {
            LatLng latLng = new LatLng(mLatitude, mLongitude);
            map.clear();
            map.addMarker(new MarkerOptions().position(latLng).title(mTitle).icon(IconManager.getInstance().getIcon(mRate)));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            map.animateCamera(cameraUpdate);
            mLatitude = latLng.latitude;
            mLongitude = latLng.longitude;
            hasMovedOnce = true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        endActivity();
        super.onStop();
        mMapView.onStop();
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
