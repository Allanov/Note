package com.flaterlab.parkingapp.data.mocktest;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.flaterlab.parkingapp.data.ParkingZoneRepository;
import com.flaterlab.parkingapp.data.Response;
import com.flaterlab.parkingapp.model.ParkingZone;
import com.flaterlab.parkingapp.model.Polygon;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class ParkingZoneRepo implements ParkingZoneRepository {

    List<ParkingZone> parkingZones = new ArrayList<>();

    @NonNull
    @Override
    public LiveData<Response<List<ParkingZone>>> getAllParkingZones() {
        Response<List<ParkingZone>> response = new Response<>(getMockParkingZones());
        MutableLiveData<Response<List<ParkingZone>>> liveData = new MutableLiveData<>();
        response.success();
        liveData.setValue(response);

        return liveData;
    }

    @NonNull
    @Override
    public LiveData<Response<ParkingZone>> getParkingZone(String id) {
        String name = "Parking Zone " + id;
        Response<ParkingZone> response = new Response<>(
                new ParkingZone(id, name, getMockPolygons(), 1,
                        new LatLng(42.855945, 74.681521), getParkingPolygon()));
        response.success();
        return new ConstructorLiveData<>(response);
    }

    @NonNull
    @Override
    public LiveData<Response<String>> polygonReserved(String zoneId, String pid, boolean status) {
        Response<String> response = new Response<>("200");
        response.success();
        return new ConstructorLiveData<>(response);
    }

    @NonNull
    @Override
    public LiveData<Response<ParkingZone>> getParkingZone(LatLng latLng) {
        String name = "Parking Zone " + 1;
        Response<ParkingZone> response = new Response<>(
                new ParkingZone("1", name, getMockPolygons(), 1,
                        new LatLng(42.855945, 74.681521), getParkingPolygon()));
        response.success();
        return new ConstructorLiveData<>(response);
    }

    private List<ParkingZone> getMockParkingZones() {
        int id = 0;
        List<ParkingZone> zones = new ArrayList<>();
        zones.add(new ParkingZone(str(id++), "Alatoo", getMockPolygons(), 1, new LatLng(42.855945, 74.681521), getParkingPolygon()));

        return zones;
    }

    private List<Polygon> getMockPolygons() {
        int pid = 0;
        List<Polygon> polygons = new ArrayList<>();

        polygons.add(
                new Polygon(str(pid++),
                        new LatLng(42.855830, 74.681419),
                        new LatLng(42.855830, 74.681483),
                        new LatLng(42.855856, 74.681483),
                        new LatLng(42.855856, 74.681419),
                        true
        ));

        polygons.add(
                new Polygon(str(pid++),
                        new LatLng(42.855856, 74.681485),
                        new LatLng(42.855856, 74.681420),
                        new LatLng(42.855882, 74.681420),
                        new LatLng(42.855882, 74.681485),
                        true
                ));

        polygons.add(
                new Polygon(str(pid++),
                        new LatLng(42.855882, 74.681421),
                        new LatLng(42.855882, 74.681487),
                        new LatLng(42.855911, 74.681487),
                        new LatLng(42.855911, 74.681421),
                        false
                ));

        polygons.add(
                new Polygon(str(pid++),
                        new LatLng(42.855911, 74.681488),
                        new LatLng(42.855911, 74.681422),
                        new LatLng(42.855938, 74.681422),
                        new LatLng(42.855938, 74.681489),
                        true
                ));

        return polygons;
    }

    private List<LatLng> getParkingPolygon() {
        List<LatLng> corners = new ArrayList<>();
        corners.add(new LatLng(42.855625, 74.680808));
        corners.add(new LatLng(42.855601, 74.681763));
        corners.add(new LatLng(42.856655, 74.681704));
        corners.add(new LatLng(42.856600, 74.680754));
        return corners;
    }

    private String str(int n) {
        return String.valueOf(n);
    }
}
