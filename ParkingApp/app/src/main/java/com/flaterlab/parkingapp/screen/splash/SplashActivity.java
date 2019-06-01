package com.flaterlab.parkingapp.screen.splash;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.flaterlab.parkingapp.R;
import com.flaterlab.parkingapp.screen.main.MainActivity;
import com.flaterlab.parkingapp.service.WaitingService;
import com.flaterlab.parkingapp.util.BaseActivity;
import com.flaterlab.parkingapp.util.PermissionUtils;

import static com.flaterlab.parkingapp.screen.ParkingApp.LOCATION_PERMISSION;

public class SplashActivity extends BaseActivity {

    private static final int LOCATION_REQUEST_ID = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        PermissionUtils.requestPermission(this, LOCATION_REQUEST_ID, LOCATION_PERMISSION, true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        boolean mFineGranted = requestCode == LOCATION_REQUEST_ID &&
                PermissionUtils.isPermissionsGranted(permissions, grantResults, LOCATION_PERMISSION);

        if (mFineGranted) {
            startMainActivity();
        } else {
            showPermissionDeniedDialog();
        }
    }


    private void startMainActivity() {
        try {
            Thread.sleep(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void showPermissionDeniedDialog() {
        PermissionUtils.PermissionDeniedDialog.newInstance(true)
                .show(getSupportFragmentManager(), "denied");
    }
}
