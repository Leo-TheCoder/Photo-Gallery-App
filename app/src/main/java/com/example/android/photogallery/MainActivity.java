package com.example.android.photogallery;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.util.LruCache;
import android.util.Size;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;


import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;

import com.example.android.photogallery.Animation.ZoomOutPageTransformer;
import com.example.android.photogallery.CachingImage.MemoryCache;
import com.example.android.photogallery.MainFragments.MainUIAdapter;
import com.example.android.photogallery.RecyclerviewAdapter.AlbumsAdapter;
import com.example.android.photogallery.RecyclerviewAdapter.PhotoCategoryAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    public final static SimpleDateFormat Formatter = new SimpleDateFormat(Photo.DATE_FORMAT, Locale.ENGLISH);
    public final static int PAGE_NUMBER = 2;
    private CharSequence[] tabTitle = {"PHOTOS", "ALBUM"};

    private ViewPager2 viewPager;
    private MainUIAdapter myAdapter;

    private ImageButton btnMenuList;
    private ArrayList<Photo> myPhotoList = new ArrayList<Photo>();
    private final String KEY_LIST_PHOTOS = "keyList";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myPhotoList = PhotoLoader.getImagesFromExternal();
        if(myPhotoList.size() == 0) {
            PhotoLoader.externalStoragePermissionCheck(this);
            PhotoLoader.getAllImageFromExternal(this);
            myPhotoList = PhotoLoader.getImagesFromExternal();
        }
        setContentView(R.layout.activity_main);

        MemoryCache.instance();
        btnMenuList = findViewById(R.id.btn_option);

        final PhotoCategoryAdapter photoDateAdapter = new PhotoCategoryAdapter(this);
        final AlbumsAdapter photoBucketAdapter = new AlbumsAdapter(this);


        for(int i = 0; i < myPhotoList.size(); i++) {
            try {
                photoDateAdapter.addOnePhoto(myPhotoList.get(i), PhotoCategory.CATEGORY_DATE);
                photoBucketAdapter.addOnePhotoAlbum(myPhotoList.get(i));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

//        recyclerView.setAdapter(photoDateAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MainUIAdapter(this);

        myAdapter.setNumOfTab(PAGE_NUMBER);
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

        btnMenuList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });
    }

    /**
     * function that navigate user to setting activity
     */
    void navigateToSetting(){
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }


    /**
     * function that pop up the menu when button More got hit
     * @param v the view that got hit
     */
    public void showPopup(View v){
        PopupMenu popupMenu = new PopupMenu(this, btnMenuList);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.settingMenuItem:
                        navigateToSetting();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.inflate(R.menu.more_pop_up_menu);
        popupMenu.show();
    }

}