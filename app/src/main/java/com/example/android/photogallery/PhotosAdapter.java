package com.example.android.photogallery;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.CancellationSignal;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContentResolverCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

public class PhotosAdapter extends ListAdapter<Photo,PhotosAdapter.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView photoImageView;
        private ItemClickListener itemClickListener;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            photoImageView = (ImageView) itemView.findViewById(R.id.imageview_photo);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v,getAbsoluteAdapterPosition(),false);
        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onClick(v, getAbsoluteAdapterPosition(), true);
            return true;
        }
    }

    public void addMorePhoto(ArrayList<Photo> newPhotos) {
        _photosList.addAll(newPhotos);
        submitList(_photosList);
    }

    //Constructor and member variables
    private ArrayList<Photo> _photosList;
    private Context mContext;

    public PhotosAdapter(Context context){
        super(DIFF_CALLBACK);
        _photosList = new ArrayList<Photo>();
        mContext = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.photo_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        Photo currentPhoto = getItem(position);

        // Set item views based on your views and data model
        ImageView imageView =  holder.photoImageView;

        try {
            Bitmap thumbnail =
                    mContext.getContentResolver().loadThumbnail(
                            currentPhoto.get_imageUri(), new Size(150, 150), null);
            imageView.setImageBitmap(Bitmap.createScaledBitmap(thumbnail, 200, 250, false));
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                
            }
        });


    }


    public static final DiffUtil.ItemCallback<Photo> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Photo>() {
                @Override
                public boolean areItemsTheSame(Photo oldItem, Photo newItem) {
                    return oldItem.get_imageUri().equals(newItem.get_imageUri());
                }
                @Override
                public boolean areContentsTheSame(Photo oldItem, Photo newItem) {
                    return (oldItem.get_date().equals(newItem.get_date()) &&
                            oldItem.get_bucket().equals(newItem.get_bucket()));
                }
            };


}
