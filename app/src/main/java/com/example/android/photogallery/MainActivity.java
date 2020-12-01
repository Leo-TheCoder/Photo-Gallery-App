package com.example.android.photogallery;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Build;
import android.os.Bundle;

import com.example.android.photogallery.Animation.ZoomOutPageTransformer;
import com.example.android.photogallery.MainFragments.MainUIAdapter;
import com.example.android.photogallery.RecyclerviewAdapter.AlbumsAdapter;
import com.example.android.photogallery.RecyclerviewAdapter.PhotoCategoryAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        final AlbumsAdapter photoBucketAdapter = new AlbumsAdapter(this);

        ArrayList<Photo> myPhotoList = PhotoLoader.getImagesFromExternal();
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