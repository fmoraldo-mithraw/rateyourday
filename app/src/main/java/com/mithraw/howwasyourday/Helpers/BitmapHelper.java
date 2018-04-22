package com.mithraw.howwasyourday.Helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

import com.mithraw.howwasyourday.Activities.ImportExportActivity;
import com.mithraw.howwasyourday.App;
import com.mithraw.howwasyourday.Tools.MyInt;
import com.mithraw.howwasyourday.Tools.ZipTool;
import com.mithraw.howwasyourday.databases.DaysDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/*
Help getting bitmap from resources
 */
public class BitmapHelper {
    public static Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap getBitmapFromFile(File file) {
        Bitmap bitmap = null;
        try {
            FileInputStream stream = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(stream);
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;

    }

    public static String addImageToString(String imageName, String text, int position) {
        String imgShare = "[[[" + imageName + "]]]";
        String newText = text.substring(0, position) + imgShare + text.substring(position, text.length());
        return newText;
    }

    public static SpannableStringBuilder parseStringWithBitmaps(Calendar cal, String text, MyInt[] maxId) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(text);
        File imagePath = new File(getDayImageDir(cal));
        int curIndex = 0;
        while (true) {
            curIndex = text.indexOf("[[[", curIndex);
            if (curIndex == -1)
                break;
            int curLast = text.indexOf("]]]", curIndex);
            if (curLast == -1)
                break;

            // We are looking for the imageNumber in order to get it back to the activity
            int indexNum = text.indexOf("image", curIndex);
            if (indexNum == -1)
                break;
            int indexNumEnd = text.indexOf(".png", curIndex);
            if (indexNumEnd == -1)
                break;
            String numImage = text.substring(indexNum + 5, indexNumEnd);
            int curNum = Integer.parseInt(numImage);
            if (curNum > maxId[0].getValue())
                maxId[0].setValue(curNum);
            //End of the looking for the image number

            String imageName = text.substring(curIndex + 3, curLast);
            File newFile = new File(imagePath, imageName);
            Bitmap bitmap = BitmapHelper.getBitmapFromFile(newFile);
            if (bitmap != null) {
                BitmapDrawable d = new BitmapDrawable(bitmap);
                d.setBounds(0, 0, d.getIntrinsicWidth()*3, d.getIntrinsicHeight()*3);
                ImageSpan span = new ImageSpan(d,text.substring(curIndex,curIndex + imageName.length() + 6) ,ImageSpan.ALIGN_BASELINE);
                builder.setSpan(span, curIndex, curIndex + imageName.length() + 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            curIndex += (imageName.length() + 6);
        }
        return builder;
    }

    public static List<String> getImagesInADay(String text) {
        List<String> listImages = new ArrayList<>();
        int curIndex = 0;
        while (true) {
            curIndex = text.indexOf("[[[", curIndex);
            if (curIndex == -1)
                break;
            int curLast = text.indexOf("]]]", curIndex);
            if (curLast == -1)
                break;
            String imageName = text.substring(curIndex + 3, curLast);
            listImages.add(imageName);
            curIndex += (imageName.length() + 6);
        }
        return listImages;
    }

    public static List<File> listImageDir(){
        return Arrays.asList(new File(getImagesDir()).listFiles());
    }

    public static List<File> listFileInImageDirectory(File file){
        return Arrays.asList(file.listFiles());
    }

    public static List<File> listFileInImageDirectories(Calendar cal){
        String path = getDayImageDir(cal);
        File directory = new File(path);
        return Arrays.asList(directory.listFiles());
    }

    public static String getImagesDir() {
        if (PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean("save_images_on_external_drive", false)) {
            return getExternalImageDir();
        } else {
            return getInternalImageDir();
        }

    }

    private static String getExternalImageDir() {
        String dir = Environment.getExternalStorageDirectory().toString() + "/rateyourday/images-days/";
        File file = new File(dir);
        boolean bool = file.mkdirs();
        return dir;
    }

    private static String getInternalImageDir() {
        String dir = App.getContext().getFilesDir().toString() + "/images-days/";
        File file = new File(dir);
        file.mkdirs();
        return dir;
    }

    public static String getDayImageDir(Calendar cal) {
        String dir = getImagesDir() + cal.get(Calendar.YEAR) + String.format("%02d",cal.get(Calendar.MONTH)) + String.format("%02d",cal.get(Calendar.DAY_OF_MONTH));
        File file = new File(dir);
        file.mkdirs();
        return dir;
    }

    public static void cleanDatas(Calendar cal, String text ) {
        List<File>  filesInDir = listFileInImageDirectories(cal);
        List<String> filesInText =  getImagesInADay(text);
        for (File i: filesInDir){
            boolean found = false;
            for (String s:filesInText){
                if(i.getName().equals(s)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                i.delete();
            }
        }
    }

    public static boolean isExternalStorageAvailable() {
        boolean mExternalStorageWriteable;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media

            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mExternalStorageWriteable = false;
        }
        return mExternalStorageWriteable;
    }

    public static void moveImages() {
        File sourceDir;
        File destDir;
        if (PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean("save_images_on_external_drive", false)) {
            destDir = new File(getInternalImageDir());
            sourceDir = new File(getExternalImageDir());
        } else {
            sourceDir = new File(getInternalImageDir());
            destDir = new File(getExternalImageDir());
        }
        try {
            copyDirectory(sourceDir, destDir);
            removeDirectory(sourceDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // If targetLocation does not exist, it will be created.
    public static void copyDirectory(File sourceLocation, File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists() && !targetLocation.mkdirs()) {
                throw new IOException("Cannot create dir " + targetLocation.getAbsolutePath());
            }

            String[] children = sourceLocation.list();
            for (int i = 0; i < children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {

            // make sure the directory we plan to store the recording in exists
            File directory = targetLocation.getParentFile();
            if (directory != null && !directory.exists() && !directory.mkdirs()) {
                throw new IOException("Cannot create dir " + directory.getAbsolutePath());
            }

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            targetLocation.setLastModified(sourceLocation.lastModified());
        }
    }

    public static void removeImageDir(Calendar cal){
        File file = new File(getDayImageDir(cal));
        try {
            removeDirectory(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeDirectory(File sourceLocation)
            throws IOException {
        if (sourceLocation.isDirectory()) {
            String[] children = sourceLocation.list();
            for (int i = 0; i < children.length; i++) {
                removeDirectory(new File(sourceLocation, children[i]));
            }
            sourceLocation.delete();
        } else {
            // make sure the directory we plan to store the recording in exists
            sourceLocation.delete();
        }
    }

    public static void getPhotoArchive(final File newFile, final Handler handler) {
        new Thread() {
            @Override
            public void run() {
                try {
                    ZipTool.zipFileAtPath(getImagesDir(), newFile.getAbsolutePath());
                } catch (FileNotFoundException e) {
                    Message msg = Message.obtain();
                    msg.what = ImportExportActivity.MSG_ID.FILENOTFOUNDEXCEPTION.ordinal();
                    msg.obj = e;
                    handler.sendMessage(msg);

                } catch (IOException e) {
                    Message msg = Message.obtain();
                    msg.what = ImportExportActivity.MSG_ID.IOEXCEPTION.ordinal();
                    msg.obj = e;
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }
    public static void importPhotos(final InputStream inputStream, final Handler handler){
        new Thread() {
            @Override
            public void run() {
                try {
                    File imagePath = new File(App.getContext().getCacheDir(), "files");
                    imagePath.mkdirs();
                    final File newFile = new File(imagePath, "photos.zip");
                    newFile.delete();
                    newFile.createNewFile();
                    FileObserver fo = new FileObserver(newFile.getAbsolutePath()) {
                        @Override
                        public void onEvent(int event, @Nullable String path) {
                            if ((event == CLOSE_WRITE) || (event == CLOSE_NOWRITE)) {
                                this.stopWatching();
                                try {
                                    ZipTool.unzip(newFile, new File(getImagesDir()));
                                    handler.sendEmptyMessage(ImportExportActivity.MSG_ID.PHOTO_ARCHIVE_IMPORTED.ordinal());
                                } catch (IOException e) {
                                    Message msg = Message.obtain();
                                    msg.what = ImportExportActivity.MSG_ID.IOEXCEPTION.ordinal();
                                    msg.obj = e;
                                    handler.sendMessage(msg);
                                }
                            }
                        }
                    };
                    fo.startWatching();
                    FileOutputStream fos = new FileOutputStream(newFile);
                    byte[] buffer = new byte[1024];
                    int bufferLength;
                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        fos.write(buffer, 0, bufferLength);
                    }
                    fos.flush();
                    fos.close();
                    inputStream.close();

                } catch (FileNotFoundException e) {
                    Message msg = Message.obtain();
                    msg.what = ImportExportActivity.MSG_ID.FILENOTFOUNDEXCEPTION.ordinal();
                    msg.obj = e;
                    handler.sendMessage(msg);

                } catch (IOException e) {
                    Message msg = Message.obtain();
                    msg.what = ImportExportActivity.MSG_ID.IOEXCEPTION.ordinal();
                    msg.obj = e;
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }
}

