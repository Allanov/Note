package com.flaterlab.parkingapp.data;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.flaterlab.parkingapp.model.ParkingZone;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface ParkingZoneRepository {

    @NonNull
    LiveData<Response<List<ParkingZone>>> getAllParkingZones();

    @NonNull
    LiveData<Response<ParkingZone>> getParkingZone(String id);

    @NonNull
    LiveData<Response<ParkingZone>> getParkingZone(LatLng latLng);

    @NonNull
    LiveData<Response<String>> polygonReserved(String zoneId, String pid, boolean status);
}
