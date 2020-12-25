package com.example.android.photogallery.CachingImage;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.LruCache;
import android.widget.ImageView;

import com.example.android.photogallery.R;

public class MemoryCache {
    private static LruCache<Uri, Bitmap> memoryCache;

    public static void instance() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 10;

        memoryCache = new LruCache<Uri, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(Uri key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static void addBitmapToMemoryCache(Uri key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            memoryCache.put(key, bitmap);
        }
    }

    public static Bitmap getBitmapFromMemCache(Uri key) {
        return memoryCache.get(key);
    }

    public static void loadBitmapThumbnail(final Context mContext, final Uri resId, ImageView mImageView, MyHandler handler) {

        final Bitmap bitmap = getBitmapFromMemCache(resId);
        if (bitmap != null) {
            mImageView.setImageBitmap(bitmap);
        } else {
            mImageView.setImageResource(R.drawable.mainbg_gradient);
            new Thread(new RunnableLoadThumbnail(handler, mContext, resId)).start();
        }
    }

    public static void loadBitmapFake(final Context mContext, Uri resId, ImageView mImageView, MyHandler handler) {
        Bitmap bitmap = getBitmapFromMemCache(resId);
        if(bitmap!= null) {
            mImageView.setImageBitmap(bitmap);
        } else {
            mImageView.setImageResource(R.drawable.mainbg_gradient);
            new Thread(new RunnableLoadFake(handler, mContext, resId)).start();
        }
    }

//    public static void loadBitmapFullSize(final Context mContext, final Uri resId, ImageView mImageView, MyHandler handler) {
//        Uri temp = Uri.withAppendedPath(resId,"FULL");
//        final Bitmap bitmap = getBitmapFromMemCache(temp);
//        if (bitmap != null) {
//            mImageView.setImageBitmap(bitmap);
//        } else {
//            mImageView.setImageResource(R.drawable.mainbg_gradient);
//            new Thread(new RunnableLoadFullSize(handler, mContext, resId)).start();
//        }
//    }
}
