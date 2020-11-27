package com.example.android.photogallery;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.versionedparcelable.ParcelField;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Size;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    public final static SimpleDateFormat Formatter = new SimpleDateFormat(Photo.DATE_FORMAT, Locale.ENGLISH);
    private CharSequence[] tabTitle = {"PHOTOS", "ALBUM"};

    private ViewPager2 viewPager;
    private MainUIAdapter myAdapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PhotoLoader.externalStoragePermissionCheck(this);

        PhotoLoader.getAllImageFromExternal(this);

        //RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview_photo_all);
        final PhotoCategoryAdapter photoDateAdapter = new PhotoCategoryAdapter(this);
        final PhotoCategoryAdapter photoBucketAdapter = new PhotoCategoryAdapter(this);

        ArrayList<Photo> myPhotoList = PhotoLoader.getImagesFromExternal();
        for(int i = 0; i < myPhotoList.size(); i++) {
            try {
                photoDateAdapter.addOnePhoto(myPhotoList.get(i), PhotoCategory.CATEGORY_DATE);
                photoBucketAdapter.addOnePhoto(myPhotoList.get(i), PhotoCategory.CATEGORY_BUCKET);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

//        recyclerView.setAdapter(photoDateAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MainUIAdapter(this);

        myAdapter.setNumOfTab(2);
        myAdapter.setPhotosDateAdapter(photoDateAdapter);
        myAdapter.setPhotosBucketAdapter(photoBucketAdapter);


        viewPager = (ViewPager2) findViewById(R.id.viewpager);
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager.setAdapter(myAdapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setPageTransformer(new ZoomOutPageTransformer());

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(tabTitle[position]);
            }
        }).attach();

//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.testFragment, PhotosFragment.newInstance(photoDateAdapter)); ft.commit();

    }
}