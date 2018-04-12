package com.mithraw.howwasyourday.Helpers;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ShareActionProviderCustom;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareMediaContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.widget.ShareDialog;
import com.mithraw.howwasyourday.App;
import com.mithraw.howwasyourday.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
        mShareIntent = doIntent();
        mListViewToHide = new ArrayList<>();
    }

    public Bitmap getBitmapWithShareString() {
        Map<View, Integer> previousState = new HashMap<>();
        previousState.clear();
        for (View view : mListViewToHide) {
            previousState.put(view, view.getVisibility());
            view.setVisibility(View.GONE);
        }
        Bitmap image = Bitmap.createBitmap(mCardView.getDrawingCache());
        for (View view : mListViewToHide) {
            view.setVisibility(previousState.get(view));
        }

        return image;
    }

    private Intent doIntent() {
        File imagePath = new File(mActivity.getBaseContext().getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(mActivity.getBaseContext(), "com.mithraw.howwasyourday.fileprovider", newFile);
        Intent shareIntent = new Intent();
        if (contentUri != null) {
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, mActivity.getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        }
        return shareIntent;
    }

    public void updateDatas() {
        try {
            File cachePath = new File(App.getApplication().getApplicationContext().getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            getBitmapWithShareString().compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mShareIntent = doIntent();
    }

    public void attachToMenuItem(MenuItem item) {
        mShareActionProvider = (ShareActionProviderCustom) MenuItemCompat.getActionProvider(item);
        mShareActionProvider.setShareIntent(mShareIntent);
        mShareActionProvider.setOnShareTargetSelectedListener(
                new ShareActionProviderCustom.OnShareTargetSelectedListener() {
                    @Override
                    public boolean onShareTargetSelected(ShareActionProviderCustom actionProvider, Intent intent) {
                        final String appName = intent.getComponent().getPackageName();
                        Resources res = mActivity.getResources();
                        if ("com.facebook.katana".equals(appName)) {
                            SharePhoto photo = new SharePhoto.Builder().setBitmap(getBitmapWithShareString())
                                    .build();
                            ShareContent shareContent = new ShareMediaContent.Builder()
                                    .addMedium(photo)
                                    .setShareHashtag(new ShareHashtag.Builder()
                                            .setHashtag(res.getString(R.string.hashtag))
                                            .build())
                                    .build();
                            ShareDialog shareDialog = new ShareDialog(mActivity);
                            shareDialog.show(shareContent, ShareDialog.Mode.AUTOMATIC);
                            return true;
                        }
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
