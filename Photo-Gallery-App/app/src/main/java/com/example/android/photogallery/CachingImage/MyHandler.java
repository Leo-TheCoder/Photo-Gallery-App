package com.example.android.photogallery.CachingImage;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;



public class MyHandler extends Handler {
    private Looper looper;
    private ImageView mImageView;
    public MyHandler(ImageView imageView) {
        this.looper = Looper.getMainLooper();
        mImageView =  imageView;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        Bundle bundle = msg.getData();
        String uriString = bundle.getString("uri");
        Uri uri = Uri.parse(uriString);
        mImageView.setImageBitmap(MemoryCache.getBitmapFromMemCache(uri));
    }


}
