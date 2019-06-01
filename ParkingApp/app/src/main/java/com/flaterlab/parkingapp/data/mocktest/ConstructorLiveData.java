package com.flaterlab.parkingapp.data.mocktest;

import android.arch.lifecycle.LiveData;

import com.flaterlab.parkingapp.data.Response;

public class ConstructorLiveData<M> extends LiveData<Response<M>> {

    public ConstructorLiveData(Response<M> value) {
        setValue(value);
    }
}
