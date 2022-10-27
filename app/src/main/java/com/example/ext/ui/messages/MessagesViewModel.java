package com.example.ext.ui.messages;

import static com.example.ext.ConfigApiResponses.MESSAGES;
import static com.example.ext.ConfigApiResponses.SUBJECT_NAMES;
import static com.example.ext.ConfigApiResponses.TEACHERS;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ext.MainActivity;
import com.example.ext.api.Ext;
import com.example.ext.databinding.FragmentMessagesBinding;
import com.example.ext.ui.note.NoteViewModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class MessagesViewModel extends ViewModel {
    private static MutableLiveData<Map<String, ArrayList>> mMapMessages;
    public static AsyncTask<Void, Void, Map<String, ArrayList>> thread;


    public MessagesViewModel() {
        mMapMessages = new MutableLiveData<>();
        new asyncEXT().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    public static void getStartMessageAsync(){
        thread = new NoteViewModel.asyncEXT().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    private static class asyncEXT extends AsyncTask<Void, Void, Map<String, ArrayList>> {
        @RequiresApi(api = Build.VERSION_CODES.N)
        private Map<String, ArrayList> GetContentForMessages() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    Ext ext = new Ext(MainActivity.username, MainActivity.password);

                    JSONArray messageData = ext.GET_MESSAGES();
                    Map<String, ArrayList> messages = new TreeMap();
                    for (int k = 0; k < messageData.length(); k++) {
                        String Date = ext.GET_DATE(messageData.getJSONArray(k).getJSONArray(4));
                        String teacher = TEACHERS.get(messageData.getJSONArray(k).getInt(1));
                        if (messages.get(teacher) == null) {
                            messages.put(teacher, new ArrayList<>());
                        }
                        ArrayList<String> content = new ArrayList<>();
                        content.add(Date);                                                             // 0 date
                        content.add(messageData.getJSONArray(k).getString(3));                  // 1 message
                        content.add(SUBJECT_NAMES.get(messageData.getJSONArray(k).getInt(6))); // 2 subject name

                        Objects.requireNonNull(messages.get(teacher)).add(content);
                    }
                    return messages;
                } catch (JSONException | NoSuchAlgorithmException e) {
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
            if (result == null) {
                thread.cancel(true);
                getStartMessageAsync();
            }else {
                mMapMessages.setValue(result);
                Map<String, ArrayList> targetMap = new ConcurrentHashMap<>(result);
                MESSAGES = targetMap;
            }
        }
    }
    public static boolean IfmMapMessagesNotNull(){
        return mMapMessages != null && mMapMessages.getValue() != null;
    }
    public LiveData<Map<String, ArrayList>> getMapMessages() {
        return mMapMessages;
    }
}



