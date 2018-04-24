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
    BitmapDescriptor mIcon;

    public BitmapDescriptor getIcon() {
        return mIcon;
    }

    public void setIcon(int rate) {
        mIcon = IconManager.getInstance().getIcon(rate);
    }

    public RateClusterItem(double lat, double lng, String title, int rate) {
        mPosition = new LatLng(lat, lng);
        if (title.equals(""))
            mTitle = rate + "/5";
        else
            mTitle = title + " " + rate + "/5";
        mSnippet = mTitle;
        mRate = rate;
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
