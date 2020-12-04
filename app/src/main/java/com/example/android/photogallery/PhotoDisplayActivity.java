package com.example.android.photogallery;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class PhotoDisplayActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_IMAGE_CAPTURE = 1000;

    private boolean settingPop = true;

    ImageButton btnShare, btnMore;
    TouchImageView imageDisplay;
    LinearLayout linearTopNav, linearBottomSetting;


    static Uri photoUri;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Intent callingIntent = getIntent();
        Bundle bundle = callingIntent.getExtras();
        ArrayList<Photo> myPhotoList = bundle.getParcelableArrayList("listPhoto");
        Log.i("SIZE", "" + myPhotoList.size());

        int position = bundle.getInt("position");

        btnShare = (ImageButton) findViewById(R.id.btnShare);
        btnMore = (ImageButton) findViewById(R.id.btnMore);
        imageDisplay = (TouchImageView) findViewById(R.id.show_main_photo);
        linearTopNav = (LinearLayout) findViewById(R.id.linearTopNav);
        linearBottomSetting = (LinearLayout) findViewById(R.id.linearBottomSetting);


        btnShare.setOnClickListener(this);

        photoUri = myPhotoList.get(position).get_imageUri();
        imageDisplay.setImageURI(photoUri);

       imageDisplay.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View view) {
                if (!settingPop) {
                    linearTopNav.setVisibility(View.VISIBLE);
                    linearBottomSetting.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        getWindow().getInsetsController().show(WindowInsets.Type.statusBars());
                    } else {
                        getWindow().clearFlags(
                                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        );
                    }
                } else {
                    linearTopNav.setVisibility(View.INVISIBLE);
                    linearBottomSetting.setVisibility(View.INVISIBLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        getWindow().getInsetsController().hide(WindowInsets.Type.statusBars());
                        Log.v("Tag", "Hi em");
                    } else {
                        getWindow().addFlags(
                                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        );
                    }
                }
                settingPop = !settingPop;            }
        });
        btnMore.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == btnShare.getId()) {
            Intent shareImageIntent = shareImage(this, "");
            startActivity(shareImageIntent);
        } else if (view.getId() == btnMore.getId()) {
            showPopup(view);
        }
    }

    /**
     * function that pop up the menu when button More got hit
     *
     * @param v the view that got hit
     */
    public void showPopup(View v) {
        PopupMenu popupMenu = new PopupMenu(this, btnMore);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.camera:
                        openCamera();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.inflate(R.menu.photo_menu);
        popupMenu.show();
    }

    /**
     * function that create an intent to open camera
     *
     * @no params
     */

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
            e.printStackTrace();
        }
    }

    


    /**
     * function handle activity result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Picture captured!!!", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * share image to another application
     *
     * @param context     the context that handle this
     * @param pathToImage absolute path to image
     * @return an intent to start
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("deprecation")
    public static Intent shareImage(Context context, String pathToImage) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        else
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);

        shareIntent.setType("image/*");


        shareIntent.putExtra(Intent.EXTRA_STREAM, photoUri);
        return shareIntent;
    }
}

