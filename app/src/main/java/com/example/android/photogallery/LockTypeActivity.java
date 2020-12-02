package com.example.android.photogallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;

import androidx.biometric.BiometricManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;

import static android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

public class LockTypeActivity extends AppCompatActivity  implements View.OnClickListener {

    public static final String SHARE_PREFERENCES = "SHARE_PREFERENCES";
    public static final String KEY_PIN_CODE = "PIN_CODE";
    public static final String KEY_FINGERPRINT = "FINGERPRINT";
    private static final int SET_LOCK_REQUEST_CODE = 1;
    private static final int LOCK_REQUEST_CODE = 2;

    LinearLayout linPinCode,linFingerPrint,linNone;

    boolean isPinCodeEnabled;

    SharedPreferences sharedPref;

    SwitchMaterial switchFingerPrint;

    private static final int REQUEST_CODE_ENABLE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_type);

        linPinCode = (LinearLayout)findViewById(R.id.linPinCode);
        linFingerPrint = (LinearLayout)findViewById(R.id.linFingerPrint);
        linNone = (LinearLayout)findViewById(R.id.linNone);

        switchFingerPrint = (SwitchMaterial) findViewById(R.id.switchFingerPrint);

        sharedPref = getSharedPreferences(SHARE_PREFERENCES,MODE_PRIVATE);
        isPinCodeEnabled = sharedPref.getBoolean(KEY_PIN_CODE,false);

        //SET DEFAULT SWITCH VALUE
        switchFingerPrint.setChecked(sharedPref.getBoolean(KEY_FINGERPRINT,false));

        linPinCode.setOnClickListener(this);
        linNone.setOnClickListener(this);
        switchFingerPrint.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE_ENABLE:
                Toast.makeText(this, "PinCode enabled", Toast.LENGTH_SHORT).show();
                break;
            case LOCK_REQUEST_CODE:
                if (!data.getBooleanExtra("MESSAGE",false)) {
                    Runnable runnable = new Runnable() {
                        public void run() {
                            Log.i("Tag", "Access fail!!! Quitting!!!");

                        }
                    };
                    Toast.makeText(this,"Access fail!!! Quitting!!!",Toast.LENGTH_LONG).show();
                    Handler handler = new android.os.Handler();
                    handler.postDelayed(runnable, 1000);

                    handler.removeCallbacks(runnable);
                    finish();
                } else {
                    if (data.hasExtra("SET")){
                        Toast.makeText(this,data.getStringExtra("SET"),Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case SET_LOCK_REQUEST_CODE:
                BiometricManager biometricManager = BiometricManager.from(this);
                switchFingerPrint.setChecked(!(biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED));
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(KEY_FINGERPRINT, switchFingerPrint.isChecked());
                editor.apply();
        }
    }

    void sendSetLockIntent(){
        Intent setLockIntent = new Intent(this, LockActivity.class);
        setLockIntent.putExtra(LockActivity.KEY_SET,true);
        startActivityForResult(setLockIntent,LOCK_REQUEST_CODE);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == linPinCode.getId()){
            sendSetLockIntent();
        } else if (view.getId() == linNone.getId()){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(KEY_PIN_CODE, false);
            editor.putBoolean(KEY_FINGERPRINT, false);
            switchFingerPrint.setChecked(false);
            editor.apply();
            Toast.makeText(this, "Security removed !!!", Toast.LENGTH_SHORT).show();
        } else if (view.getId() == switchFingerPrint.getId()){

            SharedPreferences.Editor editor = sharedPref.edit();
            //handle finger print
            BiometricManager biometricManager = BiometricManager.from(this);
            switch (biometricManager.canAuthenticate()) {
                case BiometricManager.BIOMETRIC_SUCCESS:
                    Toast.makeText(this, "App can authenticate using biometrics.",Toast.LENGTH_LONG).show();
                    editor.putBoolean(KEY_FINGERPRINT, switchFingerPrint.isChecked());
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                    Toast.makeText(this, "No biometric features available on this device.",Toast.LENGTH_LONG).show();
                    switchFingerPrint.setChecked(false);
                    editor.putBoolean(KEY_FINGERPRINT, switchFingerPrint.isChecked());
                    break;
                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    Toast.makeText(this, "Biometric features are currently unavailable.",Toast.LENGTH_LONG).show();
                    switchFingerPrint.setChecked(false);
                    editor.putBoolean(KEY_FINGERPRINT, switchFingerPrint.isChecked());
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                    Toast.makeText(this, "You need to set up your device fingerprint to use it here!!!",Toast.LENGTH_LONG).show();
                    // Prompts the user to create credentials that your app accepts.
                    final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                    enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                            BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                    startActivityForResult(enrollIntent, SET_LOCK_REQUEST_CODE);
                    return;
            }
            editor.apply();
            Toast.makeText(this,"Fingerprint is set successfully!!!",Toast.LENGTH_LONG).show();
        }
    }
}