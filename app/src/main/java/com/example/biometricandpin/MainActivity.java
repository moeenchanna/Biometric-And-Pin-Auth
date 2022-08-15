package com.example.biometricandpin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private boolean isBiometricEnabled;
    private boolean isBiometricPresent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkBioMetric();
    }

    private void checkBioMetric() {
        checkIfBiometricIsSupported();
        if (isBiometricEnabled) {
            showBiometricPrompt();
        } else if (isBiometricPresent) {
            authAlert(getString(R.string.biometric_not_enabled));
        } else {
            authAlert(getString(R.string.biometric_not_present));
        }
    }

    public void authenticate(View view) {
        checkBioMetric();
    }

    private void authAlert(String data) {

        runOnUiThread(() -> {

            if (!isFinishing()) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Auth Alert")
                        .setMessage(data)
                        .setCancelable(false)
                        .setPositiveButton("ok", (dialog, which) -> {
                            // Whatever...

                        }).show();
            }
        });
    }

    private void checkIfBiometricIsSupported() {
        int id = BiometricManager.from(this).canAuthenticate();
        if (id == BiometricManager.BIOMETRIC_SUCCESS) {
            isBiometricEnabled = true;
            isBiometricPresent = true;
        } else if (id == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE) {
            Log.d(TAG, "No biometric features available on this device.");
            authAlert(getString(R.string.no_biometric_feature));
            isBiometricEnabled = false;
            isBiometricPresent = false;
        } else if (id == BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE) {
            Log.d(TAG, "Biometric features are currently unavailable.");
            authAlert(getString(R.string.no_biometric_feature_available));
            isBiometricEnabled = false;
            isBiometricPresent = false;
        } else if (id == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
            authAlert(getString(R.string.hasnt_associated_any_biometric_credentials));
            Log.d(TAG, "he user hasn't associated any biometric credentials with their account.");
            isBiometricEnabled = false;
            isBiometricPresent = true;
        }
    }

    private void showBiometricPrompt() {

        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Log.e(TAG, "onAuthenticationError: ");
                authAlert(getString(R.string.failed_biometric));
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Log.i(TAG, "onAuthenticationSucceeded: ");
                authAlert(getString(R.string.successfully_validated_biometric));
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Log.e(TAG, "onAuthenticationFailed: ");
                authAlert(getString(R.string.failed_biometric));
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Validation")
                .setSubtitle("This is is to validate using your biometric to check whether you are valid user to see the screen content. \n " +
                        "You can enter Device PIN if your fingerprint is failing")
                .setDeviceCredentialAllowed(true)
                .build();
        biometricPrompt.authenticate(promptInfo);
    }


}