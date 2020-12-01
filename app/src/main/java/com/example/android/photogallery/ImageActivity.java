package com.example.android.photogallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Intent callingIntent = getIntent();
        Bundle bundle = callingIntent.getExtras();
        ArrayList<Photo> myPhotoList = bundle.getParcelableArrayList("listPhoto");
        Log.i("SIZE", "" + myPhotoList.size());
        int position = bundle.getInt("position");

        ImageView myImageView = (ImageView)findViewById(R.id.show_main_photo);
        myImageView.setImageURI(myPhotoList.get(position).get_imageUri());
    }
}