package com.example.android.photogallery.RecyclerviewAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Size;
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

import com.example.android.photogallery.Photo;
import com.example.android.photogallery.PhotoCategory;
import com.example.android.photogallery.R;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

public class AlbumsAdapter extends ListAdapter<PhotoCategory, AlbumsAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView1, imageView2, imageView3, imageView4;
        public TextView title;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView1 = (ImageView) itemView.findViewById(R.id.image_albums_item1);
            imageView2 = (ImageView) itemView.findViewById(R.id.image_albums_item2);
            imageView3 = (ImageView) itemView.findViewById(R.id.image_albums_item3);
            imageView4 = (ImageView) itemView.findViewById(R.id.image_albums_item4);
            title = (TextView) itemView.findViewById(R.id.album_title);
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
        for (int i = 0; i < mPhotoCategoryList.size(); i++) {
            if (photoTitle.equals(mPhotoCategoryList.get(i).get_title())) {
                mPhotoCategoryList.get(i).addPhoto(newPhoto);
                flag = true;
                submitList(mPhotoCategoryList);
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
            mPhotoCategoryList.add(newPhotoDate);
            submitList(mPhotoCategoryList);
        }
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull AlbumsAdapter.ViewHolder holder, int position) {
        ArrayList<Photo> currentPhotoList = getItem(position).get_photosList();
        int numberOfPhotos = currentPhotoList.size();
        ArrayList<ImageView> imageList = new ArrayList<ImageView>();
        imageList.add(holder.imageView1);
        imageList.add(holder.imageView2);
        imageList.add(holder.imageView3);
        imageList.add(holder.imageView4);
        holder.title.setText(getItem(position).get_title());

        for(int i = 0; i < numberOfPhotos; i++)
        {
            if(i > 3) {
                break;
            }

            Photo currentPhoto = currentPhotoList.get(i);
            try {
                Bitmap thumbnail =
                        mContext.getContentResolver().loadThumbnail(
                                currentPhoto.get_imageUri(), new Size(150, 150), null);
                //imageList.get(i).setImageBitmap(thumbnail);
                imageList.get(i).setImageBitmap(Bitmap.createScaledBitmap(thumbnail, 200, 200, false));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



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
