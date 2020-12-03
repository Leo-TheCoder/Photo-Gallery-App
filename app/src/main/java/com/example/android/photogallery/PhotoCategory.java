package com.example.android.photogallery;

import android.content.Context;

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
        _photosList.add(0, photo);
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
