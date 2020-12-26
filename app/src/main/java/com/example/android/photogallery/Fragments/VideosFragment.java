package com.example.android.photogallery.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.photogallery.Models.VideoCategory;
import com.example.android.photogallery.R;
import com.example.android.photogallery.RecyclerviewAdapter.PhotoCategoryAdapter;
import com.example.android.photogallery.RecyclerviewAdapter.VideoCategoryAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideosFragment} factory method to
 * create an instance of this fragment.
 */
public class VideosFragment extends Fragment {

    private static VideoCategoryAdapter mDateVideoCategoryAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private final String KEY_STATE_DATE_VIDEO_RECYCLERVIEW = "dateRecyclerView";
    private Parcelable mState;

    private RecyclerView recyclerViewVideo;


    public VideosFragment() {
        // Required empty public constructor
    }

    public VideosFragment(VideoCategoryAdapter myAdapter) {
        mDateVideoCategoryAdapter = myAdapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videos, container, false);
        // Inflate the layout for this fragment

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewVideo = (RecyclerView) view.findViewById(R.id.recyclerview_video_all);
        recyclerViewVideo.setAdapter(mDateVideoCategoryAdapter);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewVideo.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable(KEY_STATE_DATE_VIDEO_RECYCLERVIEW, mState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null)
            mState = savedInstanceState.getParcelable(KEY_STATE_DATE_VIDEO_RECYCLERVIEW);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Retrieve list state and list/item positions
        if(savedInstanceState != null)
            mState = savedInstanceState.getParcelable(KEY_STATE_DATE_VIDEO_RECYCLERVIEW);
    }
}