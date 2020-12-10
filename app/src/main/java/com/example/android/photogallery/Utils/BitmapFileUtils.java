package com.example.android.photogallery.Utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BitmapFileUtils {
    public static final int REQUEST_IMAGE_CAPTURE = 123;

    public static String CAMERA = "Camera";
    public static String TRASH = "Trash";
    public static String FAVORITE = "Favorite";

    public static void saveToExternal(Bitmap bitmap, String bucketName) {
        String path = Environment.getExternalStorageDirectory().toString()
                + "/Pictures/" + bucketName + "/camera-" + System.currentTimeMillis() + ".jpg";

        OutputStream out = null;
        File imageFile = new File(path);

        if (!imageFile.getParentFile().exists()) {
            if (!imageFile.getParentFile().mkdirs()) {
                Log.e("MKDIR:", "Cannot mkdirs!!!");
            }
        }
        try {
            out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
