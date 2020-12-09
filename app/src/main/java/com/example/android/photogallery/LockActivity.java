package com.example.android.photogallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import androidx.security.crypto.MasterKeys;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;

public class LockActivity extends AppCompatActivity {
    public static final String SHARE_PREFERENCES = "SHARE_PREFERENCES";
    private static final String APP_PREFERENCES = "secret_shared_prefs";
    public static final String KEY_PIN_CODE = "PIN_CODE";
    public static final String KEY_SET = "SET";
    public static final String KEY_PASSWORD = "PASSWORD";
    private static final int MAX_ATTEMPTS = 5;
    private static final String KEY_MESSAGE = "MESSAGE";

    private static final int KEY_SIZE = 256;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    EditText editTextInput;
    Button btnDone, btnFingerprint;
    TextView textViewLabel;
    boolean isSet, reType = false,isCompleted = false;

    String firstPin = "";

    SharedPreferences password = null, sharedPreferences = null;

    int attempts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        setUpFingerPrintPrompt();

        sharedPreferences = getSharedPreferences(SHARE_PREFERENCES, MODE_PRIVATE);

        editTextInput = findViewById(R.id.editTextInput);
        btnDone = findViewById(R.id.btnDone);
        btnFingerprint = findViewById(R.id.btnFingerprint);
        textViewLabel = findViewById(R.id.textViewLabel);

        Intent receiveIntent = getIntent();
        isSet = receiveIntent.getBooleanExtra(KEY_SET, false);

        if (isSet) {
            btnFingerprint.setVisibility(View.GONE);
        } else {
            btnFingerprint.setVisibility(View.VISIBLE);
        }

        password = getEncryptedSharedPreferences();

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSet) {
                    if (!reType) {
                        setPinCode();
                    } else {
                        if (editTextInput.getText().toString().equals(firstPin)) {
                            onSetPinCodeCompleted();
                        } else {
                            Toast.makeText(LockActivity.this, "Pin codes do not match !!! Retry !!!", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    String pinCode = password.getString(KEY_PASSWORD, "");
                    if (editTextInput.getText().toString().equals(pinCode)) {
                        //success
                        onSuccess();
                    } else {
                        // fail
                        onPinCodeFail();
                        attempts++;
                    }
                }
            }
        });

        btnFingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prompt appears when user clicks "Log in".
                // Consider integrating with the keystore to unlock cryptographic operations,
                // if needed by your app.
                biometricPrompt.authenticate(promptInfo);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isSet){
            if (!isCompleted)
                onSetUpFail();
        } else {
            Intent returnResult = new Intent();
            returnResult.putExtra(KEY_MESSAGE, false);
            setResult(Activity.RESULT_CANCELED, returnResult);
            finish();
        }
    }

    /**
     * handle if user want to set a new pin code
     */
    void setPinCode() {
        firstPin = editTextInput.getText().toString();
        textViewLabel.setText(getString(R.string.retype_pin_code));
        editTextInput.setText("");
        reType = true;
    }


    /**
     * handle if set up failed
     */
    void onSetUpFail(){
        Intent returnResult = new Intent();
        returnResult.putExtra(KEY_MESSAGE, true);
        returnResult.putExtra(KEY_SET,"Pin Code set up fail !!!");
        setResult(Activity.RESULT_CANCELED, returnResult);
        finish();
    }



    /**
     * handle when the pin code is set completely
     */
    void onSetPinCodeCompleted() {
        SharedPreferences.Editor passEditor = password.edit();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_PIN_CODE, true);
        passEditor.putString(KEY_PIN_CODE, editTextInput.getText().toString());
        editor.apply();
        passEditor.apply();
        Intent returnResult = new Intent();
        returnResult.putExtra(KEY_MESSAGE, true);
        returnResult.putExtra(KEY_SET, "Pin Code Set Completed !!!");
        setResult(Activity.RESULT_OK, returnResult);
        isCompleted = true;
        finish();
    }

    /**
     * when user success on setting new lock
     */
    void onSuccess() {
        Intent returnResult = new Intent();
        returnResult.putExtra(KEY_MESSAGE, true);
        setResult(Activity.RESULT_OK, returnResult);
        finish();
    }

    /**
     * handle when pin code is wrong
     */
    void onPinCodeFail() {
        Toast.makeText(this, "Wrong pin code !!! Try again !!!", Toast.LENGTH_SHORT).show();
        if (attempts == MAX_ATTEMPTS) {
            Intent returnResult = new Intent();
            returnResult.putExtra(KEY_MESSAGE, false);
            setResult(Activity.RESULT_CANCELED, returnResult);
            finish();
        }
    }

    /**
     * set up new Prompt for biometrics
     */

    void setUpFingerPrintPrompt() {
        executor = ContextCompat.getMainExecutor(LockActivity.this);
        biometricPrompt = new BiometricPrompt(LockActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(LockActivity.this,
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();

            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(LockActivity.this,
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                onSuccess();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(LockActivity.this, "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }


        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                // Can't call setNegativeButtonText() and
                // setAllowedAuthenticators(...|DEVICE_CREDENTIAL) at the same time.
                // .setNegativeButtonText("Use account password")
                .setNegativeButtonText("Use pin code")
                .build();
    }

    /**
     * generate a masterkey
     * @return MasterKey
     */
    MasterKey getMasterKey() {
        try {
            KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
                    MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(KEY_SIZE)
                    .build();

            return new MasterKey.Builder(LockActivity.this)
                    .setKeyGenParameterSpec(spec)
                    .build();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error on getting master key", e);
        }
        return null;
    }

    /**
     * get a secure share preferences
     * @return Encrypted Share preference
     */
    private SharedPreferences getEncryptedSharedPreferences() {
        try {
            return EncryptedSharedPreferences.create(
                    LockActivity.this,
                    APP_PREFERENCES,
                    getMasterKey(), // calling the method above for creating MasterKey
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error on getting encrypted shared preferences", e);
        }
        return null;
    }
}