package com.example.android.photogallery.RecyclerviewAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class AlbumsAdapter extends ListAdapter<PhotoCategory, AlbumsAdapter.ViewHolder> {

    private boolean isFakeOn = false;

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

    public AlbumsAdapter(Context context,boolean isFake) {
        super(DIFF_CALLBACK);
        mPhotoCategoryList = new ArrayList<PhotoCategory>();
        mContext = context;
        isFakeOn = isFake;
    }


    public void addOnePhotoAlbum(Photo newPhoto) {
        boolean flag = false;
        String photoTitle = newPhoto.get_bucket();
        if(newPhoto.is_favorite()) {
            photoTitle = "Favorite";
        }

        for (int i = 0; i < mPhotoCategoryList.size(); i++) {
            if (photoTitle.compareTo(mPhotoCategoryList.get(i).get_title()) == 0) {
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

        for(int i = 0; i < 4; i++)
        {
            if(i < numberOfPhotos) {
                imageList.get(i).setVisibility(View.VISIBLE);
                holder.morePhotos.setVisibility(View.VISIBLE);
                if(i == 3) {
                    holder.imageView4.setColorFilter(Color.rgb(88,88,88), PorterDuff.Mode.DARKEN);
                    int remainPhotos = numberOfPhotos - 3;
                    holder.morePhotos.setText("" + remainPhotos+"\nmore");
                }

                Photo currentPhoto = currentPhotoList.get(i);
                ImageView imageView = imageList.get(i);

                if (!isFakeOn) {
                    MemoryCache.loadBitmapThumbnail(mContext, currentPhoto.get_imageUri(), imageView, new MyHandler(imageView));
                } else {
                    MemoryCache.loadBitmapFake(mContext, currentPhoto.get_imageUri(), imageView, new MyHandler(imageView));
                }
            }
            else {
                imageList.get(i).setVisibility(View.INVISIBLE);
                holder.morePhotos.setVisibility(View.INVISIBLE);
            }

        }
        holder.setAlbumClickListener(new AlbumClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("albumPhotoList", currentPhotoList);
                bundle.putString("albumName", title);
                bundle.putInt("albumNumberofPhotos", Integer.valueOf(numberOfPhotos));
                bundle.putBoolean("isFake",isFakeOn);
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
