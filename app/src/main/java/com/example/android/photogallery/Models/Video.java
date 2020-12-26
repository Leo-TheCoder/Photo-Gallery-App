package com.example.android.photogallery.Models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.photogallery.MainActivity;

import java.util.Date;

public class Video implements Parcelable {
    private String _bucket;
    private Uri _videoUri;
    private Date _date;
    private Long _size;
    private String _displayName;

    public static final String DATE_FORMAT = "dd-MM-yyyy";

    public Video(){}

    public Video(String bucket, Uri videoUri, Date date, Long size, String displayName) {
        _bucket = bucket;
        _videoUri = videoUri;
        _date = date;
        _size = size;
        _displayName = displayName;
    }

    protected Video(Parcel in) {
        _bucket = in.readString();
        _videoUri = in.readParcelable(Uri.class.getClassLoader());
        if (in.readByte() == 0) {
            _size = null;
        } else {
            _size = in.readLong();
        }
        _displayName = in.readString();
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    public String get_bucket() { return _bucket; }

    public Uri get_videoUri() { return _videoUri; }

    public Date get_date() { return _date; }

    public void set_bucket(String _bucket) { this._bucket = _bucket; }

    public void set_videoUri(Uri _videoUri) { this._videoUri = _videoUri; }

    public void set_date(Date _date) { this._date = _date; }

    public void set_size(long _size) { this._size = _size; }

    public Long get_size() { return _size; }

    public String get_displayName() { return _displayName; }

    public void set_displayName(String _displayName) { this._displayName = _displayName; }

    public String get_dateTitle() { return MainActivity.Formatter.format(_date); }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(_bucket);
        dest.writeParcelable(_videoUri, flags);
        if (_size == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(_size);
        }
        dest.writeString(_displayName);
    }
}
