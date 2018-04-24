package com.mithraw.howwasyourday.Tools.Map;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.mithraw.howwasyourday.R;

public class RateClusterItem implements ClusterItem {
    private final LatLng mPosition;
    private String mTitle;
    private String mSnippet;
    private int mRate;
    static BitmapDescriptor icon1;
    static BitmapDescriptor icon2;
    static BitmapDescriptor icon3;
    static BitmapDescriptor icon4;
    static BitmapDescriptor icon5;
    BitmapDescriptor mIcon;

    public BitmapDescriptor getIcon() {
        return mIcon;
    }

    public void setIcon(int rate) {
        switch (rate) {
            case 1:
                mIcon = icon1;
                break;
            case 2:
                mIcon = icon2;
                break;
            case 3:
                mIcon = icon3;
                break;
            case 4:
                mIcon = icon4;
                break;
            default:
                mIcon = icon5;
                break;
        }
    }

    public RateClusterItem(double lat, double lng, String title, int rate) {
        mPosition = new LatLng(lat, lng);
        if (title.equals(""))
            mTitle = rate + "/5";
        else
            mTitle = title + " " + rate + "/5";
        mSnippet = mTitle;
        mRate = rate;
        icon1 = BitmapDescriptorFactory.fromResource(R.drawable.ic_place_red);
        icon2 = BitmapDescriptorFactory.fromResource(R.drawable.ic_place_orange);
        icon3 = BitmapDescriptorFactory.fromResource(R.drawable.ic_place_yellow);
        icon4 = BitmapDescriptorFactory.fromResource(R.drawable.ic_place_light_green);
        icon5 = BitmapDescriptorFactory.fromResource(R.drawable.ic_place_solid_green);
        setIcon(rate);
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSnippet() {
        return mSnippet;
    }

    /**
     * Set the title of the marker
     *
     * @param title string to be set as title
     */
    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * Set the description of the marker
     *
     * @param snippet string to be set as snippet
     */
    public void setSnippet(String snippet) {
        mSnippet = snippet;
    }
}
