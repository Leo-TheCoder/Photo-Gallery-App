package com.example.android.photogallery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.android.photogallery.Utils.BitmapFileUtils;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class EditPhotoActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton btnBrush, btnEraser, btnFilter, btnSticker, btnUndo, btnRedo;
    PhotoEditor mPhotoEditor;
    LinearLayout colors;
    View color1, color2, color3, color4, color5, color6, color7, color8, color9, color10;
    String uri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_photo_activity);
        PhotoEditorView mPhotoEditorView = findViewById(R.id.photoEditorView);

        Intent callingIntent = getIntent();
        uri = callingIntent.getStringExtra("photoUri");
        mPhotoEditorView.getSource().setImageURI(Uri.parse(uri));

        btnBrush = findViewById(R.id.btnBrush);
        btnEraser = findViewById(R.id.btnEraser);
        btnSticker = findViewById(R.id.btnSticker);
        btnFilter = findViewById(R.id.btnFilter);
        btnUndo = findViewById(R.id.btnUndo);
        btnRedo = findViewById(R.id.btnRedo);
        colors = findViewById(R.id.colors);

        color1 = findViewById(R.id.color1);
        color2 = findViewById(R.id.color2);
        color3 = findViewById(R.id.color3);
        color4 = findViewById(R.id.color4);
        color5 = findViewById(R.id.color5);
        color6 = findViewById(R.id.color6);
        color7 = findViewById(R.id.color7);
        color8 = findViewById(R.id.color8);
        color9 = findViewById(R.id.color9);
        color10 = findViewById(R.id.color10);


        btnBrush.setOnClickListener(this);
        btnEraser.setOnClickListener(this);
        btnSticker.setOnClickListener(this);
        btnFilter.setOnClickListener(this);
        btnUndo.setOnClickListener(this);
        btnRedo.setOnClickListener(this);

        color1.setOnClickListener(this);
        color2.setOnClickListener(this);
        color3.setOnClickListener(this);
        color4.setOnClickListener(this);
        color5.setOnClickListener(this);
        color6.setOnClickListener(this);
        color7.setOnClickListener(this);
        color8.setOnClickListener(this);
        color9.setOnClickListener(this);
        color10.setOnClickListener(this);

        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true)
                .build();

    }


    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View v) {
        if (v.getId() == btnBrush.getId()) {
            mPhotoEditor.setBrushDrawingMode(true);
            colors.setVisibility(View.VISIBLE);
        } else if (v.getId() == btnEraser.getId()) {
            mPhotoEditor.brushEraser();
            colors.setVisibility(View.GONE);
        } else if (v.getId() == btnSticker.getId()) {
            colors.setVisibility(View.GONE);



            String filepath = BitmapFileUtils.getFilePath(this,Uri.parse(uri));

            int index = filepath.lastIndexOf(".");
            //print filename
            //System.out.println(file.getName().substring(0, index));
            //print extension
            //System.out.println(file.getName().substring(index));
            String ext = filepath.substring(index);
            String path = filepath.substring(0, filepath.lastIndexOf("/") + 1);
            //use file.renameTo() to rename the file
            final String newFilePath = path + System.currentTimeMillis() + ext;

            Log.d("File path: ", newFilePath);

            mPhotoEditor.saveAsFile(newFilePath, new PhotoEditor.OnSaveListener() {
                @Override
                public void onSuccess(@NonNull String imagePath) {
                    Toast.makeText(getBaseContext(), "Image saved successfully",
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getBaseContext(), "Failed to save Image",
                            Toast.LENGTH_LONG).show();
                    Log.e("File path: ", newFilePath);

                }
            });
        }
        else if (v.getId()==btnFilter.getId())
        {
            colors.setVisibility(View.GONE);
        }
        else if (v.getId()==btnUndo.getId())
        {
            mPhotoEditor.undo();
        }
        else if (v.getId()==btnRedo.getId())
        {
            mPhotoEditor.redo();
        }
        else if (v.getId()==R.id.color1 || v.getId()==R.id.color2 || v.getId()==R.id.color3 ||
                v.getId()==R.id.color4 || v.getId()==R.id.color5 || v.getId()==R.id.color6 ||
                v.getId()==R.id.color7 || v.getId()==R.id.color8 || v.getId()==R.id.color9 || v.getId()==R.id.color10)
        {
            int color = Color.TRANSPARENT;
            Drawable background = v.getBackground();
            if (background instanceof ColorDrawable)
                color = ((ColorDrawable) background).getColor();
            mPhotoEditor.setBrushColor(color);
        }
    }
}
