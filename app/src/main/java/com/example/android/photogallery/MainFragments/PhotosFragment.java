package com.example.android.photogallery.MainFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static PhotoCategoryAdapter mDatePhotoCategoryAdapter;

    private RecyclerView recyclerViewPhoto;


    public PhotosFragment() {
        // Required empty public constructor
    }

    public PhotosFragment(PhotoCategoryAdapter myAdapter) {
        mDatePhotoCategoryAdapter = myAdapter;
    }
    /**
     *
     * @param photoCategoryAdapter
     *
     */
    // TODO: Rename and change types and number of parameters
    public static PhotosFragment newInstance(PhotoCategoryAdapter photoCategoryAdapter) {
        PhotosFragment fragment = new PhotosFragment();
        mDatePhotoCategoryAdapter = photoCategoryAdapter;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
        recyclerViewPhoto.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}