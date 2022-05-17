package com.example.ext.ui.messages;

import static com.example.ext.ConfigApiResponses.MESSAGES;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class MessagesViewModel extends ViewModel {
    private final MutableLiveData<Map<String, ArrayList>> mMapMessages;

    public MessagesViewModel() {
        mMapMessages = new MutableLiveData<>();
        new asyncEXT().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    private class asyncEXT extends AsyncTask<Void, Void, Map<String, ArrayList>> {
        @RequiresApi(api = Build.VERSION_CODES.N)
        private Map<String, ArrayList> GetContentForMessages() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    Ext ext = MainActivity.getExt();

                    JSONArray messageData = ext.GET_MESSAGES();
                    Map<String, ArrayList> messages = new TreeMap();
                    for (int k = 0; k < messageData.length(); k++) {
                        String Date = ext.GET_DATE(messageData.getJSONArray(k).getJSONArray(4));
                        String teacher = ext.teachers.get(messageData.getJSONArray(k).getInt(1));
                        if (messages.get(teacher) == null) {
                            messages.put(teacher, new ArrayList<>());
                        }
                        ArrayList<String> content = new ArrayList<>();
                        content.add(Date);                                                             // 0 date
                        content.add(messageData.getJSONArray(k).getString(3));                  // 1 message
                        content.add(ext.sbj_names.get(messageData.getJSONArray(k).getInt(6))); // 2 subject name

                        Objects.requireNonNull(messages.get(teacher)).add(content);
                    }
                    return messages;
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
                return GetContentForMessages();
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
            if (result != null) {
                mMapMessages.setValue(result);
                MESSAGES = result;
            } else{
                ArrayList arr = new ArrayList<>();
                Map<String, ArrayList> map3 = new HashMap<>();
                for (int i = 1; i < 5; i++) { arr.add(i+""); }
                ArrayList arr2 = new ArrayList<>();
                arr2.add(arr);arr2.add(arr);arr2.add(arr);arr2.add(arr);
                map3.put("Русский язык", arr2);
                mMapMessages.setValue(map3);
            }
        }
    }

    public LiveData<Map<String, ArrayList>> getMapMessages() {
        return mMapMessages;
    }
}