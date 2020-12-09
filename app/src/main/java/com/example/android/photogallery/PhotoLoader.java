package com.example.android.photogallery;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.CpuUsageInfo;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PhotoLoader {
    //region const, static variables
    private static final String LOG_TAG = "Photo Loader: ";

    public static final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 1000;
    public static final int WRITE_EXTERNAL_STORAGE_PERMISSION_CODE  = 2000;

    public static final Uri EXTERNAL_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    //endregion

    //region local variables

    private static ArrayList<Photo> photosList = new ArrayList<>();
    private static ArrayList<Uri> imageUrisList = new ArrayList<>();

    //end region

    //region methods

    /**
     * function check the External Storage Access Permission
     * @param callingActivity the activity that call this function
     */
    public static void externalStoragePermissionCheck(Activity callingActivity){
        //check for permission
        if(ContextCompat.checkSelfPermission(callingActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            ActivityCompat.requestPermissions(callingActivity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_PERMISSION_CODE);
        }

        if(ContextCompat.checkSelfPermission(callingActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            ActivityCompat.requestPermissions(callingActivity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_PERMISSION_CODE);
        }
    }

    /**
     * @important This function should be run in a background thread in order not to affect the user experience
     * This function access to external Storage and store all the image in an ArrayList photosList
     * @param context the current context that call this function
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void getAllImageFromExternal(Context context){
        Uri allImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        Log.i(LOG_TAG, "URI: " + allImageUri);

        String[] projection = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.DATE_ADDED};

        Cursor cursor = context.getContentResolver().query(allImageUri, projection, null, null, null);

        Log.i(LOG_TAG, " query count=" + cursor.getCount());

        if (cursor.moveToFirst()) {
            String id,bucket;
            Long dateTaken = 0L,dateModif = 0L,dateAdded = 0L;

            //get column Index
            int bucketColumn = cursor.getColumnIndex(
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            int dateTakenColumn = cursor.getColumnIndex(
                    MediaStore.Images.Media.DATE_TAKEN);

            int dateModifiedColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED);

            int dateAddedColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);

            int idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID);

            do {
                // Get the field values
                bucket = cursor.getString(bucketColumn);
                dateTaken = cursor.getLong(dateTakenColumn);
                dateModif = cursor.getLong(dateModifiedColumn);
                dateAdded = cursor.getLong(dateAddedColumn);


                id = cursor.getString(idColumn);
                Log.i(LOG_TAG, "date= " + dateTaken + " bucket= " + bucket + " id= " + id);

                // Do something with the values.
                String dateTime = "Date taken not found!!!";
                Uri imageUri = Uri.withAppendedPath(allImageUri, id);

                Date DateFromEpocTime = null;
                 if (dateAdded != 0) {
                    DateFromEpocTime = new Date(dateAdded*1000L);
                } else if (dateTaken != 0) {
                    DateFromEpocTime = new Date(dateTaken);
                }
                else if (dateModif != 0){
                    DateFromEpocTime = new Date(dateModif*1000L);
                }
                //dateTime = formatter.format(DateFromEpocTime);

                Log.i(LOG_TAG, "dateTime= " + dateTime);
                // Add new loaded photo

                Photo newPhoto = new Photo(bucket, DateFromEpocTime, imageUri);
                photosList.add(newPhoto);
                imageUrisList.add(allImageUri);

            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    /**
     * @return ArrayList of all Loaded Photo
     */
    public static ArrayList<Photo> getImagesFromExternal() {
        return photosList;
    }
    //end region
}
