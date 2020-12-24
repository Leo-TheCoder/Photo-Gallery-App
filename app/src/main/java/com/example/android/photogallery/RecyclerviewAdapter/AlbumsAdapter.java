package com.example.android.photogallery.RecyclerviewAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.photogallery.AlbumActivity;
import com.example.android.photogallery.CachingImage.MemoryCache;
import com.example.android.photogallery.CachingImage.MyHandler;
import com.example.android.photogallery.Models.Photo;
import com.example.android.photogallery.Models.PhotoCategory;
import com.example.android.photogallery.R;

import java.util.ArrayList;

public class AlbumsAdapter extends ListAdapter<PhotoCategory, AlbumsAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView1, imageView2, imageView3, imageView4;
        public TextView title, morePhotos;
        private AlbumClickListener albumClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView1 = (ImageView) itemView.findViewById(R.id.image_albums_item1);
            imageView2 = (ImageView) itemView.findViewById(R.id.image_albums_item2);
            imageView3 = (ImageView) itemView.findViewById(R.id.image_albums_item3);
            imageView4 = (ImageView) itemView.findViewById(R.id.image_albums_item4);
            imageView4.setColorFilter(Color.rgb(88,88,88), PorterDuff.Mode.DARKEN);
            title = (TextView) itemView.findViewById(R.id.album_title);
            morePhotos = (TextView) itemView.findViewById(R.id.more_photo_album);

            itemView.setOnClickListener(this);
        }

        public void setAlbumClickListener(AlbumClickListener albumClickListener)
        {
            this.albumClickListener = albumClickListener;
        }

        @Override
        public void onClick(View v) {
            albumClickListener.onClick(v);
        }
    }
    private ArrayList<PhotoCategory> mPhotoCategoryList;
    private Context mContext;

    public AlbumsAdapter(Context context) {
        super(DIFF_CALLBACK);
        mPhotoCategoryList = new ArrayList<PhotoCategory>();
        mContext = context;
    }


    public void addOnePhotoAlbum(Photo newPhoto) {
        boolean flag = false;
        String photoTitle = newPhoto.get_bucket();
        if(newPhoto.is_favorite()) {
            photoTitle = "Favorite";
        }

        for (int i = 0; i < mPhotoCategoryList.size(); i++) {
            if (photoTitle.equals(mPhotoCategoryList.get(i).get_title())) {
                mPhotoCategoryList.get(i).addPhoto(newPhoto);
                flag = true;
                submitList(mPhotoCategoryList);
                break;
            }
        }
        if (flag == false) {
            PhotoCategory newPhotoAlbum = new PhotoCategory(
                    PhotoCategory.CATEGORY_BUCKET,
                    photoTitle,
                    new ArrayList<Photo>(),
                    mContext);
            newPhotoAlbum.addPhoto(newPhoto);
            mPhotoCategoryList.add(newPhotoAlbum);
            submitList(mPhotoCategoryList);
        }
    }

    public boolean removeByUri(Uri uri) {
        boolean result = false;
        for(int i = 0; i < mPhotoCategoryList.size(); i++) {
            result = mPhotoCategoryList.get(i).removeByUri(uri);
            if(result == true) {
                submitList(mPhotoCategoryList);
                break;
            }
        }
        return result;
    }

    @NonNull
    @Override
    public AlbumsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.album_item, parent, false);
        AlbumsAdapter.ViewHolder viewHolder = new AlbumsAdapter.ViewHolder(contactView);

        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onBindViewHolder(@NonNull AlbumsAdapter.ViewHolder holder, int position) {
        final ArrayList<Photo> currentPhotoList = getItem(position).get_photosList();
        final int numberOfPhotos = currentPhotoList.size();
        final String title = getItem(position).get_title();
        ArrayList<ImageView> imageList = new ArrayList<ImageView>();
        imageList.add(holder.imageView1);
        imageList.add(holder.imageView2);
        imageList.add(holder.imageView3);
        imageList.add(holder.imageView4);
        holder.title.setText(title);

        for(int i = 0; i < numberOfPhotos; i++)
        {
            if(i > 3) {
                int remainPhotos = numberOfPhotos - 3;
                holder.morePhotos.setText("" + remainPhotos+"\nmore");
                break;
            }

            Photo currentPhoto = currentPhotoList.get(i);
            ImageView imageView = imageList.get(i);

            MemoryCache.loadBitmapThumbnail(mContext, currentPhoto.get_imageUri(),imageView, new MyHandler(imageView));
        }
        holder.setAlbumClickListener(new AlbumClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("albumPhotoList", currentPhotoList);
                bundle.putString("albumName", title);
                bundle.putInt("albumNumberofPhotos", Integer.valueOf(numberOfPhotos));
                Intent callAlbumActivity = new Intent(mContext, AlbumActivity.class);
                callAlbumActivity.putExtras(bundle);
                mContext.startActivity(callAlbumActivity);
            }
        });
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
}
