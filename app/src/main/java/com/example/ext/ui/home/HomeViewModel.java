package com.example.ext.ui.home;

import static com.example.ext.ConfigApiResponses.HOME_WORK;
import static com.example.ext.ConfigApiResponses.MARKS;

import android.os.Build;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ext.ui.home_work.HomeWorkViewModel;
import com.example.ext.ui.note.NoteViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<Map<String, ArrayList<String>>> mMapN;
    private MutableLiveData<Map<String, ArrayList<String>>> mMapHW;
    private final Handler HandlerCheckAllAccess = new Handler();
    private boolean flag = true;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public HomeViewModel() {
        mMapHW = new MutableLiveData<>();
        mMapN = new MutableLiveData<>();
        if (mMapHW.getValue() == null || mMapN.getValue() == null){
            Runnable checkAllAccess = new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {
                    if (flag) {
                        if (HOME_WORK == null && HomeWorkViewModel.thread == null) {
                            HomeWorkViewModel.getStartHomeWorkAsync();
                        }
                        if (MARKS == null && NoteViewModel.thread == null) {
                            NoteViewModel.getStartNoteAsync();
                        }
                        if (HOME_WORK != null) {
                            String next_day = LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
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

                            if (keys.contains(next_day)) {
                                ArrayList<ArrayList<String>> array = HOME_WORK.get(next_day);
                                for (int j = 0; j < array.size(); j++) {
                                    next_day_map.put(array.get(j).get(0), array.get(j));
                                }
                            }
                            mMapHW.setValue(next_day_map);
                        }
                        if (MARKS != null) {
                            Map<String, ArrayList<String>> last_notes_map = new HashMap<>();
                            for (String subj : MARKS.keySet()) {
                                ArrayList<ArrayList<String>> array = MARKS.get(subj);
                                for (int j = 0; j < array.size(); j++) {
                                    LocalDate mark_date = LocalDate.parse(array.get(j).get(0), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                                    LocalDate end_dat = LocalDate.now();
                                    LocalDate begin_dat = end_dat.minusDays(3);
                                    if ((mark_date.isAfter(begin_dat) || mark_date.isEqual(begin_dat)) && (mark_date.isBefore(end_dat)) || mark_date.isEqual(end_dat)) {
                                        last_notes_map.put(array.get(j).get(0), array.get(j));
                                    }
                                }
                            }
                            mMapN.setValue(last_notes_map);
                        }

                        if (MARKS != null && HOME_WORK != null) {
                            flag = false;
                        }
                        HandlerCheckAllAccess.postDelayed(this, 5000);
                    }
                }
            };
            HandlerCheckAllAccess.post(checkAllAccess);
        }
    }


    public LiveData<Map<String, ArrayList<String>>> getMapOfNotes() {
        return mMapN;
    }

    public LiveData<Map<String, ArrayList<String>>> getMapOfHW() {
        return mMapHW;
    }
}