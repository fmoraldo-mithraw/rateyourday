package com.mithraw.howwasyourday.Tools.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;

public class DrawableMapView extends MapView {
    Bitmap mBitmap;
    public DrawableMapView(Context context) {
        super(context);
    }

    public DrawableMapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public DrawableMapView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public DrawableMapView(Context context, GoogleMapOptions googleMapOptions) {
        super(context, googleMapOptions);
    }

    @Override
    public Bitmap getDrawingCache() {
        getDrawingCache(false);
        return null;
    }
    public void setDrawingCache(Bitmap bitmap){
        mBitmap = bitmap;
    }
    @Override
    public Bitmap getDrawingCache(boolean autoScale) {
        return mBitmap;
    }
}
