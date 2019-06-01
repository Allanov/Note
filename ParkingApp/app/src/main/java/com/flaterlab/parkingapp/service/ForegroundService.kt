package com.flaterlab.parkingapp.service

import android.app.PendingIntent
import android.arch.lifecycle.LifecycleService
import android.arch.lifecycle.Observer
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.flaterlab.parkingapp.R
import com.flaterlab.parkingapp.model.ParkingStatus
import com.flaterlab.parkingapp.screen.ParkingApp.CHANNEL_ID
import com.flaterlab.parkingapp.screen.main.MainActivity

class ForegroundService : LifecycleService() {

    private lateinit var mNotificationBuilder: NotificationCompat.Builder

    private var mWaitingServiceConnected = false

    override fun onCreate() {
        super.onCreate()
        initNotificationBuilder()
        startForeground(FOREGROUND_SERVICE_ID, mNotificationBuilder.build())
        val intent = Intent(this, WaitingService::class.java)
        application.bindService(intent, WaitingServiceConnection(), Context.BIND_ABOVE_CLIENT)
    }

    private fun initNotificationBuilder() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        mNotificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_splash_screen)
                .setContentTitle(getString(R.string.foreground_service_title))
                .setContentText(getString(R.string.foreground_service_msg))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
    }

    private fun refreshNotification(polygonSignature: String, status: String) {
        val polygon = application.getString(R.string.notif_title_polygon) + polygonSignature
        mNotificationBuilder.setContentTitle(polygonSignature)
                .setContentText(String.format(polygon, status))
        NotificationManagerCompat.from(this).notify(FOREGROUND_SERVICE_ID, mNotificationBuilder.build())
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    private inner class WaitingServiceConnection : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as WaitingService.WaitingServiceBinder
            binder.status.observe(this@ForegroundService, Observer {
                status: ParkingStatus? ->
                if (status != null) {
                    refreshNotification(status.polygonName, status.currentStatus)
                }
            })
    }

        override fun onServiceDisconnected(name: ComponentName) {
            mWaitingServiceConnected = false
        }
    }

    companion object {
        private const val FOREGROUND_SERVICE_ID = 1288
    }
}
