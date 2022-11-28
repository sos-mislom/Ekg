package com.example.ext.helper;

import static android.content.Context.MODE_PRIVATE;
import static com.example.ext.ConfigApiResponses.lenOfBreak;
import static com.example.ext.ConfigApiResponses.lenOfShortBreak;
import static com.example.ext.ConfigApiResponses.listOfIntervals;
import static com.example.ext.ConfigApiResponses.positionOfShortBreak;
import static com.example.ext.ConfigApiResponses.startTime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PreferencesUtil {
    private final String APP_VERSION_CODE = "APP_VERSION_CODE";
    private SharedPreferences sharedPreferencesAppVersionCode;
    private SharedPreferences.Editor editorAppVersionCode;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    public static String data;
    public static String username;
    public static String password;
    public static ArrayList<Integer> TimeList;
    public static String lessons_start;
    public static String long_break;
    public static String curt_break;

    public static ArrayList<String> aboutNewVersion;

    public PreferencesUtil(Context context) {

        mContext = context;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        password = preferences.getString("password", "");
        username = preferences.getString("username", "");
        data = preferences.getString("dairyData", "");

        lessons_start = preferences.getString("lessons_start", "");
        long_break = preferences.getString("long_break", "");
        curt_break = preferences.getString("curt_break", "");

        if (lessons_start.length() == 0){
            lessons_start = "8:10"; }
        if (long_break.length() == 0){
            long_break = "20"; }
        if (curt_break.length() == 0){
            curt_break = "10"; }

        TimeList = new ArrayList<Integer>();
        for (String x : lessons_start.split(":")){
            TimeList.add(Integer.parseInt(x));
        }

        sharedPreferencesAppVersionCode = mContext.getSharedPreferences(APP_VERSION_CODE,MODE_PRIVATE);
        editorAppVersionCode = sharedPreferencesAppVersionCode.edit();
    }

    public void createAppVersionCode(String versionCode) {
        editorAppVersionCode.putString(APP_VERSION_CODE, versionCode);
        editorAppVersionCode.apply();
    }

    public String getAppVersionCode() {
        return sharedPreferencesAppVersionCode.getString(APP_VERSION_CODE,"0.9");
    }

    public void checkAppVersionCodeInSite(){
        new async().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }


    public static void createListOfIntervals(ArrayList<Integer> mstartTime,
                                             ArrayList<Integer> mpositionOfShortBreak,
                                             int mlenOfBreak,
                                             int mlenOfShortBreak){
        startTime = mstartTime;
        positionOfShortBreak = mpositionOfShortBreak;
        lenOfBreak = mlenOfBreak;
        lenOfShortBreak = mlenOfShortBreak;
        createListOfIntervals();
    }

    private static void createListOfIntervals(){
        listOfIntervals = new ArrayList<ArrayList<String>>();

        listOfIntervals.add(new ArrayList<String>(Arrays.asList(
                formatTime(startTime),
                formatTime(new ArrayList<Integer>(Arrays.asList(
                        startTime.get(0),
                        startTime.get(1)+40)))
        )));

        for (int i = 0; i < 8; i++) {
            if (positionOfShortBreak.contains(i)){
                listOfIntervals.add(minusTime(startTime, lenOfShortBreak));
            } else {

                listOfIntervals.add(minusTime(startTime, lenOfBreak));
            }
        }
    }

    private static ArrayList<String> minusTime(ArrayList<Integer> listOfTime, int lenOfBreak){
        listOfTime = rebaseTime(new ArrayList<Integer>(
                Arrays.asList(listOfTime.get(0),
                        listOfTime.get(1)+40+lenOfBreak)));
        startTime = listOfTime;

        return new ArrayList<String>(Arrays.asList(formatTime(listOfTime),
                formatTime(rebaseTime(new ArrayList<Integer>(
                        Arrays.asList(listOfTime.get(0),
                                listOfTime.get(1) + 40))))));
    }

    private static ArrayList<Integer> rebaseTime(ArrayList<Integer> listOfTime){
        if (listOfTime.get(1) >= 60){
            return new ArrayList<Integer>(
                    Arrays.asList(
                            listOfTime.get(0) + (listOfTime.get(1) / 60),
                            listOfTime.get(1) - 60 * (listOfTime.get(1) / 60)));
        }
        return listOfTime;
    }

    private static String formatTime(ArrayList<Integer> listOfTime){
        if (listOfTime.get(0) < 2){
            return String.format(
                    "%s0:%s",
                    listOfTime.get(0).toString(),
                    listOfTime.get(1).toString());
        } else if (listOfTime.get(1) < 2){
            return String.format(
                    "%s:%s0",
                    listOfTime.get(0).toString(),
                    listOfTime.get(1).toString());
        }
        return String.format(
                "%s:%s",
                listOfTime.get(0).toString(),
                listOfTime.get(1).toString());
    }

    public class async extends AsyncTask<Void, Void, ArrayList<String>> {
        private ArrayList<String> GetVersion() {
            Post http = new Post();
            String url = "http://85.12.218.165:8080/";
            Map<String, String> data = new HashMap<>();
            data.put("version", getAppVersionCode());

            try {
                Response r = http.post(url + "api/check_version", data);
                JSONObject resp = new JSONObject(String.valueOf(r));

                if (resp.getBoolean("need_update")) {
                    JSONObject version = resp.getJSONObject("version");

                    String patchNote = version.getString("patchnote");
                    String urlNewApk = version.getString("url");
                    String newVersion = version.getString("version");

                    ArrayList<String> next_day_map = new ArrayList<>();
                    next_day_map.add("True");
                    next_day_map.add(patchNote);
                    next_day_map.add(urlNewApk);
                    next_day_map.add(newVersion);

                    return next_day_map;
                } else {
                    ArrayList<String> next_day_map = new ArrayList<>();
                    next_day_map.add("False");
                    next_day_map.add("null");
                    next_day_map.add("null");
                    next_day_map.add("null");
                    return next_day_map;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayList<String> next_day_map = new ArrayList<>();
            next_day_map.add("False");
            next_day_map.add("null");
            next_day_map.add("null");
            next_day_map.add("null");
            return next_day_map;
        }

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            return GetVersion();
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            super.onPostExecute(result);
            aboutNewVersion = new ArrayList<>(result);

        }
    }
}
