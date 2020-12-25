package com.example.android.photogallery.Utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class BitmapFileUtils {
    public static final int REQUEST_IMAGE_CAPTURE = 123;

    public static String CAMERA = "Camera";
    public static String TRASH = "Trash";
    public static String FAVORITE = "Favorite";

    public static void saveImageToStorage(Activity callingActivity, Bitmap bitmap, String bucket) throws IOException {
        OutputStream imageOutStream;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME,
                    System.currentTimeMillis() + ".jpg");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH,"DCIM/" + bucket);

            Uri uri =
                    callingActivity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values);

            imageOutStream = callingActivity.getContentResolver().openOutputStream(uri);

        } else {

            String imagesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES). toString();
            File image = new File(imagesDir,  System.currentTimeMillis() + ".jpg");
            imageOutStream = new FileOutputStream(image);
        }


        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageOutStream);
        imageOutStream.close();


    }
}
