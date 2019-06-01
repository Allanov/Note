package com.flaterlab.parkingapp.util;

import android.support.v4.app.Fragment;
import android.util.Log;

public abstract class BaseFragment extends Fragment {

    protected void log(String tag, String msg) {
        Log.d("Mylog" + tag, msg);
    }
}
