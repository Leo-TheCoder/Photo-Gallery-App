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

import com.example.android.photogallery.MainActivity;
import com.example.android.photogallery.Models.Photo;
import com.example.android.photogallery.Models.PhotoCategory;
import com.example.android.photogallery.Models.Video;
import com.example.android.photogallery.Models.VideoCategory;
import com.example.android.photogallery.Utils.PhotoUtils;
import com.example.android.photogallery.Utils.VideoUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class MediaTrackerService extends Service {
    private ContentObserver contentObserverImage;
    private ContentObserver contentObserverVideo;
    public final static String SERVICE_ACTION_CODE_IMAGE = "android.action.UPDATE_IMAGE";
    public final static String SERVICE_ACTION_CODE_VIDEO = "android.action.UPDATE_VIDEO";
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
        getContentResolver().registerContentObserver(PhotoUtils.EXTERNAL_URI, true, contentObserverImage);
        getContentResolver().registerContentObserver(VideoUtils.EXTERNAL_URI, true, contentObserverVideo);

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        contentObserverImage = new ContentObserver(new Handler(Looper.myLooper())) {
            private void addPhotoToDateList(ArrayList<PhotoCategory> datePhotos, Photo photo) throws ParseException {
                Date dateCategory;
                Date dateNewPhoto = null;
                dateNewPhoto = MainActivity.Formatter.parse(photo.get_dateTitle());

                boolean flag = false;
                for (int i = 0; i < datePhotos.size(); i++) {
                    dateCategory = MainActivity.Formatter.parse(datePhotos.get(i).get_title());
                    if (dateNewPhoto.equals(dateCategory)) {
                        datePhotos.get(i).addPhoto(photo);
                        flag = true;
                        break;
                    } else if (dateNewPhoto.after(dateCategory)) {
                        PhotoCategory newPhotoDate = new PhotoCategory(
                                PhotoCategory.CATEGORY_DATE,
                                photo.get_dateTitle(),
                                new ArrayList<Photo>(), null);
                        newPhotoDate.addPhoto(photo);
                        datePhotos.add(i, newPhotoDate);
                        flag = true;
                        break;
                    }
                }
                if (flag == false) {
                    PhotoCategory newPhotoDate = new PhotoCategory(
                            PhotoCategory.CATEGORY_DATE,
                            photo.get_dateTitle(),
                            new ArrayList<Photo>(),
                            null);
                    newPhotoDate.addPhoto(photo);
                    datePhotos.add(newPhotoDate);
                }
            }

            private void addPhotoToAlbumList(ArrayList<PhotoCategory> albumPhotos, Photo photo) {
                boolean flag = false;
                String photoTitle = photo.get_bucket();
                if(photo.is_favorite()) {
                    photoTitle = "Favorite";
                }

                for (int i = 0; i < albumPhotos.size(); i++) {
                    if (photoTitle.equals(albumPhotos.get(i).get_title())) {
                        albumPhotos.get(i).addPhoto(photo);
                        flag = true;
                        break;
                    }
                }
                if (flag == false) {
                    PhotoCategory newPhotoAlbum = new PhotoCategory(
                            PhotoCategory.CATEGORY_BUCKET,
                            photoTitle,
                            new ArrayList<Photo>(),
                            null);
                    newPhotoAlbum.addPhoto(photo);
                    albumPhotos.add(newPhotoAlbum);
                }
            }
            /**
             * Function for all API
             * @param selfChange
             */
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                Log.e("SERVICE", "Something have been changed!");
                PhotoUtils.getAllImageFromExternal(getApplicationContext());
                ArrayList<Photo> myUpdateList = PhotoUtils.getImagesFromExternal();
                ArrayList<PhotoCategory> datePhotos = new ArrayList<PhotoCategory>();
                ArrayList<PhotoCategory> albumPhotos = new ArrayList<PhotoCategory>();

                for(int i = 0; i < myUpdateList.size(); i++) {
                    try {
                        addPhotoToDateList(datePhotos, myUpdateList.get(i));
                        addPhotoToAlbumList(albumPhotos, myUpdateList.get(i));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                Intent broadcast = new Intent(SERVICE_ACTION_CODE_IMAGE);
                Bundle sendBundle = new Bundle();
                sendBundle.putParcelableArrayList("dateList", datePhotos);
                sendBundle.putParcelableArrayList("albumList", albumPhotos);
                broadcast.putExtras(sendBundle);
                sendBroadcast(broadcast);
            }
        };
        contentObserverVideo = new ContentObserver(new Handler(Looper.myLooper())) {
            private void addOneVideoDateCategory(ArrayList<VideoCategory> dateVideos, Video newVideo) throws ParseException {
                Date dateCategory;
                Date dateNewVideo = MainActivity.Formatter.parse(newVideo.get_dateTitle());
                boolean flag = false;
                for (int i = 0; i < dateVideos.size(); i++) {
                    dateCategory = MainActivity.Formatter.parse(dateVideos.get(i).get_title());
                    if (dateNewVideo.equals(dateCategory)) {
                        dateVideos.get(i).addVideo(newVideo);
                        flag = true;
                        break;
                    } else if (dateNewVideo.after(dateCategory)) {
                        VideoCategory newVideoDate = new VideoCategory(
                                VideoCategory.CATEGORY_DATE,
                                newVideo.get_dateTitle(),
                                new ArrayList<Video>(),
                                null);
                        newVideoDate.addVideo(newVideo);
                        dateVideos.add(i, newVideoDate);
                        flag = true;
                        break;
                    }
                }
                if (flag == false) {
                    VideoCategory newVideoDate = new VideoCategory(
                            VideoCategory.CATEGORY_DATE,
                            newVideo.get_dateTitle(),
                            new ArrayList<Video>(),
                            null);
                    newVideoDate.addVideo(newVideo);
                    dateVideos.add(newVideoDate);
                }
            }

            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                VideoUtils.getAllVideoFromExternal(getApplicationContext());
                ArrayList<Video> myUpdateList = VideoUtils.getVideosFromExternal();
                ArrayList<VideoCategory> dateVideos = new ArrayList<VideoCategory>();

                for(int i = 0; i< myUpdateList.size(); i++) {
                    try {
                        addOneVideoDateCategory(dateVideos, myUpdateList.get(i));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                Intent broadcast = new Intent(SERVICE_ACTION_CODE_VIDEO);
                Bundle sendBundle = new Bundle();
                sendBundle.putParcelableArrayList("videoList", dateVideos);
                broadcast.putExtras(sendBundle);
                sendBroadcast(broadcast);
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(contentObserverImage);
        getContentResolver().unregisterContentObserver(contentObserverVideo);
    }
}
