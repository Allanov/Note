package com.flaterlab.parkingapp.service;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.flaterlab.parkingapp.util.LocationTrackerService;

public class LocationService extends LocationTrackerService {

    private static final String TAG = "LocationService";
    private final LocationServiceBinder binder = new LocationServiceBinder();
    private final MutableLiveData<Location> currentLocation = new MutableLiveData<>();

    @Override
    public void onLocationChanged(Location location) {
        currentLocation.setValue(location);
        log(TAG, "Location ->>> " + location.getLatitude() + " - " + location.getLongitude());
    }

    @Override
    protected void onDutyStatusChanged(boolean permissionGranted) {
        log(TAG, "Enable to get location updates");
    }

    @Override
    public void onCreate() {
        log(TAG, "onCreate()");
        super.onCreate();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        super.onConnected(bundle);
    }

    @Override
    public IBinder onBind(Intent intent) {
        log(TAG, "onBind()");
        return binder;
    }

    public class LocationServiceBinder extends Binder {
        public LiveData<Location> getLocationLiveData() {
            return currentLocation;
        }

        public void setHighAccuracy(boolean highAccuracy) {
            LocationService.super.setHighAccuracy(highAccuracy);
        }
    }
}
