package com.mithraw.howwasyourday.Tools.Map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.mithraw.howwasyourday.App;
import com.mithraw.howwasyourday.R;

public class OwnIconRenderer extends DefaultClusterRenderer<RateClusterItem> {
    private final IconGenerator mClusterIconGenerator = new IconGenerator(App.getContext());
    int mRate;

    public OwnIconRenderer(Context context, GoogleMap map,
                           ClusterManager<RateClusterItem> clusterManager, int rate) {
        super(context, map, clusterManager);
        mRate = rate;
    }

    @Override
    protected void onBeforeClusterItemRendered(RateClusterItem item, MarkerOptions markerOptions) {
        markerOptions.icon(item.getIcon());
        markerOptions.snippet(item.getSnippet());
        markerOptions.title(item.getTitle());
        super.onBeforeClusterItemRendered(item, markerOptions);
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<RateClusterItem> cluster, MarkerOptions markerOptions) {
        Resources res = App.getContext().getResources();
        final Drawable clusterIcon =res.getDrawable(R.drawable.ic_lens);
        switch(mRate){
            case 1: clusterIcon.setColorFilter(res.getColor(R.color.color1), PorterDuff.Mode.SRC_ATOP);break;
            case 2: clusterIcon.setColorFilter(res.getColor(R.color.color2), PorterDuff.Mode.SRC_ATOP);break;
            case 3: clusterIcon.setColorFilter(res.getColor(R.color.color3), PorterDuff.Mode.SRC_ATOP);break;
            case 4: clusterIcon.setColorFilter(res.getColor(R.color.color4), PorterDuff.Mode.SRC_ATOP);break;
            default :clusterIcon.setColorFilter(res.getColor(R.color.color5), PorterDuff.Mode.SRC_ATOP);break;
        }


        mClusterIconGenerator.setBackground(clusterIcon);
        mClusterIconGenerator.setTextAppearance(R.style.TextAppearanceCentered);
        //modify padding for one or two digit numbers
        if (cluster.getSize() < 10) {
            mClusterIconGenerator.setContentPadding(36, 16, 0, 0);
        }
        else {
            mClusterIconGenerator.setContentPadding(24, 16, 0, 0);
        }

        Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        markerOptions.title(String.valueOf(cluster.getSize()));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }
}

