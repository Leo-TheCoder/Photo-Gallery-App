package com.example.android.photogallery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;


import com.example.android.photogallery.Animation.ZoomOutPageTransformer;
import com.example.android.photogallery.CachingImage.MemoryCache;
import com.example.android.photogallery.Fragments.MainUIAdapter;
import com.example.android.photogallery.Models.Photo;
import com.example.android.photogallery.Models.PhotoCategory;
import com.example.android.photogallery.Utils.BitmapFileUtils;
import com.example.android.photogallery.Utils.PhotoUtils;
import com.example.android.photogallery.RecyclerviewAdapter.AlbumsAdapter;
import com.example.android.photogallery.RecyclerviewAdapter.PhotoCategoryAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public final static SimpleDateFormat Formatter = new SimpleDateFormat(Photo.DATE_FORMAT, Locale.ENGLISH);
    private static final int MY_CAMERA_PERMISSION_CODE = 101;
    public final static int PAGE_NUMBER = 2;
    public final static int IS_LOCK_REQUEST = 100;
    private CharSequence[] tabTitle = {"PHOTOS", "ALBUM"};

    private ViewPager2 viewPager;
    private MainUIAdapter myAdapter;

    private ImageButton btnMenuList;
    private ArrayList<Photo> myPhotoList = new ArrayList<Photo>();
    private final String KEY_LIST_PHOTOS = "keyList";

    private boolean isFakeOn = false;

    private PhotoCategoryAdapter photoDateAdapter = null;
    private AlbumsAdapter photoBucketAdapter = null;
    private int signal = 0;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //Get photos from system
        //...
        PhotoUtils.externalStoragePermissionCheck(this);
        PhotoUtils.generateFakeImageList(this);
        PhotoUtils.getAllImageFromExternal(this);

        if (isLock()) {
            isFakeOn = true;
            myPhotoList = PhotoUtils.getFakeImages();

        } else {
            isFakeOn = false;
            myPhotoList = PhotoUtils.getImagesFromExternal();
        }

        Log.e("TAG", "true? " + isFakeOn);
        setContentView(R.layout.activity_main);

        MemoryCache.instance();
        btnMenuList = findViewById(R.id.btn_option);

        //Initialize 2 adapter for recycler view
        //....
        setMyAdapter();

        //Initialize tab layout for our viewpager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(tabTitle[position]);
            }
        }).attach();
        btnMenuList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFakeOn) {
                    signal++;
                    if (signal == 5) {
                        Intent lockIntent = new Intent(MainActivity.this, LockActivity.class);
                        lockIntent.putExtra(LockActivity.KEY_SET, false);
                        startActivityForResult(lockIntent, IS_LOCK_REQUEST);
                    }
                } else {
                    showPopup(v);
                }
            }
        });
    }

    void setMyAdapter() {
        photoDateAdapter = new PhotoCategoryAdapter(this, isFakeOn);
        photoBucketAdapter = new AlbumsAdapter(this,isFakeOn);
        //Initialize 2 adapter for recycler view
        //....
        for (int i = 0; i < myPhotoList.size(); i++) {
            try {
                photoDateAdapter.addOnePhoto(myPhotoList.get(i), PhotoCategory.CATEGORY_DATE);
                photoBucketAdapter.addOnePhotoAlbum(myPhotoList.get(i));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //Call an adapter which contains 2 fragments (Photos, Albums)
        myAdapter = new MainUIAdapter(this);
        //Set number of elements we want to show in viewpager
        myAdapter.setNumOfTab(PAGE_NUMBER);
        //Each fragment contains recyclerview which needs adapter to display data
        //Get adapters for each fragment
        myAdapter.setPhotosDateAdapter(photoDateAdapter);       //Adapter for Photos fragment
        myAdapter.setPhotosBucketAdapter(photoBucketAdapter);   //Adapter for Albums fragment

        //Initialize viewpager with our custom adapter
        viewPager = (ViewPager2) findViewById(R.id.viewpager);
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager.setAdapter(myAdapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setPageTransformer(new ZoomOutPageTransformer()); //Animation when transfer page
    }


    /**
     * function that navigate user to setting activity
     */
    void navigateToSetting() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    /**
     * Check if the app is locked
     *
     * @return boolean value
     */
    boolean isLock() {
        SharedPreferences sharedPreferences = getSharedPreferences(LockActivity.SHARE_PREFERENCES, MODE_PRIVATE);
        return sharedPreferences.getBoolean(LockActivity.KEY_PIN_CODE, false);
    }


    /**
     * function that pop up the menu when button More got hit
     *
     * @param v the view that got hit
     */
    public void showPopup(View v) {
        PopupMenu popupMenu = new PopupMenu(this, btnMenuList);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.MainSettingMenuItem:
                        navigateToSetting();
                        return true;
                    case R.id.MainCamera:
                        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                        } else {
                            openCamera();
                        }
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.inflate(R.menu.more_pop_up_menu);
        popupMenu.show();
    }

    /**
     * function that create an intent to open camera
     *
     * @no params
     */

    private void openCamera() {
        Log.e("TAG", getExternalFilesDir(null).toString());
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, BitmapFileUtils.REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();

                openCamera();

            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * function handle activity result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IS_LOCK_REQUEST) {
            if (resultCode != RESULT_OK) {
                finish();
            } else {
                myPhotoList = PhotoUtils.getImagesFromExternal();
                isFakeOn = false;
                setMyAdapter();
            }
        }

        if (requestCode == BitmapFileUtils.REQUEST_IMAGE_CAPTURE) {
            if (data != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                try {
                    BitmapFileUtils.saveImageToStorage(this, photo, BitmapFileUtils.CAMERA);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Error while saving image!!! Please Try again!!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}