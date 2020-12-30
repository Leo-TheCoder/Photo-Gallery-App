package com.example.android.photogallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.photogallery.Models.Photo;
import com.example.android.photogallery.Utils.BitmapFileUtils;
import com.example.android.photogallery.Utils.PhotoUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class PhotoDisplayActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int MY_CAMERA_PERMISSION_CODE = 200;
    private static final int TRASH_IMAGE_REQUEST = 201;
    private static final int FAV_IMAGE_REQUEST = 202;

    private boolean settingPop = true;

    TextView imageName, imageTime;
    ImageButton btnShare, btnMore, btnBack, btnEdit, btnDelete,btnFavorite;
    TouchImageView imageDisplay;
    LinearLayout linearTopNav, linearBottomSetting;

    private boolean isFakeOn = false;

    static Uri photoUri;
    private Photo thisPhoto;
    String sendingUri;

    LinearLayout layoutBottomSheet;

    BottomSheetBehavior sheetBehavior;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Intent callingIntent = getIntent();
        Bundle bundle = callingIntent.getExtras();
        isFakeOn = bundle.getBoolean("isFake");
        ArrayList<Photo> myPhotoList = bundle.getParcelableArrayList("listPhoto");
        Log.i("SIZE", "" + myPhotoList.size());

        int position = bundle.getInt("position");
        thisPhoto = myPhotoList.get(position);
        imageName = (TextView) findViewById(R.id.textViewTitle);
        imageTime = (TextView) findViewById(R.id.textViewUploadTime);
        btnShare = (ImageButton) findViewById(R.id.btnShare);
        btnMore = (ImageButton) findViewById(R.id.btnMore);
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnDelete = (ImageButton) findViewById(R.id.btnDelete);
        btnEdit = (ImageButton) findViewById(R.id.btnEdit);
        btnFavorite = (ImageButton) findViewById(R.id.btnFavorite);
        if(thisPhoto.is_favorite()){
            btnFavorite.setImageResource(R.drawable.baseline_favorite_border_red_24dp);
        }
        else {
            btnFavorite.setImageResource(R.drawable.baseline_favorite_border_white_24dp);
        }

        imageDisplay = (TouchImageView) findViewById(R.id.show_main_photo);
        linearTopNav = (LinearLayout) findViewById(R.id.linearTopNav);
        linearBottomSetting = (LinearLayout) findViewById(R.id.linearBottomSetting);


        btnShare.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        imageName.setText(thisPhoto.get_dateTitle());
        imageTime.setText(thisPhoto.get_dateHourTitle());
        photoUri = myPhotoList.get(position).get_imageUri();
        sendingUri = photoUri.toString();
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
                settingPop = !settingPop;
            }
        });
        btnMore.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnFavorite.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

        if(isFakeOn){
            btnDelete.setEnabled(false);
            btnFavorite.setEnabled(false);
            btnMore.setEnabled(false);
            btnEdit.setEnabled(false);
            btnShare.setEnabled(false);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == btnShare.getId()) {
            Intent shareImageIntent = shareImage(this, "");
            startActivity(shareImageIntent);
        } else if (view.getId() == btnMore.getId()) {
            showPopup(view);
        } else if (view.getId() == btnEdit.getId()) {
            Log.i("CLICK EDIT", "" + sendingUri);
            Intent photoEditIntent = new Intent(this, EditPhotoActivity.class);
            photoEditIntent.putExtra("photoUri", sendingUri);
            this.startActivity(photoEditIntent);
        } else if (view.getId() == btnDelete.getId()) {
            deleteOrFavoriteImage(true);
        }else if (view.getId() == btnFavorite.getId()) {
            deleteOrFavoriteImage(false);
        }
        else if (view.getId() == btnBack.getId()) {
            finish();
        }
    }

    /**
     * Show a message dialog ask whether user want to trash the image
     */
    void deleteOrFavoriteImage(boolean isTrash) {
        // change this if query multi images at a time
        ArrayList<Uri> uris = new ArrayList<>();
        uris.add(photoUri);

        PendingIntent pendingIntent = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            if (isTrash) {
                pendingIntent = MediaStore.createTrashRequest(
                        getContentResolver(), uris
                        , true);
                try {
                    PhotoDisplayActivity.this.startIntentSenderForResult(pendingIntent.getIntentSender(),TRASH_IMAGE_REQUEST, null, 0, 0, 0);
                    Log.e("TAG","Sended!!!");
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } else {
                if(!thisPhoto.is_favorite()) {
                    pendingIntent = MediaStore.createFavoriteRequest(
                            getContentResolver(), uris
                            , true);
                    btnFavorite.setImageResource(R.drawable.baseline_favorite_border_red_24dp);
                    thisPhoto.set_favorite(true);
                }
                else {
                    pendingIntent = MediaStore.createFavoriteRequest(
                            getContentResolver(), uris
                            , false);
                    btnFavorite.setImageResource(R.drawable.baseline_favorite_border_white_24dp);
                    thisPhoto.set_favorite(false);
                }

                try {
                    PhotoDisplayActivity.this.startIntentSenderForResult(pendingIntent.getIntentSender(),FAV_IMAGE_REQUEST, null, 0, 0, 0);
                    Log.e("TAG","Sended!!!");
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        }
        else {

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
                if (menuItem.getItemId() == R.id.photoMenuCamera) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                    } else {
                        openCamera();
                    }
                    return true;
                } else if (menuItem.getItemId() == R.id.photoAddToAlbum){
                    bucketListDialog();
                } else if (menuItem.getItemId() == R.id.photoDetail){
                    imageDetailDialog();
                }
                return false;
            }
        });
        popupMenu.inflate(R.menu.photo_menu);
        popupMenu.show();
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    public void imageDetailDialog(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.SheetDialog);
        View dialogView = getLayoutInflater().inflate(R.layout.layout_photo_detail,null);
        bottomSheetDialog.setContentView(dialogView);

        TextView photoName,photoSize,photoDir,photoDate,photoDes,photoRes;
        photoName = (TextView)dialogView.findViewById(R.id.photoName);
        photoSize = (TextView)dialogView.findViewById(R.id.photoSize);
        photoDir = (TextView)dialogView.findViewById(R.id.photoDir);
        photoDate = (TextView)dialogView.findViewById(R.id.photoAddedDate);
        photoDes = (TextView)dialogView.findViewById(R.id.photoDescription);
        photoRes = (TextView)dialogView.findViewById(R.id.photoResolution);

        Bundle bundle = PhotoUtils.getImageDetails(this,photoUri);

        Date date = new Date(bundle.getLong("DATE_ADDED")*100L);

        photoName.setText(bundle.getString("DISPLAY_NAME"));

        String size = bundle.getString("SIZE");

        photoSize.setText("Size: " + Long.parseLong(size)/1024+ " KB");
        photoDir.setText(BitmapFileUtils.getFilePath(this,photoUri));
        photoDate.setText(date.toString());
        if (bundle.getString("DESCRIPTION") == null){
            photoDes.setText("No Description !!!");
        } else {
            photoDes.setText(bundle.getString("DESCRIPTION"));
        }
        photoRes.setText("RES: " + bundle.getString("RESOLUTION"));

        bottomSheetDialog.show();
    }

    /**
     * Display a dialog of bucket for user to choose
     */
    public void bucketListDialog(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle("Select Album Name");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        arrayAdapter.addAll(PhotoUtils.getBucketList());

        builderSingle.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setNeutralButton("NEW ALBUM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder newAlbumDialog = new AlertDialog.Builder(PhotoDisplayActivity.this);

                newAlbumDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                newAlbumDialog.setTitle(getString(R.string.new_album));
                newAlbumDialog.setMessage(getString(R.string.album_name));

                final EditText input = new EditText(PhotoDisplayActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                newAlbumDialog.setView(input); // uncomment this line

                newAlbumDialog.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String bucketName = input.getText().toString();
                        Log.e("BUCKET: ",bucketName);
                        if (bucketName.equals("")){
                            Toast.makeText(PhotoDisplayActivity.this, "Invalid Album Name", Toast.LENGTH_SHORT).show();
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                Log.e("BUCKET: ",bucketName);
                                if (BitmapFileUtils.moveImageToAnotherBucket(PhotoDisplayActivity.this,photoUri,bucketName)){
                                    Toast.makeText(PhotoDisplayActivity.this, "Photo Moved Successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(PhotoDisplayActivity.this, "Photo Moved Failed!!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });
                newAlbumDialog.show();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (BitmapFileUtils.moveImageToAnotherBucket(PhotoDisplayActivity.this,photoUri,strName)){
                        Toast.makeText(PhotoDisplayActivity.this, "Photo Moved Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PhotoDisplayActivity.this, "Photo Moved Failed!!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        builderSingle.show();
    }


    /**
     * function that create an intent to open camera
     *
     * @no params
     */

    private void openCamera() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();

                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * function handle activity result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BitmapFileUtils.REQUEST_IMAGE_CAPTURE) {
            try {
                if (!BitmapFileUtils.saveImageCapturedByCameraToStorage(this, BitmapFileUtils.CAMERA)) {
                    Toast.makeText(this, "Error saving picture!!!", Toast.LENGTH_SHORT).show();
                }
                ;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == TRASH_IMAGE_REQUEST){
            if (resultCode == RESULT_OK)
                finish();
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


    /**
     * calling camera
     */
    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = BitmapFileUtils.createImageFile(this);
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, BitmapFileUtils.REQUEST_IMAGE_CAPTURE);
            }
        }
    }
}