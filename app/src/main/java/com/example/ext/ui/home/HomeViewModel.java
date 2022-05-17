package com.example.ext.ui.home;

import static com.example.ext.ConfigApiResponses.HOME_WORK;
import static com.example.ext.ConfigApiResponses.MARKS;
import static com.example.ext.ui.diary.DiaryViewModel.deserialize;

import android.os.Build;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ext.MainActivity;
import com.example.ext.api.JSON;
import com.example.ext.ui.home_work.HomeWorkViewModel;
import com.example.ext.ui.note.NoteViewModel;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeViewModel extends ViewModel {
    private String dairyData;
    private Map<String, ArrayList<String>> jsondairy = new HashMap<>();
    private MutableLiveData<Map<String, ArrayList<String>>> mMapN;
    private MutableLiveData<Map<String, ArrayList>> mMapD;
    private MutableLiveData<Map<String, ArrayList<String>>> mMapHW;
    private final Handler HandlerCheckAllAccess = new Handler();
    private boolean flag = true;

    private final Runnable CheckAllAccess = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            if (flag){
            if (HOME_WORK == null && HomeWorkViewModel.thread == null){
                HomeWorkViewModel.getStartHomeWorkAsync();
            } else if (MARKS == null && NoteViewModel.thread == null){
                NoteViewModel.getStartNoteAsync();
            } else if (MARKS != null && HOME_WORK != null){
                String next_day = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE", new Locale("ru"));
                Date d = new Date();
                String dayOfTheWeek = sdf.format(d).substring(0, 1).toUpperCase() + sdf.format(d).substring(1);

                List<String> keys = new ArrayList<>(HOME_WORK.keySet());
                List<LocalDate> keys_format_date = new ArrayList<>();
                for (int i = 0; i < keys.size(); i++) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        keys_format_date.add(LocalDate.parse(keys.get(i), DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    }
                }
                Collections.sort(keys_format_date);
                keys = new ArrayList<String>();
                for (int i = 0; i < keys_format_date.size(); i++) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        keys.add(keys_format_date.get(i).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    }
                }
                Map<String, ArrayList<String>> next_day_map = new HashMap<>();
                Map<String, ArrayList> curr_day_map = new HashMap<>();
                Map<String, ArrayList<String>> last_notes_map = new HashMap<>();

                if (keys.contains(next_day)){
                    ArrayList<ArrayList<String>> array = HOME_WORK.get(next_day);
                    for (int j = 0; j < array.size(); j++) {
                        next_day_map.put(array.get(j).get(0), array.get(j));
                    }
                }
                mMapHW.setValue(next_day_map);

                for (String subj: MARKS.keySet()){
                    ArrayList<ArrayList<String>> array = MARKS.get(subj);
                    for (int j = 0; j < array.size(); j++) {
                        LocalDate mark_date = LocalDate.parse(array.get(j).get(0), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                        LocalDate end_dat = LocalDate.now();
                        LocalDate begin_dat = end_dat.minusDays(3);
                        if ((mark_date.isAfter(begin_dat) || mark_date.isEqual(begin_dat)) && (mark_date.isBefore(end_dat)) || mark_date.isEqual(end_dat)){
                            last_notes_map.put(array.get(j).get(0), array.get(j));
                        }
                    }
                }
                mMapN.setValue(last_notes_map);

                dairyData = MainActivity.data;
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
                curr_day_map.put(dayOfTheWeek, jsondairy.get(dayOfTheWeek));
                ArrayList<String> days = new ArrayList<>();
                days.add("Понедельник");days.add("Вторник");days.add("Среда");days.add("Четверг");
                days.add("Пятница");days.add("Суббота");days.add("Воскресенье");
                String nextDayOfWeek;
                if (dayOfTheWeek.equals("Воскресенье")){
                    nextDayOfWeek = days.get(0);
                } else{
                    nextDayOfWeek = days.get(days.indexOf(dayOfTheWeek)+1);
                }
                curr_day_map.put(nextDayOfWeek, jsondairy.get(nextDayOfWeek));

                mMapD.setValue(curr_day_map);
                flag = false;
            }
            HandlerCheckAllAccess.postDelayed(this, 5000);
        }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    public HomeViewModel() {
        mMapHW = new MutableLiveData<>();
        mMapN = new MutableLiveData<>();
        mMapD = new MutableLiveData<>();
        if (mMapHW.getValue() == null || mMapN.getValue() == null){
            HandlerCheckAllAccess.post(CheckAllAccess);
        }
    }
    public LiveData<Map<String, ArrayList>> getMapOfDiary() {
        return mMapD;
    }

    public LiveData<Map<String, ArrayList<String>>> getMapOfNotes() {
        return mMapN;
    }

    public LiveData<Map<String, ArrayList<String>>> getMapOfHW() {
        return mMapHW;
    }
}