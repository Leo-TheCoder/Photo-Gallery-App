package com.example.android.photogallery.Models;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class VideoCategory implements Parcelable{
    public static final int CATEGORY_DATE = 1;
    public static final int CATEGORY_BUCKET = 2;

    private int _category;
    private String _title;
    private ArrayList<Video> _videosList;
    private Context _context;

    public VideoCategory(){}

    public VideoCategory(int category, String title, ArrayList<Video> videosList, Context context) {
        _category = category;
        _videosList = videosList;
        _title = title;
        _context = context;
    }

    protected VideoCategory(Parcel in) {
        this();
        _videosList = new ArrayList<Video>();
        _category = in.readInt();
        _title = in.readString();
        in.readTypedList(_videosList, Video.CREATOR);
    }


    public static final Parcelable.Creator<VideoCategory> CREATOR = new Parcelable.Creator<VideoCategory>() {
        @Override
        public VideoCategory createFromParcel(Parcel in) {
            return new VideoCategory(in);
        }

        @Override
        public VideoCategory[] newArray(int size) {
            return new VideoCategory[size];
        }
    };

    public void addVideo(Video video) {
        if(_videosList.isEmpty()) {
            _videosList.add(video);
        }
        else {
            Uri lastVidUri = _videosList.get(0).get_videoUri();
            Uri newVidUri = video.get_videoUri();
            if(!lastVidUri.equals(newVidUri)){
                _videosList.add(0, video);
            }
            else {
                _videosList.set(0, video);
            }
        }
    }

    public boolean removeByUri(Uri uri) {
        boolean result = false;
        for(int i = 0; i < _videosList.size(); i++) {
            Uri imageUri = _videosList.get(i).get_videoUri();
            if(imageUri.equals(uri)) {
                _videosList.remove(i);
                result = true;
                break;
            }
        }
        return result;
    }

    public ArrayList<Video> get_videosList() {
        return _videosList;
    }

    public String get_title() {
        return _title;
    }

    public void set_title(String _title) {
        this._title = _title;
    }

    public int get_category() {
        return _category;
    }

    public void set_category(int _category) {
        this._category = _category;
    }

    public void set_videosList(ArrayList<Video> _videosList) {
        this._videosList = _videosList;
    }

    public Context get_context() {
        return _context;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_category);
        dest.writeString(_title);
        dest.writeTypedList(_videosList);
    }
}
