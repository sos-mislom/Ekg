package com.example.ext.ui;

import static com.example.ext.ui.diary.DiaryViewModel.getDiaryMap;
import static com.example.ext.ui.diary.ListOfDiaryFragment.serialize;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ext.R;
import com.example.ext.helper.ItemTouchHelperAdapter;
import com.example.ext.ui.diary.DiaryFragment;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> implements ItemTouchHelperAdapter {

    private List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private String mKey;

    public Adapter(Context context, List<String> data, String key) {
        this.mKey = key;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.fragment_subject_diary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String subj = mData.get(position);
        holder.myTextView.setText(subj);
        holder.myTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mData.set(position, String.valueOf(holder.myTextView.getText()));
                UpdatePreferences();
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    public void addItem(String newItem) {
        if (mData.size() < 8){
            mData.add(newItem);
            notifyItemRemoved(mData.size());
            UpdatePreferences();
        } else {
            Toast.makeText(mInflater.getContext(), "Ну неее, чет перегнули Вы палку", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mData, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mData, i, i - 1);
            }
        }
        UpdatePreferences();
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        mData.remove(position);
        UpdatePreferences();
        notifyItemRemoved(position);

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        EditText myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.subject_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public String getItem(int id) {
        return (String) mData.get(id);
    }

    public void setClickListener(DiaryFragment itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    private void UpdatePreferences(){
        Map<String, ArrayList<String>> jsondiary = getDiaryMap().getValue();
        assert jsondiary != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jsondiary.replace(mKey, (ArrayList<String>) mData);
        }
        try {
            PreferenceManager.getDefaultSharedPreferences(mInflater.getContext().getApplicationContext()).edit()
                    .putString("dairyData", serialize(jsondiary).toString())
                    .apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}