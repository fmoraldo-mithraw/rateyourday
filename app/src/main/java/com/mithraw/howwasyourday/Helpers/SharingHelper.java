package com.mithraw.howwasyourday.Helpers;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ShareActionProviderCustom;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.commonsware.cwac.provider.StreamProvider;
import com.mithraw.howwasyourday.Activities.MainActivity;
import com.mithraw.howwasyourday.App;
import com.mithraw.howwasyourday.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/*  In order to share things from cardViews, you have to first init your
SharingHelper mSharingHelper =  new SharingHelper(mCardView,this,
                this.findViewById(R.id.button_layout), // Theses views will be disabled
                this.findViewById(R.id.expand_button));
    Then attach it to a menuItem or a button
mSharingHelper.attachToMenuItem(item);
mSharingHelper.attachToButton(button);
    Then, update datas when you cardview is filled with your datas
mSharingHelper.updateDatas();
 */
public class SharingHelper {
    CardView mCardView;
    private ShareActionProviderCustom mShareActionProvider = null;
    private Intent mShareIntent;
    private Activity mActivity;
    Collection<View> mListViewToHide;


    public SharingHelper(@NonNull CardView cardView, @NonNull Activity activity, View... listViewToHide) {
        this(cardView, activity);
        mListViewToHide.addAll(Arrays.asList(listViewToHide));
    }

    public SharingHelper(@NonNull CardView cardView, @NonNull Activity activity) {
        mCardView = cardView;
        mCardView.setDrawingCacheEnabled(true);
        mActivity = activity;
        mShareIntent = new Intent();
        mListViewToHide = new ArrayList<>();
    }

    public Bitmap getBitmapWithShareString() {
        Map<View, Integer> previousState = new HashMap<>();
        previousState.clear();
        Bitmap image = null;
        for (View view : mListViewToHide) {
            previousState.put(view, view.getVisibility());
            view.setVisibility(View.GONE);
        }
        if ((mCardView != null) && (mCardView.getVisibility() != View.GONE)) {
            Bitmap tempBmp = mCardView.getDrawingCache();
            if (tempBmp != null) {
                Resources res =  MainActivity.getContext().getResources();
                image = Bitmap.createBitmap(tempBmp);
                Bitmap icon = BitmapHelper.drawableToBitmap(res.getDrawable(R.mipmap.ic_launcher));
                if (icon != null) {
                    Bitmap iconResized = Bitmap.createScaledBitmap(icon,100, 100, true);
                    if(iconResized != null) {
                        Canvas cs = new Canvas(image);
                        cs.drawBitmap(iconResized, image.getWidth() - iconResized.getWidth() + 5, 5, null);
                    }
                }
            }
        }
        for (View view : mListViewToHide) {
            view.setVisibility(previousState.get(view));
        }

        return image;
    }

    private Intent doIntent() {
        File imagePath = new File(mActivity.getBaseContext().getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = StreamProvider.getUriForFile("com.mithraw.howwasyourday.fileprovider", newFile);

        Logger.getLogger("SharingHelper").log(new LogRecord(Level.INFO, "FMORALDO : doIntent mms : " + contentUri));
        mShareIntent = new Intent();
        if (contentUri != null) {
            mShareIntent.setAction(Intent.ACTION_SEND);
            mShareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            mShareIntent.setData(contentUri);
            mShareIntent.setType("image/png");
            mShareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            //Fix permissions
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                List<ResolveInfo> resInfoList = App.getContext().getPackageManager().queryIntentActivities(mShareIntent, PackageManager.MATCH_ALL);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    mActivity.getBaseContext().grantUriPermission(packageName, contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }
            //mShareIntent.setDataAndType(contentUri, mActivity.getContentResolver().getType(contentUri));
        }
        return mShareIntent;
    }

    public void updateDatas() {
        try {
            File cachePath = new File(App.getApplication().getApplicationContext().getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            Bitmap image = getBitmapWithShareString();
            if(image != null)
                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        doIntent();
        if (mShareActionProvider != null)
            mShareActionProvider.setShareIntent(mShareIntent);
    }

    public void attachToMenuItem(MenuItem item) {
        mShareActionProvider = (ShareActionProviderCustom) MenuItemCompat.getActionProvider(item);
        mShareActionProvider.setShareIntent(mShareIntent);
        mShareActionProvider.setOnShareTargetSelectedListener(
                new ShareActionProviderCustom.OnShareTargetSelectedListener() {
                    @Override
                    public boolean onShareTargetSelected(ShareActionProviderCustom actionProvider, Intent intent) {
                        final String appName = intent.getComponent().getPackageName();
                        updateDatas();
                        return false;
                    }
                });
    }

    public void attachToButton(Button b) {
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDatas();
                Resources res = mActivity.getResources();
                mActivity.startActivity(Intent.createChooser(mShareIntent, res.getString(R.string.share_via)));
            }
        });
    }
    public void attachToImageButton(ImageButton b) {
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDatas();
                Resources res = mActivity.getResources();
                mActivity.startActivity(Intent.createChooser(mShareIntent, res.getString(R.string.share_via)));
            }
        });
    }
}
