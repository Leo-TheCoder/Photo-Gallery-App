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

import com.example.android.photogallery.RecyclerviewAdapter.PhotoCategoryAdapter;
import com.example.android.photogallery.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PhotosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotosFragment extends Fragment {
    private static PhotoCategoryAdapter mDatePhotoCategoryAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private final String KEY_STATE_DATE_PHOTO_RECYCLERVIEW = "dateRecyclerView";
    private Parcelable mState;

    private RecyclerView recyclerViewPhoto;


    public PhotosFragment() {
        // Required empty public constructor
    }

    public PhotosFragment(PhotoCategoryAdapter myAdapter) {
        mDatePhotoCategoryAdapter = myAdapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        // Inflate the layout for this fragment

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewPhoto = (RecyclerView) view.findViewById(R.id.recyclerview_photo_all);
        recyclerViewPhoto.setAdapter(mDatePhotoCategoryAdapter);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewPhoto.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable(KEY_STATE_DATE_PHOTO_RECYCLERVIEW, mState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null)
            mState = savedInstanceState.getParcelable(KEY_STATE_DATE_PHOTO_RECYCLERVIEW);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Retrieve list state and list/item positions
        if(savedInstanceState != null)
            mState = savedInstanceState.getParcelable(KEY_STATE_DATE_PHOTO_RECYCLERVIEW);
    }
}