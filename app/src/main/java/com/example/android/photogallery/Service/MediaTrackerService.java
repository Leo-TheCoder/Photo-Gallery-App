package com.example.android.photogallery.Service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.android.photogallery.Models.Photo;
import com.example.android.photogallery.Utils.PhotoUtils;

import java.util.Date;

public class MediaTrackerService extends Service {
    private ContentObserver contentObserver;
    public final static String SERVICE_ACTION_CODE = "android.action.UPDATE";
    public MediaTrackerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Register Content Observer for observe there is some image's changes !
        getContentResolver().registerContentObserver(PhotoUtils.EXTERNAL_URI, true, contentObserver);


        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        contentObserver = new ContentObserver(new Handler(Looper.myLooper())) {
            /**
             * Function for all API
             * @param selfChange
             */
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                String[] projection = {MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.BUCKET_ID,
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                        MediaStore.Images.Media.DATE_TAKEN,
                        MediaStore.Images.Media.DATE_MODIFIED,
                        MediaStore.Images.Media.DATE_ADDED};

                Cursor cursor = getContentResolver().query(PhotoUtils.EXTERNAL_URI, projection, null, null, null);
                //Still update
            }

            /**
             * Function for API >= 16
             * @param selfChange
             * @param uri
             */
            @Override
            public void onChange(boolean selfChange, @Nullable Uri uri) {
                super.onChange(selfChange, uri);
                String[] projection = {MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.BUCKET_ID,
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                        MediaStore.Images.Media.DATE_TAKEN,
                        MediaStore.Images.Media.DATE_MODIFIED,
                        MediaStore.Images.Media.DATE_ADDED};

                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

                Intent broadcast = new Intent(SERVICE_ACTION_CODE);
                Bundle sendBundle = new Bundle();

                Log.i("SERVICE", "onChange: " + uri);
                if (cursor.moveToFirst()) {
                    String id, bucket;
                    Long dateTaken = 0L, dateModif = 0L, dateAdded = 0L;

                    //get column Index
                    int bucketColumn = cursor.getColumnIndex(
                            MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                    int dateTakenColumn = cursor.getColumnIndex(
                            MediaStore.Images.Media.DATE_TAKEN);
                    int dateModifiedColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED);
                    int dateAddedColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
                    int idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID);

                    bucket = cursor.getString(bucketColumn);

                    dateTaken = cursor.getLong(dateTakenColumn);
                    dateModif = cursor.getLong(dateModifiedColumn);
                    dateAdded = cursor.getLong(dateAddedColumn);

                    id = cursor.getString(idColumn);
                    Log.i("TEST", "date= " + dateTaken + " bucket= " + bucket + " id= " + id);


                    Date DateFromEpocTime = null;
                    if (dateAdded != 0) {
                        DateFromEpocTime = new Date(dateAdded * 1000L);
                    } else if (dateTaken != 0) {
                        DateFromEpocTime = new Date(dateTaken);
                    } else if (dateModif != 0) {
                        DateFromEpocTime = new Date(dateModif * 1000L);
                    }


                    sendBundle.putParcelable("photo", new Photo(bucket, DateFromEpocTime, uri));
                    sendBundle.putString("key", "add");
                    broadcast.putExtras(sendBundle);
                }
                else {
                    sendBundle.putString("key", "remove");
                    sendBundle.putString("uri", "" + uri);
                    broadcast.putExtras(sendBundle);
                }
                sendBroadcast(broadcast);
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(contentObserver);
    }
}