package com.flaterlab.parkingapp.screen.fragments;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flaterlab.parkingapp.R;
import com.flaterlab.parkingapp.data.Response;
import com.flaterlab.parkingapp.model.ParkingZone;
import com.flaterlab.parkingapp.model.Polygon;
import com.flaterlab.parkingapp.screen.main.MainViewModel;
import com.flaterlab.parkingapp.util.BaseFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class MapFragment extends BaseFragment implements OnMapReadyCallback {

    private static final String TAG = "MapFragment";
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private MainViewModel mViewModel;
    private List<ParkingZone> mCurrentParkingZones;
    private ParkingZone mCurrentParkingZone;
    private Polygon mCurrentPolygon;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        mMapView = rootView.findViewById(R.id.mv_main);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        mMapView.getMapAsync(this);

        return rootView;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        int marginTopInDp = 72;
        float paddingTop = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                marginTopInDp,
                getResources().getDisplayMetrics());
        mGoogleMap.setPadding(0, (int)paddingTop, 0, 0); // Map padding top
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        //mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mGoogleMap.setMyLocationEnabled(true);

        initObservers();
    }

    private void initObservers() {
        mViewModel.getAllParkingZones().observe(this, parkingZones -> {
            log(TAG, parkingZones + "");
            if (parkingZones != null) {
                mCurrentParkingZones = parkingZones;
                drawZones(mCurrentParkingZones);

                LatLng parking = mCurrentParkingZones.get(0).getLocation();
                mGoogleMap.moveCamera(CameraUpdateFactory.zoomBy(mGoogleMap.getMaxZoomLevel()));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(parking));
            }
        });

        mViewModel.getCurrentPolygon().observe(this, polygon -> {
            if (mCurrentPolygon != null) {
                mCurrentPolygon.makeFree();
            }
            if (polygon != null) {
                mCurrentPolygon = polygon;
                mCurrentPolygon.makeReserved();
            }
        });

        mViewModel.getClickedPolygon().observe(this, polygon -> {
            if (polygon != null) {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(polygon.getCenter(), 21));
            }
        });
    }

    private void drawZones(List<ParkingZone> zones) {
        for (ParkingZone zone : zones) {
            zone.drawOnMap(mGoogleMap);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        //mService.removeLocationListener(this);
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public interface MapViewModel {
        LiveData<Location> getCurrentLocation();
        LiveData<Response<ParkingZone>> getParkingZone(String id);
        LiveData<List<ParkingZone>> getAllParkingZones();
    }
}

