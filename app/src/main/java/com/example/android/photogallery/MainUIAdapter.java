package com.example.android.photogallery;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MainUIAdapter extends FragmentStateAdapter {
    private int mNumOfTab;
    private static PhotoCategoryAdapter mPhotosDateAdapter;
    private static PhotoCategoryAdapter mPhotosBucketAdapter;


    public MainUIAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }


    public MainUIAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public MainUIAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public void setNumOfTab(int numOfTab) {
        this.mNumOfTab = numOfTab;
    }

    public void setPhotosDateAdapter(final PhotoCategoryAdapter adapter) {

        mPhotosDateAdapter = adapter;
    }

    public void setPhotosBucketAdapter(final PhotoCategoryAdapter adapter) {
        mPhotosBucketAdapter = adapter;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.i("TEST", "createFragment: " + position);
        if(position == 0) {

            return new PhotosFragment(mPhotosDateAdapter);
        }
        else if(position == 1) {
            return new PhotosFragment(mPhotosBucketAdapter);
        }
        else {
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return mNumOfTab;
    }
}
