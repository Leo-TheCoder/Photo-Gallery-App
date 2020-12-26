package com.example.android.photogallery.Fragments;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.android.photogallery.RecyclerviewAdapter.AlbumsAdapter;
import com.example.android.photogallery.RecyclerviewAdapter.PhotoCategoryAdapter;
import com.example.android.photogallery.RecyclerviewAdapter.VideoCategoryAdapter;

public class MainUIAdapter extends FragmentStateAdapter {
    private int mNumOfTab;
    private static PhotoCategoryAdapter mPhotosDateAdapter;
    private static AlbumsAdapter mAlbumsAdapter;
    private static VideoCategoryAdapter mVideosDateAdapter;

    public MainUIAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    public void setNumOfTab(int numOfTab) {
        this.mNumOfTab = numOfTab;
    }
    public void setPhotosDateAdapter(final PhotoCategoryAdapter adapter) { mPhotosDateAdapter = adapter; }
    public void setPhotosBucketAdapter(final AlbumsAdapter adapter) {
        mAlbumsAdapter = adapter;
    }
    public void setVideosDateAdapter(final VideoCategoryAdapter adapter) { mVideosDateAdapter = adapter; }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0) {
            return new PhotosFragment(mPhotosDateAdapter);
        }
        else if(position == 1) {
            return new AlbumsFragment(mAlbumsAdapter);
        }
        else if(position == 2){
            return new VideosFragment(mVideosDateAdapter);
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
