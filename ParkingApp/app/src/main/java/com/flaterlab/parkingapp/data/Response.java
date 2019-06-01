package com.flaterlab.parkingapp.data;

public class Response<M> {
    private M body;
    private Status status = Status.ERROR;

    public Response() {
    }

    public Response(M body) {
        this.body = body;
    }

    public M getBody() {
        return body;
    }

    public void success() {
        status = Status.SUCCESS;
    }

    public void failure() {
        status = Status.FAILED;
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    public boolean isFailure() {
        return status == Status.FAILED;
    }

    public boolean isError() {
        return status == Status.ERROR;
    }

    private enum Status {
        SUCCESS,
        FAILED,
        ERROR
    }
}