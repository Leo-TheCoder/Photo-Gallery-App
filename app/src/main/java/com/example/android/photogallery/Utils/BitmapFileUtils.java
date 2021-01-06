package com.example.android.photogallery.Utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.example.android.photogallery.PhotoDisplayActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class BitmapFileUtils {
    public static final int REQUEST_IMAGE_CAPTURE = 123;

    public static String CAMERA = "Camera";
    public static String TRASH = "Trash";
    public static String FAVORITE = "Favorite";

    private static String currentPhotoPath;

    //getting real path from uri
    public static String getFilePath(Activity callingActivity, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = callingActivity.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(projection[0]);
            String picturePath = cursor.getString(columnIndex); // returns null
            cursor.close();
            return picturePath;
        }
        return null;
    }

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

    /**
     * move image to another bucket (folder)
     * @param callingActivity the activity that call this function
     * @param imageUri
     * @param bucket bucket name
     * @return success or not of this function
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public static boolean moveImageToAnotherBucket(Activity callingActivity, Uri imageUri, String bucket) {
        ContentResolver resolver = callingActivity.getApplicationContext().getContentResolver();

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_MODIFIED,System.currentTimeMillis());
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/" + bucket);
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                resolver.update(
                        imageUri,values,null);
            }
        }
        catch (SecurityException e) {
            Log.e("Update FAILED", "" + e.getMessage());
            return false;
        }
        return true;
    }
    public static class RecoverableSecurityExceptionExt extends SecurityException {

        private final PendingIntent pIntent;

        public RecoverableSecurityExceptionExt(PendingIntent pIntent) {
            this.pIntent = pIntent;
        }

        public PendingIntent getPIntent() {
            return pIntent;
        }
    }


    public static void queryTrashImages(Activity callingActivity) {
        Bundle bundle = new Bundle();
        bundle.putString("query",MediaStore.QUERY_ARG_MATCH_TRASHED);
        Cursor query = null;

        String[] projection = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.IS_TRASHED};
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            query = callingActivity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                     bundle,
                     null);
        }
        if (query.moveToFirst()) {
            String id,bucket,isTrash;
            int idColumn = query.getColumnIndex(
                    MediaStore.Images.Media._ID);
            int isTrashCol = query.getColumnIndex(
                    MediaStore.Images.Media.IS_TRASHED);
            do {
                id = query.getString(idColumn);
                isTrash = query.getString(isTrashCol);
                Log.i("Hello", "date= " + " bucket= " + isTrash  + " id= " + id);
            } while (query.moveToNext());
        }
        query.close();

    }
}
