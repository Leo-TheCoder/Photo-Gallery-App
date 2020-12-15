package com.example.android.photogallery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.example.android.photogallery.Service.MediaTrackerService;
import com.example.android.photogallery.Utils.BitmapFileUtils;
import com.example.android.photogallery.Utils.PhotoUtils;
import com.example.android.photogallery.RecyclerviewAdapter.AlbumsAdapter;
import com.example.android.photogallery.RecyclerviewAdapter.PhotoCategoryAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
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
    private Intent myService;

    private PhotoCategoryAdapter photoDateAdapter = new PhotoCategoryAdapter(this);
    private AlbumsAdapter photoBucketAdapter = new AlbumsAdapter(this);

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //check if the app is locked

        if (isLock()){
            Intent lockIntent = new Intent(this,LockActivity.class);
            lockIntent.putExtra(LockActivity.KEY_SET,false);
            startActivityForResult(lockIntent,IS_LOCK_REQUEST);
        }


        super.onCreate(savedInstanceState);
        //Get photos from system
        //...
        PhotoUtils.externalStoragePermissionCheck(this);
        myPhotoList = PhotoUtils.getImagesFromExternal();
        if(myPhotoList.size() == 0) {

            PhotoUtils.getAllImageFromExternal(this);
            myPhotoList = PhotoUtils.getImagesFromExternal();
        }

        myService = new Intent(this, MediaTrackerService.class);
        startService(myService);

        IntentFilter filter = new IntentFilter(MediaTrackerService.SERVICE_ACTION_CODE);
        BroadcastReceiver receiver = new MediaBroadcastReceiver();
        registerReceiver(receiver, filter);

        setContentView(R.layout.activity_main);

        MemoryCache.instance();
        btnMenuList = findViewById(R.id.btn_option);


        //Initialize 2 adapter for recycler view
        //....
        for(int i = 0; i < myPhotoList.size(); i++) {
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

        //Initialize tab layout for our viewpager
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(tabTitle[position]);
            }
        }).attach();
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
     * Check if the app is locked
     * @return boolean value
     */
    boolean isLock() {
        SharedPreferences sharedPreferences =  getSharedPreferences(LockActivity.SHARE_PREFERENCES, MODE_PRIVATE);
        return sharedPreferences.getBoolean(LockActivity.KEY_PIN_CODE,false);
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
    private void openCamera() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();

                dispatchTakePictureIntent();
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

        if (requestCode == IS_LOCK_REQUEST && resultCode != RESULT_OK) {
            finish();
        }

        if (requestCode == BitmapFileUtils.REQUEST_IMAGE_CAPTURE) {
            try {
                if (!BitmapFileUtils.saveImageCapturedByCameraToStorage(this, BitmapFileUtils.CAMERA)){
                    Toast.makeText(this, "Error saving picture!!!", Toast.LENGTH_SHORT).show();
                };
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = BitmapFileUtils.createImageFile(this);
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, BitmapFileUtils.REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * Add one photo to both DateAdapter and AlbumsAdapter
     * Update UI
     * @param newPhoto
     * @throws ParseException
     */
    public void addPhoto(Photo newPhoto) throws ParseException {
        photoDateAdapter.addOnePhoto(newPhoto, PhotoCategory.CATEGORY_DATE);
        photoBucketAdapter.addOnePhotoAlbum(newPhoto);
        photoDateAdapter.notifyDataSetChanged();    //Update UI Photos's tab
        photoBucketAdapter.notifyDataSetChanged();  //Update UI Albums's tab
    }

    /**
     * Remove photo by finding its uri
     * Only update UI when removes successfully
     * @param uri
     */
    public void removePhoto(Uri uri) {
        boolean result = false;
        result = photoDateAdapter.removePhotoByUri(uri);
        result = photoBucketAdapter.removeByUri(uri);
        if(result == true) {
            photoDateAdapter.notifyDataSetChanged();
            photoBucketAdapter.notifyDataSetChanged();
        }
    }

    // Broadcast Receiver to receive info whenever there is some changes
    public class MediaBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String key = bundle.getString("key");

            if(key.equals("add"))
            {
                Photo newPhoto = bundle.getParcelable("photo");
                try {
                    addPhoto(newPhoto);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else {
                String uriStr = bundle.getString("uri");
                Uri removedUri = Uri.parse(uriStr);
                removePhoto(removedUri);
            }
        }
    }


}