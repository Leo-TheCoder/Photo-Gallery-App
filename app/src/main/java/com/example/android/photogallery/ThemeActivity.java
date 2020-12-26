package com.example.android.photogallery;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class ThemeActivity extends AppCompatActivity {
    public static final String SHARE_PREFERENCES = "SHARE_PREFERENCES";

    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
        SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARE_PREFERENCES, 0); // 0 - for private mode
        RadioGroup radioGroup = findViewById(R.id.radio_theme);
        int theme = pref.getInt("THEME", -1);
        switch (theme) {
            case AppCompatDelegate.MODE_NIGHT_NO:
                radioGroup.check(R.id.radio_light);
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                radioGroup.check(R.id.radio_dark);
                break;
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                radioGroup.check(R.id.radio_default);
                break;
        }

        editor = pref.edit();
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_light:
                if (checked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putInt("THEME", AppCompatDelegate.MODE_NIGHT_NO);
                    editor.commit();
                }
                break;
            case R.id.radio_dark:
                if (checked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putInt("THEME", AppCompatDelegate.MODE_NIGHT_YES);
                    editor.commit();
                }
                break;
            case R.id.radio_default:
                if (checked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    editor.putInt("THEME", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    editor.commit();
                }
                break;
        }
    }

    public void onBackButtonClicked(View view) {
        finish();
    }
}