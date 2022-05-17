package com.example.ext.ui.diary;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ext.api.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DiaryViewModel extends ViewModel {
    private String dairyData;
    private Map<String, ArrayList<String>> jsondairy = new HashMap<>();
    private final MutableLiveData<Map<String, ArrayList<String>>> mMap;

    public DiaryViewModel() {
        dairyData = DiaryFragment.data;
        mMap = new MutableLiveData<>();
        //preferences.edit().remove("dairyData").commit();
        if (dairyData.length() > 0) {
            try {
                jsondairy = deserialize(JSON.decode(dairyData));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else{
            jsondairy.put("Понедельник", new ArrayList<>());
            jsondairy.put("Вторник", new ArrayList<>());
            jsondairy.put("Среда", new ArrayList<>());
            jsondairy.put("Четверг", new ArrayList<>());
            jsondairy.put("Пятница", new ArrayList<>());
            jsondairy.put("Суббота", new ArrayList<>());
            jsondairy.put("Воскресенье", new ArrayList<>());
        }
        mMap.setValue(jsondairy);
    }


    public static Map<String, ArrayList<String>> deserialize(JSONObject json) throws JSONException {
        Map<String, ArrayList<String> > map = new HashMap<>();
        for (Iterator<String> it = json.keys(); it.hasNext(); ) {
            String key = it.next();
            JSONArray value = json.getJSONArray(key);
            ArrayList<String> list = new ArrayList<>();

            for(int i = 0; i < value.length(); i++) {
                list.add(value.getString(i));
            }
            map.put(key, list);
        }
        return map;
    }
    public LiveData<Map<String, ArrayList<String>>> getDiaryMap() {
        return mMap;
    }
}