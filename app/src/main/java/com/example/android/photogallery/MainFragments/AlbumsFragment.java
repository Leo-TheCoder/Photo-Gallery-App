package com.example.android.photogallery.MainFragments;

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

import com.example.android.photogallery.R;
import com.example.android.photogallery.RecyclerviewAdapter.AlbumsAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlbumsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlbumsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static AlbumsAdapter mAlbumAdapter;
    private RecyclerView albumRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private final String KEY_STATE_ALBUMS_PHOTO_RECYCLERVIEW = "albumsRecyclerView";
    private Parcelable mState;

    public AlbumsFragment() {
        // Required empty public constructor
    }

    public AlbumsFragment(AlbumsAdapter albumsAdapter) {
        mAlbumAdapter = albumsAdapter;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param albumsAdapter get albumsAdapter.
     * @return A new instance of fragment AlbumsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlbumsFragment newInstance(AlbumsAdapter albumsAdapter) {
        AlbumsFragment fragment = new AlbumsFragment();
        mAlbumAdapter = albumsAdapter;
        return fragment;
    }

    /**
     * This function is not used :)
     * @param savedInstanceState
     */
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_albums, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        albumRecyclerView = (RecyclerView) view.findViewById(R.id.album_recyclerview);
        albumRecyclerView.setAdapter(mAlbumAdapter);
        mLayoutManager = new LinearLayoutManager(getActivity());
        albumRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable(KEY_STATE_ALBUMS_PHOTO_RECYCLERVIEW, mState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null)
            mState = savedInstanceState.getParcelable(KEY_STATE_ALBUMS_PHOTO_RECYCLERVIEW);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Retrieve list state and list/item positions
        if(savedInstanceState != null)
            mState = savedInstanceState.getParcelable(KEY_STATE_ALBUMS_PHOTO_RECYCLERVIEW);
    }
}