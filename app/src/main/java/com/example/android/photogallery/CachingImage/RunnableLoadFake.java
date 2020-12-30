package com.example.android.photogallery.CachingImage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.util.Size;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.example.android.photogallery.CachingImage.MemoryCache.addBitmapToMemoryCache;

public class RunnableLoadFake implements Runnable {
    private Context mContext;
    private Uri resId;
    private MyHandler myHandler;

    public RunnableLoadFake(MyHandler myHandler, Context context, Uri uri) {
        this.myHandler = myHandler;
        this.mContext = context;
        this.resId = uri;
    }


    @Override
    public void run() {
        try {
            InputStream inputStream = mContext.getContentResolver().openInputStream(resId);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            float bitmapRatio = (float)width / (float) height;
            if (bitmapRatio > 1) {
                width = 200;
                height = (int) (width / bitmapRatio);
            } else {
                height = 200;
                width = (int) (height * bitmapRatio);
            }
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
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
