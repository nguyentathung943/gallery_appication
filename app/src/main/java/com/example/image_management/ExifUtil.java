package com.example.image_management;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

public class ExifUtil {
    public static int[] getVideoHW(String path){
        MediaMetadataRetriever mRetriever = new MediaMetadataRetriever();
        mRetriever.setDataSource(path);
        Bitmap frame = mRetriever.getFrameAtTime();
        int[] temp = new int[2];
        int width = frame.getWidth();
        int height = frame.getHeight();
        temp[0] = width;
        temp[1] = height;
        return temp;
    }
    public static int[] getHW(Context context, String path) throws FileNotFoundException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Uri photoURI = FileProvider.getUriForFile(context, "com.example.android.fileprovider", new File(path));
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(
                context.getContentResolver().openInputStream(photoURI),
                null,
                options);
        int[] temp = new int[2];
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        temp[0] = imageHeight;
        temp[1] = imageWidth;
        return temp;
    }
    public static Bitmap rotateBitmap(String src, Bitmap bitmap) {
        try {
            int orientation = getExifOrientation(src);

            if (orientation == 1) {
                return bitmap;
            }

            Matrix matrix = new Matrix();
            switch (orientation) {
                case 2:
                    matrix.setScale(-1, 1);
                    break;
                case 3:
                    matrix.setRotate(180);
                    break;
                case 4:
                    matrix.setRotate(180);
                    matrix.postScale(-1, 1);
                    break;
                case 5:
                    matrix.setRotate(90);
                    matrix.postScale(-1, 1);
                    break;
                case 6:
                    matrix.setRotate(90);
                    break;
                case 7:
                    matrix.setRotate(-90);
                    matrix.postScale(-1, 1);
                    break;
                case 8:
                    matrix.setRotate(-90);
                    break;
                default:
                    return bitmap;
            }

            try {
                Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.recycle();
                return oriented;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private static int getExifOrientation(String src) throws IOException {
        int orientation = 1;

        try {
            /**
             * if your are targeting only api level >= 5
             * ExifInterface exif = new ExifInterface(src);
             * orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
             */
            if (Build.VERSION.SDK_INT >= 5) {
                Class<?> exifClass = Class.forName("android.media.ExifInterface");
                Constructor<?> exifConstructor = exifClass.getConstructor(new Class[] { String.class });
                Object exifInstance = exifConstructor.newInstance(new Object[] { src });
                Method getAttributeInt = exifClass.getMethod("getAttributeInt", new Class[] { String.class, int.class });
                Field tagOrientationField = exifClass.getField("TAG_ORIENTATION");
                String tagOrientation = (String) tagOrientationField.get(null);
                orientation = (Integer) getAttributeInt.invoke(exifInstance, new Object[] { tagOrientation, 1});
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return orientation;
    }

}
