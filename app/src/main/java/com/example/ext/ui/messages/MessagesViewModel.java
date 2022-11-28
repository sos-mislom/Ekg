package com.example.ext.ui.messages;

import static com.example.ext.ConfigApiResponses.MESSAGES;
import static com.example.ext.ConfigApiResponses.SUBJECT_NAMES;
import static com.example.ext.ConfigApiResponses.TEACHERS;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ext.api.Ext;
import com.example.ext.helper.PreferencesUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class MessagesViewModel extends ViewModel {
    private static MutableLiveData<Map<String, ArrayList>> mMapMessages;
    private static AsyncTask<Void, Void, Map<String, ArrayList>> MessagesThread;

    public MessagesViewModel() {
        mMapMessages = new MutableLiveData<>();
    }
    public static void getStartMessageAsync(){
        MessagesThread = new asyncMessages().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static class asyncMessages extends AsyncTask<Void, Void, Map<String, ArrayList>> {
        @RequiresApi(api = Build.VERSION_CODES.N)
        private Map<String, ArrayList> GetContentForMessages() {
            try {
                Ext ext = new Ext(PreferencesUtil.username, PreferencesUtil.password);
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

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Map<String, ArrayList> doInBackground(Void... params) {

            return GetContentForMessages();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("ResourceAsColor")
        @Override
        protected void onPostExecute(Map<String, ArrayList> result) {
            super.onPostExecute(result);
            if (result == null) {
                MessagesThread.cancel(true);
                getStartMessageAsync();
            }else {
                mMapMessages.setValue(result);
                MESSAGES = new HashMap<>(result);
            }
        }
    }

    public static boolean IfmMapMessagesNotNull(){
        return mMapMessages != null && mMapMessages.getValue() != null;
    }

}



