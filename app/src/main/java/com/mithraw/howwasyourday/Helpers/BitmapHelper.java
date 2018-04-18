package com.mithraw.howwasyourday.Helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

import com.mithraw.howwasyourday.App;
import com.mithraw.howwasyourday.Tools.MyInt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
        File imagePath = new File(getImageDir(cal));
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
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
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
    public static List<File> listFileInImageDirectories(Calendar cal){
        String path = getImageDir(cal);
        File directory = new File(path);
        return Arrays.asList(directory.listFiles());
    }
    public static String getImageDir(Calendar cal){
        return App.getContext().getFilesDir().toString() + "/images-days/" + cal.get(Calendar.YEAR) + cal.get(Calendar.MONTH) + cal.get(Calendar.DAY_OF_MONTH);
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
}
