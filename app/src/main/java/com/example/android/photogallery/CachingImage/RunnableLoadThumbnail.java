package com.example.android.photogallery.CachingImage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.util.Size;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.InputStream;

import static com.example.android.photogallery.CachingImage.MemoryCache.addBitmapToMemoryCache;


public class RunnableLoadThumbnail implements Runnable {
    private Context mContext;
    private Uri resId;
    private MyHandler myHandler;

    public RunnableLoadThumbnail(MyHandler myHandler, Context context, Uri uri) {
        this.myHandler = myHandler;
        this.mContext = context;
        this.resId = uri;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void run() {
        try {

            Bitmap bitmap = mContext.getContentResolver().loadThumbnail(resId, new Size(150,150),null);
            addBitmapToMemoryCache(resId, bitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putString("uri", String.valueOf(resId));
        msg.setData(bundle);
        myHandler.sendMessage(msg);
    }
}
