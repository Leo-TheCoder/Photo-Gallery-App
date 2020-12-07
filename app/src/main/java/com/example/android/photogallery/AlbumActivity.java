package com.example.android.photogallery;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.photogallery.Models.Photo;
import com.example.android.photogallery.RecyclerviewAdapter.SpecificAlbumAdapter;

import java.util.ArrayList;

public class AlbumActivity extends AppCompatActivity {
    private ArrayList<Photo> albumPhotoList;

    private SpecificAlbumAdapter albumAdapter = new SpecificAlbumAdapter(this);
    private RecyclerView photoAlbumRecyclerView;
    private ImageView headerImageView;
    private TextView titleTextView;
    private TextView numOfPhotosTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        Intent callingIntent = getIntent();
        Bundle bundle = callingIntent.getExtras();
        albumPhotoList = bundle.getParcelableArrayList("albumPhotoList");
        Log.i("SIZE", "" + albumPhotoList.size());

        String albumTitle = bundle.getString("albumName");

        titleTextView = (TextView) findViewById(R.id.album_title);
        titleTextView.setText(albumTitle);

        Integer numberOfPhotos = bundle.getInt("albumNumberofPhotos");
        numOfPhotosTextView = (TextView) findViewById(R.id.album_number_of_photos);
        Resources res = getResources();
        String numOfPhotos = res.getQuantityString(R.plurals.numberOfPhotos, numberOfPhotos, numberOfPhotos);
        numOfPhotosTextView.setText(numOfPhotos);

        headerImageView = (ImageView) findViewById(R.id.album_header);

        try {
            //Bitmap headerImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), albumPhotoList.get(0).get_imageUri());
            headerImageView.setImageURI(albumPhotoList.get(0).get_imageUri());
        }
        catch (Exception ex)
        {
            Log.e("Bitmap", "Setting Bitmap Image Failed");
            ex.printStackTrace();
        }

        albumAdapter.addMorePhoto(albumPhotoList);
        photoAlbumRecyclerView = (RecyclerView) findViewById(R.id.album_recyclerview);
        photoAlbumRecyclerView.setAdapter(albumAdapter);
        photoAlbumRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

}
