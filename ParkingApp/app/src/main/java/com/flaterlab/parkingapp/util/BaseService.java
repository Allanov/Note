package com.flaterlab.parkingapp.util;

import android.app.Service;
import android.util.Log;

public abstract class BaseService extends Service {

    protected void log(String tag, String msg) {
        Log.d("Mylog" + tag, msg);
    }
}
