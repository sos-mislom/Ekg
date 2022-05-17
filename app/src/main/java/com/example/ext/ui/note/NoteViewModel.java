package com.example.ext.ui.note;

import static com.example.ext.ConfigApiResponses.MARKS;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ext.MainActivity;
import com.example.ext.api.Ext;

import org.json.JSONArray;
import org.json.JSONException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import kotlin.Pair;


public class NoteViewModel extends ViewModel {

    private static MutableLiveData<Map<String, ArrayList>> mMapN;
    public static AsyncTask<Void, Void, Map<String, ArrayList>> thread;

    public NoteViewModel() {
        mMapN = new MutableLiveData<>();
        new asyncEXT().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public LiveData<Map<String, ArrayList>> getNoteMap() {
        return mMapN;
    }

    public static void getStartNoteAsync(){
        thread = new asyncEXT().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public static class asyncEXT extends AsyncTask<Void, Void, Map<String, ArrayList>> {
        @RequiresApi(api = Build.VERSION_CODES.N)
        private Map<String, ArrayList> GetContentForNotes() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    Ext ext = MainActivity.getExt();
                    Pair<LocalDate, LocalDate> dt = ext.GET_INTERVAL(false);
                    JSONArray list_of_weights = ext.GET_STUDENT_LESSONS(dt.component1().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), dt.component2().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    Log.e("list_of_weights", list_of_weights.toString());

                    Map<Integer, ArrayList> id_of_all_notes = new TreeMap();
                    Double weight = 1.0;
                    String type_of_mark="";
                    String theme="";
                    String home_work="";

                    for (int i = 0; i < list_of_weights.length(); i++) {
                        if (!list_of_weights.getJSONArray(i).get(9).equals(null)) {
                            weight = list_of_weights.getJSONArray(i).getDouble(9);
                        }
                        if (!list_of_weights.getJSONArray(i).get(3).equals(null) && weight == 1.0) {
                            weight = list_of_weights.getJSONArray(i).getDouble(3);
                        }
                        type_of_mark = list_of_weights.getJSONArray(i).getString(1);
                        if (type_of_mark.equals("null")){type_of_mark="Задание";}
                        theme = list_of_weights.getJSONArray(i).getString(4);
                        home_work =list_of_weights.getJSONArray(i).getString(8);
                        id_of_all_notes.put(list_of_weights.getJSONArray(i).getInt(0), new ArrayList<>(Arrays.asList(type_of_mark, theme, home_work, weight.toString())));
                    }

                    Pair<LocalDate, LocalDate> intervals = ext.GET_INTERVAL(true);
                    JSONArray journalData = ext.GET_STUDENT_JOURNAL_DATA();
                    Map<String, ArrayList> notes = new TreeMap();
                    for (int k = 0; k < journalData.length(); k++) {
                        weight = 1.0;
                        int id = journalData.getJSONArray(k).getInt(0);

                        if (id_of_all_notes.containsKey(id)){
                            type_of_mark = (String) id_of_all_notes.get(id).get(0);
                            theme = (String) id_of_all_notes.get(id).get(1);
                            home_work = (String)  id_of_all_notes.get(id).get(2);
                            weight = Double.parseDouble((String) id_of_all_notes.get(id).get(3));
                        }
                        String Date = Ext.GET_DATE(journalData.getJSONArray(k).getJSONArray(3));
                        String Subj = Ext.sbj_names.get(journalData.getJSONArray(k).getInt(4));
                        if (notes.get(Subj) == null) {
                            notes.put(Subj, new ArrayList<>());
                        } else {
                            LocalDate date = LocalDate.parse(Date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                            if ((date.isAfter(intervals.component1()) || date.isEqual(intervals.component1())) && (date.isBefore(intervals.component2())) || date.isEqual(intervals.component2())) {
                                ArrayList<String> content = new ArrayList<>();
                                content.add(Date);
                                content.add(journalData.getJSONArray(k).getString(8));
                                content.add(journalData.getJSONArray(k).getString(2));
                                content.add(String.valueOf(weight));
                                content.add(type_of_mark);
                                content.add(theme);
                                content.add(home_work);
                                Objects.requireNonNull(notes.get(Subj)).add(content);
                            }
                        }
                    }
                    return notes;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Map<String, ArrayList> doInBackground(Void... params) {
            try {
                return GetContentForNotes();
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
                getStartNoteAsync();
            } else {
               // mMapN.setValue(result);
                Map<String, ArrayList> targetMap = new ConcurrentHashMap<>(result);
                MARKS = targetMap;
            }
        }
    }
}