package com.example.android.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.SimpleFormatter;

public class Photo {
    private String _bucket;
    private Uri _imageUri;
    private Date _date;
    public static final String DATE_FORMAT = "dd-MM-yyyy";


    public Photo(){}

    public Photo(String bucket, Date date, Uri imageUri)
    {
        _bucket=bucket;
        _date=date;
        _imageUri=imageUri;
    }


    public String get_bucket() {
        return _bucket;
    }

    public Date get_date() {
        return _date;
    }

    public Uri get_imageUri() {
        return _imageUri;
    }

    public void set_bucket(String _bucket) {
        this._bucket = _bucket;
    }

    public void set_imageUri(Uri _imageUri) {
        this._imageUri = _imageUri;
    }

    public void set_date(Date _date) {
        this._date = _date;
    }

    public String get_dateTitle() {
        return MainActivity.Formatter.format(_date);
    }

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
}
