package com.example.android.photogallery.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.android.photogallery.Models.Photo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class PhotoUtils {
    //region const, static variables
    private static final String LOG_TAG = "Photo Loader: ";

    public static final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 1000;

    public static final Uri EXTERNAL_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    //endregion

    //region local variables

    private static ArrayList<Photo> photosList = new ArrayList<>();
    private static ArrayList<Photo> trashPhotoList = new ArrayList<>();
    private static ArrayList<Uri> imageUrisList = new ArrayList<>();
    private static ArrayList<Photo> fakePhotoList =  new ArrayList<>();
    private static ArrayList<String> bucketList =  new ArrayList<>();

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
    }

    /**
     * @important This function should be run in a background thread in order not to affect the user experience
     * This function access to external Storage and store all the image in an ArrayList photosList
     * @param context the current context that call this function
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void getAllImageFromExternal(Context context){
        photosList.clear();
        Uri allImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        Log.i(LOG_TAG, "URI: " + allImageUri);

        String[] projection = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.IS_FAVORITE,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.RELATIVE_PATH};

        Cursor cursor = context.getContentResolver().query(allImageUri, projection, null, null, null);

        Log.i(LOG_TAG, " query count=" + cursor.getCount());

        if (cursor.moveToFirst()) {
            String id,bucket,isTrash, isFavorite,prevBucket = "";
            String displayName, relativePath;
            Long dateTaken = 0L,dateModif = 0L,dateAdded = 0L, size = 0L;

            //get column Index
            int bucketColumn = cursor.getColumnIndex(
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            int dateTakenColumn = cursor.getColumnIndex(
                    MediaStore.Images.Media.DATE_TAKEN);

            int dateModifiedColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED);

            int dateAddedColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);

            int idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID);

            int favColumn = cursor.getColumnIndex(MediaStore.Images.Media.IS_FAVORITE);

            int displayNameColumn = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);

            int relativePathColumn = cursor.getColumnIndex(MediaStore.Images.Media.RELATIVE_PATH);

            int sizeColumn = cursor.getColumnIndex(MediaStore.Images.Media.SIZE);

            do {
                // Get the field values
                bucket = cursor.getString(bucketColumn);

                dateTaken = cursor.getLong(dateTakenColumn);
                dateModif = cursor.getLong(dateModifiedColumn);
                dateAdded = cursor.getLong(dateAddedColumn);
                isFavorite = cursor.getString(favColumn);
                displayName = cursor.getString(displayNameColumn);
                relativePath = cursor.getString(relativePathColumn);
                size = cursor.getLong(sizeColumn);
                boolean isFav = isFavorite.equals("1");
                id = cursor.getString(idColumn);
                Log.i(LOG_TAG, "date= " + dateTaken + " bucket= " + bucket + " id= " + id + " favorite= " + isFavorite);

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

                if (!prevBucket.equals(bucket)){
                    bucketList.add(bucket);
                    prevBucket = bucket;
                    Log.e("TAG",prevBucket);
                }

                Photo newPhoto = new Photo(bucket, DateFromEpocTime, imageUri, isFav, displayName, relativePath, size);
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

    /**
     * @return ArrayList of all bucket
     */
    public static ArrayList<String> getBucketList() {
        return bucketList;
    }
    //end region

    public static void queryTrashImages(Activity callingActivity) {
        Uri allImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        Bundle bundle = new Bundle();
        String[] projection = new String[0];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            bundle.putInt(MediaStore.QUERY_ARG_MATCH_TRASHED,MediaStore.MATCH_INCLUDE);
            projection = new String[]{MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.BUCKET_ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_TAKEN,
                    MediaStore.Images.Media.DATE_MODIFIED,
                    MediaStore.Images.Media.IS_TRASHED};
        }
        Cursor cursor = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cursor = callingActivity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    bundle,
                    null);
        }
        if (cursor.moveToFirst()) {
            String id,bucket,isTrash;
            Long dateTaken = 0L,dateModif = 0L,dateAdded = 0L;
            int idColumn = cursor.getColumnIndex(
                    MediaStore.Images.Media._ID);
            int isTrashCol = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                isTrashCol = cursor.getColumnIndex(
                        MediaStore.Images.Media.IS_TRASHED);
            }
            //get column Index
            int bucketColumn = cursor.getColumnIndex(
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            int dateTakenColumn = cursor.getColumnIndex(
                    MediaStore.Images.Media.DATE_TAKEN);

            int dateModifiedColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED);

            int dateAddedColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);

            // Get the field values
            bucket = cursor.getString(bucketColumn);

            dateTaken = cursor.getLong(dateTakenColumn);
            dateModif = cursor.getLong(dateModifiedColumn);
            dateAdded = cursor.getLong(dateAddedColumn);


            do {
                id = cursor.getString(idColumn);
                isTrash = cursor.getString(isTrashCol);
                Log.i(LOG_TAG," Trash= " + isTrash  + " id= " + id);

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

                Photo newPhoto = new Photo(bucket, DateFromEpocTime, imageUri, false, null, null, null);
                trashPhotoList.add(newPhoto);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
    /**
     * @return ArrayList of all Loaded Photo
     */
    public static ArrayList<Photo> getTrashImages() {
        return trashPhotoList;
    }

    //fake image list
    public static void generateFakeImageList(Activity callingActivity) {
        ArrayList<Photo> fakeImageList = new ArrayList<>();
        Resources resources = callingActivity.getResources();

        for (int i = 1; i<=10;i++){
            String name = "fake"+i;
            int id = callingActivity.getResources().getIdentifier(name, "drawable", callingActivity.getPackageName());
            Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    resources.getResourcePackageName(id) + '/' +
                    resources.getResourceTypeName(id) + '/' +
                    resources.getResourceEntryName(id) );
            Photo newPhoto = new Photo("Camera",new Date(System.currentTimeMillis()), imageUri,false, null, null, null);
            fakePhotoList.add(newPhoto);
        }
    }
    /**
     * @return ArrayList of all Loaded Photo
     */
    public static ArrayList<Photo> getFakeImages() {
        return fakePhotoList;
    }
    //end region
}
