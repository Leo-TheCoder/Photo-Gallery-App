package com.example.android.photogallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class SettingActivity extends AppCompatActivity  implements View.OnClickListener {

    Button accountBtn,themeBtn,displayModeBtn,HAFBtn,secureBtn;
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //set up view
        accountBtn = (Button)findViewById(R.id.btnAccount);
        accountBtn.setVisibility(View.GONE);
        themeBtn = (Button)findViewById(R.id.btnTheme);
        displayModeBtn = (Button)findViewById(R.id.btnDisplayMode);
        displayModeBtn.setVisibility(View.GONE);
        HAFBtn = (Button)findViewById(R.id.btnHAF);
        HAFBtn.setVisibility(View.GONE);
        secureBtn = (Button)findViewById(R.id.btnSecure);
        btnBack = (ImageButton)findViewById(R.id.btnBack);

        accountBtn.setOnClickListener(this);
        themeBtn.setOnClickListener(this);
        displayModeBtn.setOnClickListener(this);
        HAFBtn.setOnClickListener(this);
        secureBtn.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == accountBtn.getId()){

        } else if (view.getId() == themeBtn.getId()){
            Intent themeIntent = new Intent(this, ThemeActivity.class);
            startActivity(themeIntent);
        }  else if (view.getId() == displayModeBtn.getId()) {

        } else if (view.getId() == secureBtn.getId()){
            Intent lockTypeIntent = new Intent(this, LockTypeActivity.class);
            startActivity(lockTypeIntent);
        } else if (view.getId() == btnBack.getId()){
            finish();
        }
    }
}