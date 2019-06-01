package com.flaterlab.parkingapp.screen.main;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.flaterlab.parkingapp.data.ParkingZoneRepository;
import com.flaterlab.parkingapp.data.Response;
import com.flaterlab.parkingapp.model.ParkingResult;
import com.flaterlab.parkingapp.model.ParkingZone;
import com.flaterlab.parkingapp.model.Polygon;
import com.flaterlab.parkingapp.screen.fragments.MapFragment;
import com.flaterlab.parkingapp.service.LocationService;
import com.flaterlab.parkingapp.service.WaitingService;

import java.util.List;

public class MainViewModel extends AndroidViewModel implements MapFragment.MapViewModel {

    private ParkingZoneRepository parkingRepo;
    private MutableLiveData<Location> mCurrentLocation = new MutableLiveData<>();
    private MutableLiveData<List<ParkingZone>> mParkingZones = new MutableLiveData<>();
    private MutableLiveData<ParkingZone> mCurrentParkingZone = new MutableLiveData<>();
    private MutableLiveData<Polygon> mCurrentPolygon = new MutableLiveData<>();
    private MutableLiveData<Long> mTimer = new MutableLiveData<>();
    private MutableLiveData<ParkingResult> mResult = new MutableLiveData<>();
    private MutableLiveData<Polygon> mClickedPolygon = new MutableLiveData<>();

    private boolean mWaitingServiceConnected = false;
    LocationService.LocationServiceBinder mLocationServiceBinder;
    private boolean mLocationServiceConnected = false;

    public MainViewModel(@NonNull Application application) {
        super(application);

        Intent intentW = new Intent(getApplication(), WaitingService.class);
        getApplication().bindService(intentW, new WaitingServiceConnection(), Context.BIND_ABOVE_CLIENT);

        Intent intentS = new Intent(getApplication(), LocationService.class);
        getApplication().bindService(intentS, new LocationServiceConnection(), Context.BIND_AUTO_CREATE);
    }

    void init(ParkingZoneRepository parkingRepo) {
        this.parkingRepo = parkingRepo;
    }

    @Override
    public LiveData<Location> getCurrentLocation() {
        return mCurrentLocation;
    }

    @Override
    public LiveData<Response<ParkingZone>> getParkingZone(String id) {
        return parkingRepo.getParkingZone(id);
    }

    @Override
    public LiveData<List<ParkingZone>> getAllParkingZones() {
        return mParkingZones;
    }

    public LiveData<ParkingZone> getCurrentParkingZone() {
        return mCurrentParkingZone;
    }

    public LiveData<Polygon> getCurrentPolygon() {
        return mCurrentPolygon;
    }

    public LiveData<Long> getStartingCountingTimer() {
        return mTimer;
    }

    public LiveData<ParkingResult> getParkingResult() {
        return mResult;
    }

    public void onPolygonClicked(Polygon polygon) {
        mClickedPolygon.setValue(polygon);
    }

    public LiveData<Polygon> getClickedPolygon() {
        return mClickedPolygon;
    }

    public void setAccuracy(boolean isChecked) {
        if (mLocationServiceConnected) {
            mLocationServiceBinder.setHighAccuracy(isChecked);
        }
    }

    private class WaitingServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            WaitingService.WaitingServiceBinder mWaitingBinder = (WaitingService.WaitingServiceBinder) service;
            mWaitingServiceConnected = true;
            mWaitingBinder.getParkingZones().observeForever(zones ->
                    mParkingZones.setValue(zones));
            mWaitingBinder.getCurrentPolygon().observeForever(polygon ->
                    mCurrentPolygon.setValue(polygon));
            mWaitingBinder.getCurrentParkingZone().observeForever(zone ->
                    mCurrentParkingZone.setValue(zone));
            mWaitingBinder.getParkStartingCountdown().observeForever(time ->
                    mTimer.setValue(time));
            mWaitingBinder.getParkingResult().observeForever(result ->
                    mResult.setValue(result));
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mWaitingServiceConnected = false;
        }
    }

    private class LocationServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mLocationServiceBinder = (LocationService.LocationServiceBinder) service;
            mLocationServiceConnected = true;

            mLocationServiceBinder.getLocationLiveData().observeForever(location ->
                    mCurrentLocation.setValue(location));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mLocationServiceConnected = false;
        }
    }
}
