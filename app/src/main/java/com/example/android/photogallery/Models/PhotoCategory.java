package com.example.android.photogallery.Models;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

public class PhotoCategory {
    public static final int CATEGORY_DATE = 1;
    public static final int CATEGORY_BUCKET = 2;

    private int _category;
    private String _title;
    private ArrayList<Photo> _photosList;
    private Context _context;

    public PhotoCategory(){}

    public PhotoCategory(int category, String title, ArrayList<Photo> photosList, Context context) {
        _category = category;
        _photosList = photosList;
        _title = title;
        _context = context;
    }

    public void addPhoto(Photo photo) {
        if(_photosList.isEmpty()) {
           _photosList.add(photo);
        }
        else {
            Uri lastImgUri = _photosList.get(0).get_imageUri();
            Uri newImgUri = photo.get_imageUri();
            if(!lastImgUri.equals(newImgUri)){
                _photosList.add(0, photo);
            }
            else {
                _photosList.set(0, photo);
            }
        }
    }

    public boolean removeByUri(Uri uri) {
        boolean result = false;
        for(int i = 0; i < _photosList.size(); i++) {
            Uri imageUri = _photosList.get(i).get_imageUri();
            if(imageUri.equals(uri)) {
                _photosList.remove(i);
                result = true;
                break;
            }
        }
        return result;
    }

    public ArrayList<Photo> get_photosList() {
        return _photosList;
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

    public void set_photosList(ArrayList<Photo> _photosList) {
        this._photosList = _photosList;
    }

    public Context get_context() {
        return _context;
    }
}
