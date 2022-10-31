package com.example.ext.ui.diary;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.ext.R;
import com.example.ext.databinding.FragmentDiaryBinding;
import com.example.ext.ui.ObjectOfDiary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;


public class ListOfDiaryFragment extends Fragment {
    public static String data;
    private FragmentDiaryBinding binding;


    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        data = preferences.getString("dairyData", "");
        //preferences.edit().remove("dairyData").commit();
        DiaryViewModel notificationsViewModel =
                new ViewModelProvider(this).get(DiaryViewModel.class);

        Map<String, ArrayList<String>> jsonDiary = notificationsViewModel.getDiaryMap().getValue();

        binding = FragmentDiaryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        for (String key: jsonDiary.keySet()) {
            Bundle bundle = new Bundle();
            ObjectOfDiary obj = new ObjectOfDiary(key, jsonDiary.get(key));
            bundle.putParcelable("diary_key", obj);

            DiaryFragment youFragment = new DiaryFragment();
            youFragment.setArguments(bundle);

            FragmentManager fragmentManager = getChildFragmentManager();

            fragmentManager.beginTransaction()
                    .add(R.id.tbl_of_day, youFragment)
                    .addToBackStack("myStack2")
                    .commit();
        }

        return root;
    }
    public static JSONObject serialize(Map<String, ArrayList<String>> map) throws JSONException {
        JSONObject json = new JSONObject();
        for(Map.Entry<String, ArrayList<String> > entry : map.entrySet()) {
            json.put(entry.getKey(), new JSONArray(entry.getValue()));
        }
        return json;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}