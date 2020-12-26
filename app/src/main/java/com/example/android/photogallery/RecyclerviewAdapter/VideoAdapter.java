package com.example.android.photogallery.RecyclerviewAdapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.photogallery.CachingImage.MemoryCache;
import com.example.android.photogallery.CachingImage.MyHandler;
import com.example.android.photogallery.Models.Photo;
import com.example.android.photogallery.Models.Video;
import com.example.android.photogallery.PhotoDisplayActivity;
import com.example.android.photogallery.R;

import java.util.ArrayList;

public class VideoAdapter extends ListAdapter<Video, VideoAdapter.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView videoImageView;
        private ItemClickListener itemClickListener;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            videoImageView = (ImageView) itemView.findViewById(R.id.imageview_video);
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

    public void addMoreVideo(ArrayList<Video> newVideos) {
        _videosList.addAll(newVideos);
        submitList(_videosList);
    }

    //Constructor and member variables
    private ArrayList<Video> _videosList;
    private Context mContext;
    private boolean isFakeOn;

    public VideoAdapter(Context context, boolean isFake){
        super(DIFF_CALLBACK);
        _videosList = new ArrayList<Video>();
        mContext = context;
        isFakeOn = isFake;
    }


    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.video_item, parent, false);
        // Return a new holder instance
        VideoAdapter.ViewHolder viewHolder = new VideoAdapter.ViewHolder(contactView);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        Video currentVideo = getItem(position);

        // Set item views based on your views and data model
        ImageView imageView =  holder.videoImageView;

        //Load image to show
        if(!isFakeOn){
            MemoryCache.loadBitmapThumbnail(mContext, currentVideo.get_videoUri(),imageView, new MyHandler(imageView));
        } else {
            MemoryCache.loadBitmapFake(mContext, currentVideo.get_videoUri(), imageView, new MyHandler(imageView));
        }

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Video current = getItem(position);
                Log.i("TEST CLICK", "" + position);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("listVideo", _videosList);
                bundle.putInt("position", position);
                bundle.putBoolean("isFake", isFakeOn);
                //
                Intent callImageActivity = new Intent(mContext, PhotoDisplayActivity.class);
                //
                callImageActivity.putExtras(bundle);
                mContext.startActivity(callImageActivity);
            }
        });
    }


    public static final DiffUtil.ItemCallback<Video> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Video>() {
                @Override
                public boolean areItemsTheSame(Video oldItem, Video newItem) {
                    return oldItem.get_videoUri().equals(newItem.get_videoUri());
                }
                @Override
                public boolean areContentsTheSame(Video oldItem, Video newItem) {
                    return (oldItem.get_date().equals(newItem.get_date()) &&
                            oldItem.get_bucket().equals(newItem.get_bucket()));
                }
            };
}


