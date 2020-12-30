package com.example.android.photogallery.Models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.photogallery.MainActivity;

import java.text.ParseException;
import java.util.Date;

public class Photo implements Parcelable {
    private String _bucket;
    private Uri _imageUri;
    private Date _date;
    private boolean _favorite;
    private String _displayName;
    private String _relativePath;
    private long _size;
    public static final String DATE_FORMAT = "dd-MM-yyyy";
    public static final String DATE_FORMAT_DETAIL = "dd-MM-yyyy hh:mm a";


    public Photo(){}

    public Photo(String bucket, Date date, Uri imageUri, boolean favorite,
                 String displayName, String relativePath, Long size)
    {
        _bucket=bucket;
        _date=date;
        _imageUri=imageUri;
        _favorite = favorite;
        _displayName = displayName;
        _relativePath = relativePath;
        _size = size;
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public String get_bucket() {
        return _bucket;
    }

    public Date get_date() {
        return _date;
    }

    public Uri get_imageUri() {
        return _imageUri;
    }

    public String get_displayName() {return _displayName;}

    public String get_relativePath() {return  _relativePath;}

    public Long get_size() {return _size;}

    public void set_bucket(String _bucket) {
        this._bucket = _bucket;
    }

    public void set_imageUri(Uri _imageUri) {
        this._imageUri = _imageUri;
    }

    public void set_date(Date _date) {
        this._date = _date;
    }

    public void set_favorite(boolean isFavorite) {this._favorite = isFavorite;}

    public boolean is_favorite() { return _favorite; }

    public String get_dateTitle() {
        return MainActivity.Formatter.format(_date);
    }

    public String get_dateHourTitle() { return MainActivity.FormatterDetail.format(_date);}

    public boolean takenBeforeByDay(Photo photo){
        String dateTitle1 = this.get_dateTitle();
        String dateTitle2 = photo.get_dateTitle();
        Date date1, date2;
        try {
            date1 = MainActivity.Formatter.parse(dateTitle1);
            date2 = MainActivity.Formatter.parse(dateTitle2);
        } catch (ParseException e) {
            e.printStackTrace();
            date1 = null;
            date2 = null;
        }
        return date1.before(date2);

    }
    public boolean takenAfterByDay(Photo photo){
        String dateTitle1 = this.get_dateTitle();
        String dateTitle2 = photo.get_dateTitle();
        Date date1, date2;
        try {
            date1 = MainActivity.Formatter.parse(dateTitle1);
            date2 = MainActivity.Formatter.parse(dateTitle2);
        } catch (ParseException e) {
            e.printStackTrace();
            date1 = null;
            date2 = null;
        }
        return date1.after(date2);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_bucket);
        dest.writeParcelable(_imageUri, flags);
        dest.writeLong(_date.getTime());
        dest.writeBoolean(_favorite);
        dest.writeString(_displayName);
        dest.writeString(_relativePath);
        dest.writeLong(_size);
    }

    protected Photo(Parcel in) {
        _bucket = in.readString();
        _imageUri = in.readParcelable(Uri.class.getClassLoader());
        _date = new Date(in.readLong());
        _favorite = in.readBoolean();
        _displayName = in.readString();
        _relativePath = in.readString();
        _size = in.readLong();
    }

    public String getTheFirstLetterName() {
        return _displayName.substring(0,1);
    }

}
