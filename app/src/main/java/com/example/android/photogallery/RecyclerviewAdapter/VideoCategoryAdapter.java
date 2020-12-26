package com.example.android.photogallery.RecyclerviewAdapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.photogallery.MainActivity;
import com.example.android.photogallery.Models.Photo;
import com.example.android.photogallery.Models.PhotoCategory;
import com.example.android.photogallery.Models.Video;
import com.example.android.photogallery.Models.VideoCategory;
import com.example.android.photogallery.R;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VideoCategoryAdapter extends ListAdapter<VideoCategory, VideoCategoryAdapter.ViewHolder> {
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

    private ArrayList<VideoCategory> _videoCategoryList;
    private Context mContext;

    public VideoCategoryAdapter(Context context,boolean isFake) {
        super(DIFF_CALLBACK);
        _videoCategoryList = new ArrayList<VideoCategory>();
        mContext = context;
        isFakeOn = isFake;
    }

    @NonNull
    @Override
    public VideoCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.recylerview_photo_item, parent, false);
        // Return a new holder instance
        VideoCategoryAdapter.ViewHolder viewHolder = new VideoCategoryAdapter.ViewHolder(contactView);
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
    public void onBindViewHolder(@NonNull VideoCategoryAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        VideoCategory currentVideoCategory = getItem(position);

        // Set item views based on your views and data model
        TextView titleDate = holder.title;
        RecyclerView videosList = holder.recyclerView;
        titleDate.setText(currentVideoCategory.get_title());

        // Since this is a nested layout, so to define how many child items
        // should be prefetched when the child RecyclerView is nested
        // inside the parent RecyclerView, we use the following method
        VideoAdapter videosAdapter = new VideoAdapter(mContext,isFakeOn);
        videosAdapter.addMoreVideo(currentVideoCategory.get_videosList());
        layoutManager.setInitialPrefetchItemCount(
                currentVideoCategory.get_videosList().size());
        holder.recyclerView.setAdapter(videosAdapter);
        holder.recyclerView.setRecycledViewPool(viewPool);
    }

    public void addMoreVideoCategory(List<VideoCategory> newVideoCategory) {
        _videoCategoryList.addAll(newVideoCategory);
        submitList(_videoCategoryList); // DiffUtil takes care of the check
    }

    public void addOneVideoCategory(VideoCategory newVideoCategory) {
        _videoCategoryList.add(newVideoCategory);
        submitList(_videoCategoryList);
    }

    public void addOneVideo(Video newVideo, int category) throws ParseException {
        if (category == VideoCategory.CATEGORY_DATE) {
            addOneVideoDateCategory(newVideo);
        } else if (category == VideoCategory.CATEGORY_BUCKET) {
            addOneVideoBucketCategory(newVideo);
        } else {
            //DO NOTHING
        }
    }

    private void addOneVideoDateCategory(Video newVideo) throws ParseException {
        Date dateCategory;
        Date dateNewVideo = MainActivity.Formatter.parse(newVideo.get_dateTitle());
        boolean flag = false;
        for (int i = 0; i < _videoCategoryList.size(); i++) {
            dateCategory = MainActivity.Formatter.parse(_videoCategoryList.get(i).get_title());
            if (dateNewVideo.equals(dateCategory)) {
                _videoCategoryList.get(i).addVideo(newVideo);
                submitList(_videoCategoryList);
                flag = true;
                break;
            } else if (dateNewVideo.after(dateCategory)) {
                VideoCategory newVideoDate = new VideoCategory(
                        VideoCategory.CATEGORY_DATE,
                        newVideo.get_dateTitle(),
                        new ArrayList<Video>(),
                        mContext);
                newVideoDate.addVideo(newVideo);
                _videoCategoryList.add(i, newVideoDate);
                submitList(_videoCategoryList);
                flag = true;
                break;
            }
        }
        if (flag == false) {
            VideoCategory newVideoDate = new VideoCategory(
                    VideoCategory.CATEGORY_DATE,
                    newVideo.get_dateTitle(),
                    new ArrayList<Video>(),
                    mContext);
            newVideoDate.addVideo(newVideo);
            _videoCategoryList.add(newVideoDate);
            submitList(_videoCategoryList);
        }
    }

    private void addOneVideoBucketCategory(Video newVideo) {
        boolean flag = false;
        String videoTitle = newVideo.get_bucket();
        for (int i = 0; i < _videoCategoryList.size(); i++) {
            if (videoTitle.equals(_videoCategoryList.get(i).get_title())) {
                _videoCategoryList.get(i).addVideo(newVideo);
                flag = true;
                submitList(_videoCategoryList);
                break;
            }
        }
        if (flag == false) {
            VideoCategory newVideoDate = new VideoCategory(
                    VideoCategory.CATEGORY_BUCKET,
                    videoTitle,
                    new ArrayList<Video>(),
                    mContext);
            newVideoDate.addVideo(newVideo);
            _videoCategoryList.add(newVideoDate);
            submitList(_videoCategoryList);
        }
    }

    public boolean removeVideoByUri(Uri uri) {
        boolean result = false;
        for(int i = 0; i < _videoCategoryList.size(); i++) {
            result = _videoCategoryList.get(i).removeByUri(uri);

            if(result == true) {
                if(_videoCategoryList.get(i).get_videosList().isEmpty()){
                    _videoCategoryList.remove(i);
                }
                submitList(_videoCategoryList);
                break;
            }
        }
        return result;
    }


    public static final DiffUtil.ItemCallback<VideoCategory> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<VideoCategory>() {
                @Override
                public boolean areItemsTheSame(VideoCategory oldItem, VideoCategory newItem) {
                    return oldItem.get_title().equals(newItem.get_title());
                }

                @Override
                public boolean areContentsTheSame(VideoCategory oldItem, VideoCategory newItem) {
                    return (oldItem.get_videosList().equals(newItem.get_videosList()));
                }
            };

}
