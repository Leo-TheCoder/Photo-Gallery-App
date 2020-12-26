package com.example.android.photogallery.Utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.example.android.photogallery.Models.Photo;
import com.example.android.photogallery.Models.Video;

import java.util.ArrayList;
import java.util.Date;

public class VideoUtils {
    private static final String LOG_TAG = "VIDEO_UTILS: ";
    private static ArrayList<Video> videoList = new ArrayList<Video>();
    public static final Uri EXTERNAL_URI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

    public static void getAllVideoFromExternal(Context context) {
        videoList.clear();
        Uri allVideo = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String projection[] = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.BUCKET_ID,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.DATE_MODIFIED,
                MediaStore.Video.Media.DATE_TAKEN,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION
        };

        Cursor cursor = context.getContentResolver().query(allVideo, projection, null, null, null);

        if (cursor.moveToFirst()) {
            String id,bucket, displayName, duration;
            Long dateTaken = 0L,dateModif = 0L,dateAdded = 0L, size = 0L;

            //get column Index
            int bucketColumn = cursor.getColumnIndex(
                    MediaStore.Video.Media.BUCKET_DISPLAY_NAME);

            int dateTakenColumn = cursor.getColumnIndex(
                    MediaStore.Video.Media.DATE_TAKEN);

            int dateModifiedColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED);

            int dateAddedColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED);

            int idColumn = cursor.getColumnIndex(MediaStore.Video.Media._ID);

            int sizeColumn = cursor.getColumnIndex(MediaStore.Video.Media.SIZE);

            int displayNameColumn = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME);

            int durationColumn = cursor.getColumnIndex(MediaStore.Video.Media.DURATION);

            do {
                // Get the field values
                bucket = cursor.getString(bucketColumn);

                dateTaken = cursor.getLong(dateTakenColumn);
                dateModif = cursor.getLong(dateModifiedColumn);
                dateAdded = cursor.getLong(dateAddedColumn);
                size = cursor.getLong(sizeColumn);
                displayName = cursor.getString(displayNameColumn);
                duration = cursor.getString(durationColumn);
                id = cursor.getString(idColumn);
                //Log.i(LOG_TAG, "date= " + dateTaken + " bucket= " + bucket + " id= " + id + " favorite= " + isFavorite);

                // Do something with the values.
                String dateTime = "Date taken not found!!!";
                Uri videoUri = Uri.withAppendedPath(allVideo, id);

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
                Log.e(LOG_TAG, "bucket= " + bucket+ " videoUri= " + videoUri + " date= " + DateFromEpocTime +
                        " size= " + size + " displayName= " + displayName);
                Video newVideo = new Video(bucket, videoUri, DateFromEpocTime, size, displayName, duration);
                videoList.add(newVideo);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public static ArrayList<Video> getVideosFromExternal() {
        return videoList;
    }

    public static int findIndexOfVideo(Video video) {
        int result = -1;
        Uri thisUri = video.get_videoUri();
        for(int i =0; i < videoList.size(); i ++) {
            Uri uri = videoList.get(i).get_videoUri();
            if(uri.equals(thisUri)){
                result = i;
                break;
            }
        }
        return result;
    }
}
