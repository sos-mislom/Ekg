package com.example.ext.ui.home_work;

import static com.example.ext.ConfigApiResponses.HOME_WORK;
import static com.example.ext.ConfigApiResponses.SUBJECT_NAMES;
import static com.example.ext.ConfigApiResponses.TEACHERS;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ext.MainActivity;
import com.example.ext.api.Ext;

import org.json.JSONArray;
import org.json.JSONException;

import java.security.NoSuchAlgorithmException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class HomeWorkViewModel extends ViewModel {
    public static LocalDate begin_dt;
    private static LocalDate end_dt;
    private static MutableLiveData<Map<String, ArrayList>> mMapHW = new MutableLiveData<>();
    public static AsyncTask<Void, Void, Map<String, ArrayList>> thread;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public HomeWorkViewModel() {
        begin_dt = LocalDate.now().with(DayOfWeek.MONDAY);
        mMapHW = new MutableLiveData<>();
        getStartHomeWorkAsync(begin_dt);
    }
    public static boolean IfmMapHWNotNull(){
        return mMapHW != null && mMapHW.getValue() != null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void getStartHomeWorkAsync(){
        begin_dt = LocalDate.now().with(DayOfWeek.MONDAY);
        thread = new asyncEXT().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void getStartHomeWorkAsync(LocalDate bd){
        begin_dt = bd;
        thread = new asyncEXT().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static class asyncEXT extends AsyncTask<Void, Void, Map<String, ArrayList>> {
        @RequiresApi(api = Build.VERSION_CODES.N)
        private Map<String, ArrayList> GetContentForActivityMain() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                end_dt = begin_dt.plusDays(5);
                try {
                    //Ext ext = new Ext("Зайцева","<Cb0@4F9Sx");
                    //Ext ext = new Ext("Зайцев","3MA8|ZJQ{0");
                    //Ext ext = new Ext("Кудряшов","Ob7]NDz79+");
                    Ext ext = new Ext(MainActivity.username, MainActivity.password);
                    JSONArray studentGroups = ext.GET_STUDENT_GROUPS();
                    JSONArray dairyData = ext.GET_STUDENT_DAIRY(begin_dt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), end_dt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    Map<String, ArrayList> whereIsLesson = new HashMap<>();
                    boolean curInGroup;
                    for (int k = 0; k < dairyData.length(); k++) {
                        curInGroup = true;

                        String day = String.valueOf(dairyData.getJSONArray(k).getJSONArray(0).get(2));
                        String month = String.valueOf((Integer.parseInt(String.valueOf(dairyData.getJSONArray(k).getJSONArray(0).get(1)))+1));
                        if (day.length() == 1){ day = "0"+ day; }
                        if (month.length() == 1){ month = "0"+ month; }

                        String Date = day + "." + month + "." + dairyData.getJSONArray(k).getJSONArray(0).get(0);
                        for (int n = 0; n < studentGroups.length(); n++) {
                            if (studentGroups.getJSONArray(n).get(1).equals(dairyData.getJSONArray(k).get(2)) && studentGroups.getJSONArray(n).get(2).equals(dairyData.getJSONArray(k).get(6))){
                                curInGroup = false;
                                break;
                            }
                        }
                        if (curInGroup || dairyData.getJSONArray(k).getInt(10) == 1){
                            String mark = dairyData.getJSONArray(k).getString(5);
                            if (dairyData.getJSONArray(k).get(7).toString().equals("null")){
                                if (whereIsLesson.get(Date) == null){
                                    whereIsLesson.put(Date, new ArrayList<>());
                                }
                                ArrayList<String> content = new ArrayList<>();
                                content.add(SUBJECT_NAMES.get(dairyData.getJSONArray(k).getInt(2))); // 0 subject name
                                content.add(TEACHERS.get(dairyData.getJSONArray(k).getInt(9))); // 1 teacher's name
                                String hm = "";
                                if (!dairyData.getJSONArray(k).getString(3).equals("")){hm+=dairyData.getJSONArray(k).getString(3);}
                                if (!dairyData.getJSONArray(k).getString(4).equals("")){hm+=" / " +dairyData.getJSONArray(k).getString(4);}
                                content.add(hm); // 2 home work
                                content.add(mark); // 3 mark
                                Objects.requireNonNull(whereIsLesson.get(Date)).add(content);
                            }
                        }

                    }
                    return whereIsLesson;
                } catch (JSONException | NoSuchAlgorithmException e) {
                    e.printStackTrace(); }
                return null;
            }
            return null;
        }
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Map<String, ArrayList> doInBackground(Void... params) {
            try {
                return GetContentForActivityMain();
            } catch (NullPointerException e) {
                //MainActivity.showToast("[!WARNING!] "+ e.toString());
                Log.e("WARNING!!!!!", e.toString());
                return null;
            }
        }
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("ResourceAsColor")
        @Override
        protected void onPostExecute(Map<String, ArrayList> result) {
            super.onPostExecute(result);
            if (result == null) {
                thread.cancel(true);
                getStartHomeWorkAsync(begin_dt);
            } else{
                mMapHW.setValue(result);
                Map<String, ArrayList> targetMap = new ConcurrentHashMap<>(result);
                HOME_WORK = targetMap;
            }
        }
    }

}