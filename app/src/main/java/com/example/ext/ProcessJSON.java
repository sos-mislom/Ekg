package com.example.ext;

import org.json.JSONException;
import org.json.JSONObject;

class ProcJSON {
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