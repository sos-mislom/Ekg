package com.example.ext.ui.diary;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ext.SimpleItemTouchHelperCallback;
import com.example.ext.databinding.FragmentDiaryBinding;
import com.example.ext.ui.Adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;


public class DiaryFragment extends Fragment implements Adapter.ItemClickListener{
    public static String data;
    private FragmentDiaryBinding binding;
    private String nameOfLesson;
    Adapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        data = preferences.getString("dairyData", "");


        DiaryViewModel notificationsViewModel =
                new ViewModelProvider(this).get(DiaryViewModel.class);
        Map<String, ArrayList<String>> jsondairy = notificationsViewModel.getDiaryMap().getValue();


        binding = FragmentDiaryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
// data to populate the RecyclerView with
        ArrayList<String> animalNames = new ArrayList<>();
        animalNames.add("Русский");
        animalNames.add("Матеша");
        animalNames.add("Французский");
        animalNames.add("Иностранный");
        animalNames.add("Литература");

        // set up the RecyclerView
        RecyclerView recyclerView = binding.recycler;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new Adapter(getContext(), animalNames);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);


//        try {
//            PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext()).edit()
//                    .putString("dairyData", serialize(jsondairy).toString())
//                    .apply();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
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

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(getContext(), "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }
}