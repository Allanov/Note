package com.flaterlab.parkingapp;

import android.util.Log;

import com.flaterlab.parkingapp.util.DialogUtils;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class ExampleUnitTest {
    @Test
    public void date_logging() {
        Log.d("Mylog", new Date().toString());
    }
}