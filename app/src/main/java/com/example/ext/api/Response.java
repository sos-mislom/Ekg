package com.example.ext.api;

import android.util.Log;

import org.json.JSONObject;

class Response {
    private final String string;
    private final int statusCode;

    public Response(String string, int statusCode) {
        this.string = string;
        this.statusCode = statusCode;
        Log.e("REQ", string);
    }

    public JSONObject json(){
        return JSON.decode(string);
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String toString() {
        return string;
    }
}