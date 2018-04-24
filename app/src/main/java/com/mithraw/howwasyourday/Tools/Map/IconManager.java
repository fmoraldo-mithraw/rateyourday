package com.mithraw.howwasyourday.Tools.Map;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.mithraw.howwasyourday.R;

public class IconManager {
    static BitmapDescriptor icon1;
    static BitmapDescriptor icon2;
    static BitmapDescriptor icon3;
    static BitmapDescriptor icon4;
    static BitmapDescriptor icon5;
    static IconManager mInstance;
    private IconManager(){
        icon1 = BitmapDescriptorFactory.fromResource(R.drawable.ic_place_red);
        icon2 = BitmapDescriptorFactory.fromResource(R.drawable.ic_place_orange);
        icon3 = BitmapDescriptorFactory.fromResource(R.drawable.ic_place_yellow);
        icon4 = BitmapDescriptorFactory.fromResource(R.drawable.ic_place_light_green);
        icon5 = BitmapDescriptorFactory.fromResource(R.drawable.ic_place_solid_green);
    }
    public static IconManager getInstance(){
        if(mInstance == null)
            mInstance = new IconManager();
        return mInstance;
    }
    public BitmapDescriptor getIcon(int rating){
        switch (rating) {
            case 1:
                return icon1;
            case 2:
                return icon2;
            case 3:
                return icon3;
            case 4:
                return icon4;
            default:
                return icon5;
        }
    }
}
