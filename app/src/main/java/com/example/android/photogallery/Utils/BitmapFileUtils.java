package com.example.android.photogallery.Utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;


public class BitmapFileUtils {
    public static final int REQUEST_IMAGE_CAPTURE = 123;

    public static String CAMERA = "Camera";
    public static String TRASH = "Trash";
    public static String FAVORITE = "Favorite";

    private static String currentPhotoPath;

    public static File createImageFile(Activity callingActivity) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File storageDir = callingActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, imageFileName);
        try {
            /* Making sure the Pictures directory exist.*/
            storageDir.mkdir();
            storageDir.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        Log.d("mylog", "Path: " + currentPhotoPath);
        return image;
    }

    public static boolean saveImageCapturedByCameraToStorage(Activity callingActivity, String bucket) throws IOException {
        OutputStream imageOutStream;
        Bitmap bitmap = null;
        File imgCaptured = new File(currentPhotoPath);
        try {
            bitmap = MediaStore.Images.Media.getBitmap(callingActivity.getContentResolver(), Uri.fromFile(imgCaptured));
        } catch (Exception e) {
            //handle exception
        }

        if (bitmap == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME,
                    System.currentTimeMillis() + ".jpg");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/" + bucket);

            Uri uri =
                    callingActivity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values);

            imageOutStream = callingActivity.getContentResolver().openOutputStream(uri);

        } else {
            String imagesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            File image = new File(imagesDir, System.currentTimeMillis() + ".jpg");
            imageOutStream = new FileOutputStream(image);
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageOutStream);
        imageOutStream.close();

        imgCaptured.delete();
        return true;
    }
}