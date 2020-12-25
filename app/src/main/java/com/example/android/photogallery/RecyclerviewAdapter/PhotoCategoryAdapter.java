package com.example.android.photogallery.RecyclerviewAdapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.photogallery.MainActivity;
import com.example.android.photogallery.Models.Photo;
import com.example.android.photogallery.Models.PhotoCategory;
import com.example.android.photogallery.R;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PhotoCategoryAdapter extends ListAdapter<PhotoCategory, PhotoCategoryAdapter.ViewHolder> {

    private RecyclerView.RecycledViewPool
            viewPool = new RecyclerView.RecycledViewPool();

    private GridLayoutManager layoutManager;


    private boolean isFakeOn = false;


    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public RecyclerView recyclerView;
        public TextView title;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerview_photo_item);
            title = (TextView) itemView.findViewById(R.id.recyclerview_photo_date);
        }
    }

    private ArrayList<PhotoCategory> _photoCategoryList;
    private Context mContext;

    public PhotoCategoryAdapter(Context context,boolean isFake) {
        super(DIFF_CALLBACK);
        _photoCategoryList = new ArrayList<PhotoCategory>();
        mContext = context;
        isFakeOn = isFake;
    }

    @NonNull
    @Override
    public PhotoCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.recylerview_photo_item, parent, false);
        // Return a new holder instance
        PhotoCategoryAdapter.ViewHolder viewHolder = new PhotoCategoryAdapter.ViewHolder(contactView);
        // Create a layout manager
        // to assign a layout
        // to the RecyclerView.
        // Here we have assigned the layout as GridLayout with 3 columns
        layoutManager = new GridLayoutManager(
                parent.getContext(), 3);
        layoutManager.setInitialPrefetchItemCount(100);
        layoutManager.setItemPrefetchEnabled(true);
        // Create an instance of the child item view adapter and set its
        // layout manager and RecyclerViewPool
        viewHolder.recyclerView.setLayoutManager(layoutManager);
        viewHolder.recyclerView.setHasFixedSize(false);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoCategoryAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        PhotoCategory currentPhotoCategory = getItem(position);

        // Set item views based on your views and data model
        TextView titleDate = holder.title;
        RecyclerView photosList = holder.recyclerView;
        titleDate.setText(currentPhotoCategory.get_title());

        // Since this is a nested layout, so to define how many child items
        // should be prefetched when the child RecyclerView is nested
        // inside the parent RecyclerView, we use the following method
        PhotosAdapter photosAdapter = new PhotosAdapter(mContext,isFakeOn);
        photosAdapter.addMorePhoto(currentPhotoCategory.get_photosList());
        layoutManager.setInitialPrefetchItemCount(
                currentPhotoCategory.get_photosList().size());
        holder.recyclerView.setAdapter(photosAdapter);
        holder.recyclerView.setRecycledViewPool(viewPool);
    }

    public void addMorePhotoCategory(List<PhotoCategory> newPhotoCategory) {
        _photoCategoryList.addAll(newPhotoCategory);
        submitList(_photoCategoryList); // DiffUtil takes care of the check
    }

    public void addOnePhotoCategory(PhotoCategory newPhotoCategory) {
        _photoCategoryList.add(newPhotoCategory);
        submitList(_photoCategoryList);
    }

    public void addOnePhoto(Photo newPhoto, int category) throws ParseException {
        if (category == PhotoCategory.CATEGORY_DATE) {
            addOnePhotoDateCategory(newPhoto);
        } else if (category == PhotoCategory.CATEGORY_BUCKET) {
            addOnePhotoBucketCategory(newPhoto);
        } else {
            //DO NOTHING
        }
    }

    private void addOnePhotoDateCategory(Photo newPhoto) throws ParseException {
        Date dateCategory;
        Date dateNewPhoto = MainActivity.Formatter.parse(newPhoto.get_dateTitle());
        boolean flag = false;
        for (int i = 0; i < _photoCategoryList.size(); i++) {
            dateCategory = MainActivity.Formatter.parse(_photoCategoryList.get(i).get_title());
            if (dateNewPhoto.equals(dateCategory)) {
                _photoCategoryList.get(i).addPhoto(newPhoto);
                submitList(_photoCategoryList);
                flag = true;
                break;
            } else if (dateNewPhoto.after(dateCategory)) {
                PhotoCategory newPhotoDate = new PhotoCategory(
                        PhotoCategory.CATEGORY_DATE,
                        newPhoto.get_dateTitle(),
                        new ArrayList<Photo>(),
                        mContext);
                newPhotoDate.addPhoto(newPhoto);
                _photoCategoryList.add(i, newPhotoDate);
                submitList(_photoCategoryList);
                flag = true;
                break;
            }
        }
        if (flag == false) {
            PhotoCategory newPhotoDate = new PhotoCategory(
                    PhotoCategory.CATEGORY_DATE,
                    newPhoto.get_dateTitle(),
                    new ArrayList<Photo>(),
                    mContext);
            newPhotoDate.addPhoto(newPhoto);
            _photoCategoryList.add(newPhotoDate);
            submitList(_photoCategoryList);
        }
    }

    private void addOnePhotoBucketCategory(Photo newPhoto) {
        boolean flag = false;
        String photoTitle = newPhoto.get_bucket();
        for (int i = 0; i < _photoCategoryList.size(); i++) {
            if (photoTitle.equals(_photoCategoryList.get(i).get_title())) {
                _photoCategoryList.get(i).addPhoto(newPhoto);
                flag = true;
                submitList(_photoCategoryList);
                break;
            }
        }
        if (flag == false) {
            PhotoCategory newPhotoDate = new PhotoCategory(
                    PhotoCategory.CATEGORY_BUCKET,
                    photoTitle,
                    new ArrayList<Photo>(),
                    mContext);
            newPhotoDate.addPhoto(newPhoto);
            _photoCategoryList.add(newPhotoDate);
            submitList(_photoCategoryList);
        }
    }

    public boolean removePhotoByUri(Uri uri) {
        boolean result = false;
        for(int i = 0; i < _photoCategoryList.size(); i++) {
            result = _photoCategoryList.get(i).removeByUri(uri);

            if(result == true) {
                if(_photoCategoryList.get(i).get_photosList().isEmpty()){
                    _photoCategoryList.remove(i);
                }
                submitList(_photoCategoryList);
                break;
            }
        }
        return result;
    }


    public static final DiffUtil.ItemCallback<PhotoCategory> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<PhotoCategory>() {
                @Override
                public boolean areItemsTheSame(PhotoCategory oldItem, PhotoCategory newItem) {
                    return oldItem.get_title().equals(newItem.get_title());
                }

                @Override
                public boolean areContentsTheSame(PhotoCategory oldItem, PhotoCategory newItem) {
                    return (oldItem.get_photosList().equals(newItem.get_photosList()));
                }
            };

    public void updateList(ArrayList<PhotoCategory> newList) {
        _photoCategoryList = new ArrayList<PhotoCategory>(newList);
        int count = 0;
        for(int i =0;i < newList.size(); i++) {
            count += newList.get(i).get_photosList().size();
            Log.e("UPDATE", "updateList: " + newList.get(i).get_title() );
        }
        Log.e("UPDATE", "COUNT = " + count);

        submitList(_photoCategoryList);
    }
}
