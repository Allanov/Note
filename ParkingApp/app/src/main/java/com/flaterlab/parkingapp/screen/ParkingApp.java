package com.flaterlab.parkingapp.screen;

import android.Manifest;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;

import com.flaterlab.parkingapp.R;
import com.flaterlab.parkingapp.data.ParkingZoneRepository;
import com.flaterlab.parkingapp.data.mocktest.ParkingZoneRepo;
import com.flaterlab.parkingapp.service.WaitingService;
import com.flaterlab.parkingapp.util.PermissionUtils;

public class ParkingApp extends Application {

    public static final String[] LOCATION_PERMISSION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    public static final String CHANNEL_ID = "111";

    private ParkingZoneRepository zoneRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        zoneRepository = new ParkingZoneRepo();
        if (PermissionUtils.isPermissionsGranted(this, LOCATION_PERMISSION)) {
            startWaitingService();
        }
    }

    public ParkingZoneRepository getZoneRepository() {
        return zoneRepository;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void startWaitingService() {
        Intent serviceIntent = new Intent(getApplicationContext(), WaitingService.class);
        serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startService(serviceIntent);
    }
}
