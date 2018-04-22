package com.mithraw.howwasyourday.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.commonsware.cwac.provider.StreamProvider;
import com.mithraw.howwasyourday.App;
import com.mithraw.howwasyourday.Helpers.BitmapHelper;
import com.mithraw.howwasyourday.Helpers.DataBaseHelper;
import com.mithraw.howwasyourday.R;
import com.mithraw.howwasyourday.Tools.ImportResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ImportExportActivity extends AppCompatActivity {

    Activity mActivity;
    Handler mHandler;

    public enum MSG_ID {IOEXCEPTION, FILENOTFOUNDEXCEPTION, CSV_IMPORTED, DB_IMPORTED, PHOTO_ARCHIVE_IMPORTED}

    public enum ACTIVITY_RESULTS {CSV_IMPORT, DB_IMPORT, PHOTO_ARCHIVE_IMPORT}

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mActivity = this;
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_ID.FILENOTFOUNDEXCEPTION.ordinal()) {
                    Toast.makeText(mActivity.getApplicationContext(), ((FileNotFoundException) msg.obj).getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                if (msg.what == MSG_ID.IOEXCEPTION.ordinal()) {
                    Toast.makeText(mActivity.getApplicationContext(), ((IOException) msg.obj).getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                if (msg.what == MSG_ID.CSV_IMPORTED.ordinal()) {
                    Resources res = App.getContext().getResources();
                    ImportResult ir = (ImportResult) msg.obj;
                    String text = res.getString(R.string.csv_imported,ir.getSucceed(),ir.getTotal());
                    Toast.makeText(mActivity.getApplicationContext(), text, Toast.LENGTH_LONG).show();
                }
                if (msg.what == MSG_ID.DB_IMPORTED.ordinal()) {
                    Resources res = App.getContext().getResources();
                    ImportResult ir = (ImportResult) msg.obj;
                    String text = res.getString(R.string.db_imported,ir.getSucceed(),ir.getTotal());
                    Toast.makeText(mActivity.getApplicationContext(), text, Toast.LENGTH_LONG).show();
                }
                if (msg.what == MSG_ID.PHOTO_ARCHIVE_IMPORTED.ordinal()) {
                    Toast.makeText(mActivity.getApplicationContext(), R.string.photo_archive_imported, Toast.LENGTH_LONG).show();
                }

            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    public void importCsv(View v) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
        galleryIntent.setType("text/csv");
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(galleryIntent, ACTIVITY_RESULTS.CSV_IMPORT.ordinal());
    }

    public void importDb(View v) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
        galleryIntent.setType("application/x-sqlite3");
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(galleryIntent, ACTIVITY_RESULTS.DB_IMPORT.ordinal());
    }

    public void importPhotos(View v) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
        galleryIntent.setType("application/zip");
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(galleryIntent, ACTIVITY_RESULTS.PHOTO_ARCHIVE_IMPORT.ordinal());
    }

    public void exportCsv(View v) {
        File imagePath = new File(App.getContext().getCacheDir(), "files");
        imagePath.mkdirs();
        File newFile = new File(imagePath, "import.csv");
        newFile.delete();
        try {
            newFile.createNewFile();
            final Uri contentUri = StreamProvider.getUriForFile("com.mithraw.howwasyourday.fileprovider", newFile);
            FileObserver fo = new FileObserver(newFile.getAbsolutePath()) {
                @Override
                public void onEvent(int event, @Nullable String path) {
                    if ((event == CLOSE_WRITE) || (event == CLOSE_NOWRITE)) {
                        this.stopWatching();
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                        intent.setData(contentUri);
                        intent.setType("text/csv");
                        intent.putExtra(Intent.EXTRA_STREAM, contentUri);
                        //Fix permissions
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                            List<ResolveInfo> resInfoList = App.getContext().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_ALL);
                            for (ResolveInfo resolveInfo : resInfoList) {
                                String packageName = resolveInfo.activityInfo.packageName;
                                mActivity.getBaseContext().grantUriPermission(packageName, contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            }
                        }
                        mActivity.startActivity(intent);
                    }
                }
            };
            fo.startWatching();
            DataBaseHelper.getCsv(newFile, mHandler);
        } catch (IOException e) {
            Message msg = Message.obtain();
            msg.what = ImportExportActivity.MSG_ID.IOEXCEPTION.ordinal();
            msg.obj = e;
            mHandler.sendMessage(msg);
        }
    }

    public void exportDb(View v) {
        File imagePath = new File(App.getContext().getCacheDir(), "files");
        imagePath.mkdirs();
        File newFile = new File(imagePath, "day_database.db");
        newFile.delete();
        try {
            newFile.createNewFile();
            final Uri contentUri = StreamProvider.getUriForFile("com.mithraw.howwasyourday.fileprovider", newFile);
            FileObserver fo = new FileObserver(newFile.getAbsolutePath()) {
                @Override
                public void onEvent(int event, @Nullable String path) {
                    if ((event == CLOSE_WRITE) || (event == CLOSE_NOWRITE)) {
                        this.stopWatching();
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                        intent.setData(contentUri);
                        intent.setType("application/x-sqlite3");
                        intent.putExtra(Intent.EXTRA_STREAM, contentUri);
                        //Fix permissions
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                            List<ResolveInfo> resInfoList = App.getContext().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_ALL);
                            for (ResolveInfo resolveInfo : resInfoList) {
                                String packageName = resolveInfo.activityInfo.packageName;
                                mActivity.getBaseContext().grantUriPermission(packageName, contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            }
                        }
                        mActivity.startActivity(intent);
                    }
                }
            };
            fo.startWatching();
            DataBaseHelper.getDb(newFile, mHandler);
        } catch (IOException e) {
            Message msg = Message.obtain();
            msg.what = ImportExportActivity.MSG_ID.IOEXCEPTION.ordinal();
            msg.obj = e;
            mHandler.sendMessage(msg);
        }
    }

    public void exportPhotos(View v) {
        File imagePath = new File(App.getContext().getCacheDir(), "files");
        imagePath.mkdirs();
        File newFile = new File(imagePath, "photos.zip");
        newFile.delete();
        try {
            newFile.createNewFile();
            final Uri contentUri = StreamProvider.getUriForFile("com.mithraw.howwasyourday.fileprovider", newFile);
            FileObserver fo = new FileObserver(newFile.getAbsolutePath()) {
                @Override
                public void onEvent(int event, @Nullable String path) {
                    if ((event == CLOSE_WRITE) || (event == CLOSE_NOWRITE)) {
                        this.stopWatching();
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                        intent.setData(contentUri);
                        intent.setType("application/zip");
                        intent.putExtra(Intent.EXTRA_STREAM, contentUri);
                        //Fix permissions
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                            List<ResolveInfo> resInfoList = App.getContext().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_ALL);
                            for (ResolveInfo resolveInfo : resInfoList) {
                                String packageName = resolveInfo.activityInfo.packageName;
                                mActivity.getBaseContext().grantUriPermission(packageName, contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            }
                        }
                        mActivity.startActivity(intent);
                    }
                }
            };
            fo.startWatching();
            BitmapHelper.getPhotoArchive(newFile, mHandler);
        } catch (IOException e) {
            Message msg = Message.obtain();
            msg.what = ImportExportActivity.MSG_ID.IOEXCEPTION.ordinal();
            msg.obj = e;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        try {
            if((data != null)&& (data.getData() != null)) {
                InputStream stream = getContentResolver().openInputStream(data.getData());
                if ((requestCode == ACTIVITY_RESULTS.CSV_IMPORT.ordinal())
                        && (resultCode == Activity.RESULT_OK)) {
                    DataBaseHelper.importCsv(stream, mHandler);
                } else if ((requestCode == ACTIVITY_RESULTS.DB_IMPORT.ordinal())
                        && (resultCode == Activity.RESULT_OK)) {
                    DataBaseHelper.importDb(stream, mHandler);
                } else if ((requestCode == ACTIVITY_RESULTS.PHOTO_ARCHIVE_IMPORT.ordinal())
                        && (resultCode == Activity.RESULT_OK)) {
                    BitmapHelper.importPhotos(stream, mHandler);
                }
            }
        } catch (FileNotFoundException e) {
            Message msg = Message.obtain();
            msg.what = ImportExportActivity.MSG_ID.FILENOTFOUNDEXCEPTION.ordinal();
            msg.obj = e;
            mHandler.sendMessage(msg);

        } catch (IOException e) {
            Message msg = Message.obtain();
            msg.what = ImportExportActivity.MSG_ID.IOEXCEPTION.ordinal();
            msg.obj = e;
            mHandler.sendMessage(msg);
        }
    }
}
