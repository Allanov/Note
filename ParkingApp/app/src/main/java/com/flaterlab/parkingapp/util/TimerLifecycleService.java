package com.flaterlab.parkingapp.util;

import android.arch.lifecycle.LifecycleService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public abstract class TimerLifecycleService extends LifecycleService {

    private static final long PARKING_START_IN_SECONDS = 60;
    private static final int COUNTING_PERIOD_IN_MIN_SEC = 1000;

    private Timer timer;
    private Handler mHandler;

    protected abstract void onSecondPassed(long second);

    protected abstract void onTimeFinished();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
    }

    protected void countdown() {
        stopCounting();
        timer = new Timer();
        Counter counter = new Counter();
        timer.schedule(counter, 1000, COUNTING_PERIOD_IN_MIN_SEC);
    }

    protected void stopCounting() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private class Counter extends TimerTask {

        private long counter = PARKING_START_IN_SECONDS;

        @Override
        public void run() {
            mHandler.post(this::countdown);
        }

        private void countdown() {
            onSecondPassed(counter--);
            if (counter < 0) {
                onTimeFinished();
                stopCounting();
            }
        }
    }

    protected void log(String tag, String msg) {
        Log.d("Mylog" + tag, msg);
    }
}