package com.mithraw.howwasyourday.Activities;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.mithraw.howwasyourday.Helpers.BitmapHelper;
import com.mithraw.howwasyourday.R;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class DisplayImageActivity extends AppCompatActivity {
    public static String currentImage = "DisplayImageActivity_currentImage";
    public static String imagePath = "DisplayImageActivity_imagePath";
    public static String IMG = "DisplayImageActivity_IMG_transform";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_display_image);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        File imagePathFile = (File) getIntent().getSerializableExtra(imagePath);
        File imageFile = (File) getIntent().getSerializableExtra(currentImage);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), Arrays.asList(imagePathFile.listFiles()));

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mSectionsPagerAdapter.setMainItem(imageFile);
        mViewPager.setCurrentItem(mSectionsPagerAdapter.getItemPosition(imageFile));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        boolean mIsMainItem;

        public boolean isMainItem() {
            return mIsMainItem;
        }

        public void setIsMainItem(boolean mIsMainItem) {
            this.mIsMainItem = mIsMainItem;
        }

        private static final String ARG_FILE = "FILE";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(File image, boolean isMainItem) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putSerializable(ARG_FILE, image);
            fragment.setArguments(args);
            fragment.setIsMainItem(isMainItem);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_display_image, container, false);
            ImageView imageView = (ImageView) rootView.findViewById(R.id.DisplayimageView);
            File image = (File) getArguments().getSerializable(ARG_FILE);
            Bitmap bitmap = BitmapHelper.getBitmapFromFile(image);
            if (bitmap != null) {
                BitmapDrawable d = new BitmapDrawable(bitmap);
                imageView.setImageDrawable(d);
            }
            if (isMainItem())
                ViewCompat.setTransitionName(imageView, IMG);
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        List<File> mFiles;
        int mainItem;

        public SectionsPagerAdapter(FragmentManager fm, List<File> files) {
            super(fm);
            mFiles = files;
        }

        public void setMainItem(Object obj) {
            mainItem = getItemPosition(obj);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(mFiles.get(position), (position == mainItem) ? true : false);
        }

        @Override
        public int getCount() {

            return mFiles.size();
        }

        @Override
        public int getItemPosition(Object obj) {
            File cur = (File) obj;
            for (int i = 0; i < mFiles.size(); i++) {
                if (mFiles.get(i).getAbsolutePath().equals(cur.getAbsolutePath()))
                    return i;
            }
            return -1;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
