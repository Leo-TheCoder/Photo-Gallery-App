package com.example.android.photogallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingActivity extends AppCompatActivity  implements View.OnClickListener {

    Button accountBtn,themeBtn,displayModeBtn,HAFBtn,secureBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //set up view
        accountBtn = (Button)findViewById(R.id.btnAccount);
        themeBtn = (Button)findViewById(R.id.btnTheme);
        displayModeBtn = (Button)findViewById(R.id.btnDisplayMode);
        HAFBtn = (Button)findViewById(R.id.btnHAF);
        secureBtn = (Button)findViewById(R.id.btnSecure);

        accountBtn.setOnClickListener(this);
        themeBtn.setOnClickListener(this);
        displayModeBtn.setOnClickListener(this);
        HAFBtn.setOnClickListener(this);
        secureBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == accountBtn.getId()){

        } else if (view.getId() == themeBtn.getId()){

        }  else if (view.getId() == displayModeBtn.getId()) {

        } else if (view.getId() == secureBtn.getId()){
            Intent lockTypeIntent = new Intent(this, LockTypeActivity.class);
            startActivity(lockTypeIntent);
        }
    }
}