package com.flaterlab.parkingapp.service;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;

import com.flaterlab.parkingapp.R;
import com.flaterlab.parkingapp.data.ParkingZoneRepository;
import com.flaterlab.parkingapp.model.ParkingResult;
import com.flaterlab.parkingapp.model.ParkingStatus;
import com.flaterlab.parkingapp.model.ParkingZone;
import com.flaterlab.parkingapp.model.Polygon;
import com.flaterlab.parkingapp.screen.ParkingApp;
import com.flaterlab.parkingapp.util.TimerLifecycleService;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WaitingService extends TimerLifecycleService {

    private static final String TAG = "WaitingService";
    private final IBinder binder = new WaitingServiceBinder();
    private boolean mServiceBound = false;
    private final ServiceConnection mLocationServiceConnection = new LocationServiceConnection();
    private MutableLiveData<Long> mParkStartingTimer = new MutableLiveData<>();
    private MutableLiveData<Polygon> mCurrentPolygon = new MutableLiveData<>();
    private MutableLiveData<ParkingResult> mParkingResult = new MutableLiveData<>();
    private MutableLiveData<ParkingZone> mCurrentZone = new MutableLiveData<>();
    private MutableLiveData<List<ParkingZone>> mParkingZones = new MutableLiveData<>();
    private MutableLiveData<ParkingStatus> mParkingStatus = new MutableLiveData<>();

    private ParkingZoneRepository mParkingRepo;
    private Date mParkingStartedDate;
    private LatLng mCurrentLatLng;
    private boolean IamInPolygon = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mParkingRepo = ((ParkingApp) getApplication()).getZoneRepository();
        fetchAllParkingZones();
        bindToLocationService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private void fetchAllParkingZones() {
        mParkingRepo.getAllParkingZones().observe(this, response -> {
            if (response != null) {
                if (response.isSuccess()) {
                    mParkingZones.setValue(response.getBody());
                } else if (response.isFailure()) {
                    // TODO: there is no parking zone
                } else if (response.isError()) {
                    // TODO: handle mLocationServiceConnection error
                }
            }
        });
    }

    private void bindToLocationService() {
        Intent intent = new Intent(this, LocationService.class);
        bindService(intent, mLocationServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return binder;
    }

    @Override
    protected void onSecondPassed(long second) {
        mParkStartingTimer.setValue(second);
        log(TAG, "onSecondPassed: " + second);
    }

    @Override
    protected void onTimeFinished() {
        mParkingStartedDate = Calendar.getInstance().getTime();
        log(TAG, mParkingStartedDate.toString());
        mParkStartingTimer.setValue(null);
        setCurrentPolygonStatus(R.string.notif_status_parked);
        log(TAG, "Time left, start counting parking time!");
    }

    public void onLocationChanged(Location location) {
        mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (mCurrentZone.getValue() == null) {
            getCurrentParkingZone();
        } else {
            manageParking();
        }
    }

    private void getCurrentParkingZone() {
        for (ParkingZone parkingZone : mParkingZones.getValue()) {
            if (parkingZone.contains(mCurrentLatLng)) {
                mCurrentZone.setValue(parkingZone);
            }
        }
    }

    private void manageParking() {
        if (IamInPolygon) {
            if (mParkingStartedDate != null &&
                    mCurrentPolygon.getValue() != null &&
                    !mCurrentPolygon.getValue().containsPoint(mCurrentLatLng)) {
                finishParking();
                checkIfIamInPolygon();
                log(TAG, "Parking finished!");
            }
        } else {
            checkIfIamInPolygon();
        }
    }

    private void finishParking() {
        Date endDate = Calendar.getInstance().getTime();
        log(TAG, "Start date: " + (endDate.getTime() - mParkingStartedDate.getTime()) + "End date: " + endDate.toString());
        ParkingResult result = new ParkingResult(
                String.valueOf(endDate.getTime()), "Name", mParkingStartedDate, endDate);
        mParkingResult.setValue(result);
        mParkingStartedDate = null;
    }

    private void checkIfIamInPolygon() {
        mCurrentPolygon.setValue(mCurrentZone.getValue().getCurrentPolygon(mCurrentLatLng));

        if (mCurrentPolygon.getValue() != null) {
            // in a polygon
            doInPolygon();
        } else {
            // not in polygon
            doOutsideOfPolygon();
        }
    }

    private void doInPolygon() {
        countdown();
        IamInPolygon = true;

        mParkingResult.setValue(null);
        sendCurrentPolygonStatusToServer(true);
        setCurrentPolygonStatus(R.string.notif_status_waiting);
    }

    private void doOutsideOfPolygon() {
        stopCounting();
        mParkStartingTimer.setValue(null);
        IamInPolygon = false;

        sendCurrentPolygonStatusToServer(false);
        setNotInPolygonStatus();
        log(TAG, "Car is not in any polygon!");
    }

    private void setCurrentPolygonStatus(int statusStringResource) {
        String title = getApplication().getString(R.string.notif_title_polygon);
        if (mCurrentPolygon.getValue() != null) {
            mParkingStatus.setValue(new ParkingStatus(
                    String.format(title, mCurrentPolygon.getValue().getId()),
                    getApplication().getString(statusStringResource)
            ));
        }
    }

    private void setNotInPolygonStatus() {
        mParkingStatus.setValue(new ParkingStatus(
                getString(R.string.notif_titile_no_polygon),
                getApplication().getString(R.string.notif_status_not_in_polygon)
        ));
    }

    private void sendCurrentPolygonStatusToServer(boolean reserved) {
        if (mCurrentZone.getValue() != null && mCurrentPolygon.getValue() != null) {
            mParkingRepo.polygonReserved(mCurrentZone.getValue().getId(),
                    mCurrentPolygon.getValue().getId(), reserved);
        }
    }

    @Override
    public void onDestroy() {
        if (mServiceBound) {
            unbindService(mLocationServiceConnection);
        }
        log(TAG, "onDestroy()");
        super.onDestroy();
    }

    public class WaitingServiceBinder extends Binder {
        public LiveData<Long> getParkStartingCountdown() {
            return mParkStartingTimer;
        }

        public LiveData<ParkingResult> getParkingResult() {
            return mParkingResult;
        }

        public LiveData<Polygon> getCurrentPolygon() {
            return mCurrentPolygon;
        }

        public LiveData<ParkingZone> getCurrentParkingZone() {
            return mCurrentZone;
        }

        public LiveData<List<ParkingZone>> getParkingZones() {
            return mParkingZones;
        }

        public LiveData<ParkingStatus> getStatus() {
            return mParkingStatus;
        }
    }

    private class LocationServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            LocationService.LocationServiceBinder binder = (LocationService.LocationServiceBinder) service;
            mServiceBound = true;
            binder.getLocationLiveData().observe(
                    WaitingService.this, WaitingService.this::onLocationChanged);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mServiceBound = false;
        }
    }
}
