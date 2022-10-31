package com.example.ext.ui.diary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ext.R;
import com.example.ext.SimpleItemTouchHelperCallback;
import com.example.ext.ui.Adapter;
import com.example.ext.ui.ObjectOfDiary;

import java.util.List;

public class DiaryFragment extends Fragment implements Adapter.ItemClickListener{
    Adapter adapter;

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dairy_day, container, false);
        if(getArguments() != null) {

            ObjectOfDiary obj = getArguments().getParcelable("diary_key");
            List<String> data = obj.paramToo;
            String key = obj.paramOne;

            RecyclerView recyclerView = view.findViewById(R.id.recycler);

            TextView DayOfWeek = view.findViewById(R.id.day_of_week);
            DayOfWeek.setText(key);

            ImageView add_more_lessons = view.findViewById(R.id.add_more_lesson);
            add_more_lessons.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.addItem("Окно");
                }
            });

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new Adapter(getContext(), data, key);
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);
            ItemTouchHelper.Callback callback =
                    new SimpleItemTouchHelperCallback(adapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(recyclerView);

        }
        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(
                getContext(),
                "Ткнули на " + adapter.getItem(position) + " в строке " + position,
                Toast.LENGTH_SHORT
        ).show();

    }
}