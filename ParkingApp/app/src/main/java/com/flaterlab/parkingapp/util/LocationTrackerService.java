package com.flaterlab.parkingapp.util;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import static com.flaterlab.parkingapp.screen.ParkingApp.LOCATION_PERMISSION;

public abstract class LocationTrackerService extends BaseService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "LocationTracker";
    private static final int UPDATE_INTERVAL = 5000;
    private static final int FASTEST_UPDATE_INTERVAL = 3000;
    private static final int HIGH_ACCURACY = LocationRequest.PRIORITY_HIGH_ACCURACY;
    private static final int LOW_ACCURACY = LocationRequest.PRIORITY_NO_POWER;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final LocationCallback mLocationCallback = new LocationTrackerCallback();
    private int mCurrentPriority = HIGH_ACCURACY;

    @Override
    public abstract void onLocationChanged(Location location);

    protected abstract void onDutyStatusChanged(boolean permissionGranted);

    @Override
    public void onCreate() {
        super.onCreate();
        initGoogleApiClient();
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    protected synchronized void initGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    protected void setHighAccuracy(boolean accuracy) {
        if (mLocationRequest != null && accuracy ^ (mCurrentPriority == HIGH_ACCURACY)) {
            if (accuracy) {
                mCurrentPriority = HIGH_ACCURACY;
            } else {
                mCurrentPriority = LOW_ACCURACY;
            }
            log(TAG, "Accuracy changed");
            stopRequestingUpdates();
            startLocationUpdate();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        log(TAG, "Connected");
        startLocationUpdate();
    }

    @SuppressLint("MissingPermission")
    protected void startLocationUpdate() {
        checkLocationPermission();
        setLocationRequestParams();
        if (PermissionUtils.isPermissionsGranted(this, LOCATION_PERMISSION)) {
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
    }

    private void setLocationRequestParams() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(mCurrentPriority);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
    }

    @Override
    public void onConnectionSuspended(int i) {
        log(TAG, "Connection suspended " + i);
    }

    @Override
    public void onDestroy() {
        if (mGoogleApiClient != null) {
            stopRequestingUpdates();
        }
        super.onDestroy();
    }

    protected void stopRequestingUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        log(TAG, "Connection failed");
    }

    protected void checkLocationPermission() {
        if (!PermissionUtils.isPermissionsGranted(this, LOCATION_PERMISSION)) {
            turnDutyStatus(PermissionUtils.isPermissionsGranted(this, LOCATION_PERMISSION));
            log(TAG, "Service disabled");
        }
    }

    private void turnDutyStatus(boolean isAllowed) {
        onDutyStatusChanged(isAllowed);
    }

    private class LocationTrackerCallback extends LocationCallback {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            onLocationChanged(locationResult.getLastLocation());
        }
    }
}

