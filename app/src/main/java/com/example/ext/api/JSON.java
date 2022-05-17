package com.example.ext.api;

import org.json.JSONException;
import org.json.JSONObject;

public class JSON {
    public static JSONObject decode(String json){
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            return new JSONObject();
        }
    }
    public static String loads(JSONObject json){
        return json.toString();
    }
}