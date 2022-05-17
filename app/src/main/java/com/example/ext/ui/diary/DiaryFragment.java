package com.example.ext.ui.diary;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ext.R;
import com.example.ext.databinding.FragmentDiaryBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;


public class DiaryFragment extends Fragment {
    public static String data;
    private FragmentDiaryBinding binding;
    private String nameOfLesson;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        data = preferences.getString("dairyData", "");

        DiaryViewModel notificationsViewModel =
                new ViewModelProvider(this).get(DiaryViewModel.class);

        binding = FragmentDiaryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Drawable p = getResources().getDrawable(R.drawable.plus);
        Bitmap bitmap = ((BitmapDrawable) p).getBitmap();
        Drawable plus = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 20, 20, true));

        Drawable t = getResources().getDrawable(R.drawable.ic_delete_message);
        Bitmap bitmap1 = ((BitmapDrawable) t).getBitmap();
        Drawable trash = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap1, 20, 20, true));

        Drawable c = getResources().getDrawable(R.drawable.ic_chek);
        Bitmap bitmap2 = ((BitmapDrawable) c).getBitmap();
        Drawable checkmark = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap2, 20, 20, true));

        ArrayList<String> days = new ArrayList<>();
        days.add("Понедельник");days.add("Вторник");days.add("Среда");days.add("Четверг");
        days.add("Пятница");days.add("Суббота");days.add("Воскресенье");

        final TableLayout tblOtherContent = binding.tblOtherContent;
        ScrollView scrollView = new ScrollView(getContext());
        scrollView.setVerticalScrollBarEnabled(false);

        TableLayout tableLayout = new TableLayout(getContext());
        tableLayout.setElevation(5);
        tableLayout.setBackgroundDrawable(ContextCompat.getDrawable(container.getContext(), R.drawable.less_rounded_corners_transp));
        TableLayout.LayoutParams tlLayoutParams = new TableLayout.LayoutParams();
        tlLayoutParams.setMargins(20,20,20,20);

        scrollView.addView(tableLayout, tlLayoutParams);
        tblOtherContent.addView(scrollView);
        Map<String, ArrayList<String>> jsondairy = notificationsViewModel.getDiaryMap().getValue();


        for (int i = 0; i < jsondairy.size(); i++) {
            TableLayout tbl = new TableLayout(getContext());
            tbl.setGravity(Gravity.CENTER);

            TableLayout tblForNewLesson = new TableLayout(getContext());

            TableRow tr_of_day_of_week = new TableRow(getContext());
            TextView day_of_week = new TextView(getContext());
            //day_of_week.setText(days.get(i) + " " + jsondairy.get(days.get(i)));
            day_of_week.setText(days.get(i));
            day_of_week.setPadding(10,5,0,0);
            day_of_week.setTextSize(20);
            day_of_week.setTextColor(Color.WHITE);
            tr_of_day_of_week.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.avatar_color_blue));

            tbl.addView(tr_of_day_of_week, tlLayoutParams);


            ImageView btn = new ImageView(getContext());
            btn.setId(i);
            btn.setImageDrawable(plus);
            day_of_week.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 90, 60f));
            tr_of_day_of_week.addView(day_of_week);
            tr_of_day_of_week.addView(btn);

            btn.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("WrongConstant")
                public void onClick(View view) {
                    int last = jsondairy.get(days.get(btn.getId())).size();
                    if (last <= 8){
                        TableRow new_lesson = new TableRow(getContext());
                        new_lesson.setElevation(10);
                        new_lesson.setGravity(Gravity.CENTER_VERTICAL);
                        ImageView btn_of_edit = new ImageView(getContext());
                        ImageView btn_of_delete = new ImageView(getContext());
                        nameOfLesson = last + ". ОКНО";

                        EditText ll = new EditText(getContext());
                        ll.setText(nameOfLesson);

                        jsondairy.get(days.get(btn.getId())).add("" + ll.getText());
                        ll.setId(last);
                        new_lesson.addView(ll);

                        ll.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                btn_of_edit.setVisibility(View.VISIBLE);
                                btn_of_delete.setVisibility(View.GONE);

                            }
                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        });

                        //day_of_week.setText(days.get(btn.getId()) + " " + jsondairy.get(days.get(btn.getId())));

                        btn_of_delete.setId(last);
                        btn_of_delete.setImageDrawable(trash);
                        new_lesson.addView(btn_of_delete);
                        new_lesson.setId(last);
                        btn_of_delete.setOnClickListener(new View.OnClickListener() {
                            @SuppressLint("ResourceType")
                            public void onClick(View view) {
                                ((ViewGroup) new_lesson.getParent()).removeView(new_lesson);
                                jsondairy.get(days.get(btn.getId())).remove(btn_of_delete.getId());
                                if (btn_of_delete.getId() != jsondairy.get(days.get(btn.getId())).size()) {
                                    for (View i : getAllChildren(tblForNewLesson)) {
                                        if (i.getId() > 0) {
                                            if (i instanceof ImageView) {
                                                i.setId(i.getId()-1);
                                            }
                                            if (i instanceof EditText) {
                                                i.setId(i.getId()-1);
                                                int lenght = ((EditText) i).getText().length();
                                                binding.fab.setVisibility(View.VISIBLE);
                                                ((EditText) i).setText(i.getId() +"." +((EditText) i).getText().subSequence(2, lenght));
                                            }
                                        }
                                    }
                                }
                                //day_of_week.setText(days.get(btn.getId()) + " " + jsondairy.get(days.get(btn.getId())));
                            }
                        });

                        btn_of_edit.setId(last);
                        btn_of_edit.setImageDrawable(checkmark);
                        new_lesson.addView(btn_of_edit);
                        new_lesson.setId(last);
                        btn_of_edit.setVisibility(View.GONE);
                        btn_of_edit.setOnClickListener(new View.OnClickListener() {
                            @SuppressLint("ResourceType")
                            public void onClick(View view) {
                                jsondairy.get(days.get(btn.getId())).set(btn_of_edit.getId(), "" + ll.getText());
                                nameOfLesson = String.valueOf(ll.getText());
                                btn_of_edit.setVisibility(View.GONE);
                                btn_of_delete.setVisibility(View.VISIBLE);

                                //day_of_week.setText(days.get(btn.getId()) + " " + jsondairy.get(days.get(btn.getId())));
                            }
                        });
                        binding.fab.setVisibility(View.VISIBLE);
                        tblForNewLesson.addView(new_lesson);

                        tblForNewLesson.setBackgroundDrawable(ContextCompat.getDrawable(container.getContext(), R.drawable.less_rounded_corners));
                    } else{
                        Toast.makeText(view.getContext(),
                                "Многовато уроков", Toast.LENGTH_SHORT)
                                .show();
                    }

                }
            });

            for (int l = 0; l < jsondairy.get(days.get(i)).size(); l++) {
                TableRow new_lesson = new TableRow(getContext());
                new_lesson.setElevation(10);
                ImageView btn_of_edit = new ImageView(getContext());
                ImageView btn_of_delete = new ImageView(getContext());
                new_lesson.setGravity(Gravity.CENTER_VERTICAL);
                String subject = jsondairy.get(days.get(btn.getId())).get(l);
                EditText ll = new EditText(getContext());
                ll.setText(subject);
                new_lesson.addView(ll);
                ll.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        btn_of_edit.setVisibility(View.VISIBLE);
                        btn_of_delete.setVisibility(View.GONE);
                        binding.fab.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                //day_of_week.setText(days.get(btn.getId()) + " " + jsondairy.get(days.get(btn.getId())));
                btn_of_delete.setId(l);
                btn_of_delete.setImageDrawable(trash);
                new_lesson.addView(btn_of_delete);
                new_lesson.setId(l);
                btn_of_delete.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ResourceType")
                    public void onClick(View view) {
                        binding.fab.setVisibility(View.VISIBLE);
                        ((ViewGroup) new_lesson.getParent()).removeView(new_lesson);
                        jsondairy.get(days.get(btn.getId())).remove(subject);
                        if (btn_of_delete.getId() != jsondairy.get(days.get(btn.getId())).size()) {
                            for (View i : getAllChildren(tblForNewLesson)) {
                                if (i.getId() > 0) {
                                    if (i instanceof ImageView) {
                                        i.setId(i.getId()-1);
                                        binding.fab.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                        //day_of_week.setText(days.get(btn.getId()) + " " + jsondairy.get(days.get(btn.getId())));
                    }
                });

                btn_of_edit.setId(l);
                btn_of_edit.setImageDrawable(checkmark);
                new_lesson.addView(btn_of_edit);
                new_lesson.setId(l);
                btn_of_edit.setVisibility(View.GONE);
                btn_of_edit.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        binding.fab.setVisibility(View.VISIBLE);
                        jsondairy.get(days.get(btn.getId())).set(btn_of_edit.getId(), "" + ll.getText());
                        btn_of_edit.setVisibility(View.GONE);
                        btn_of_delete.setVisibility(View.VISIBLE);
                        //day_of_week.setText(days.get(btn.getId()) + " " + jsondairy.get(days.get(btn.getId())));
                    }
                });
                binding.fab.setVisibility(View.VISIBLE);
                tblForNewLesson.setElevation(10);
                tblForNewLesson.setBackgroundDrawable(ContextCompat.getDrawable(container.getContext(), R.drawable.less_rounded_corners));
                tblForNewLesson.addView(new_lesson);
            }

            tbl.addView(tblForNewLesson, tlLayoutParams);
            tableLayout.addView(tbl);
        }
        binding.fab.setVisibility(View.GONE);
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext()).edit()
                            .putString("dairyData", serialize(jsondairy).toString())
                            .apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                binding.fab.setVisibility(View.GONE);
            }
        });
        return root;
    }
    private ArrayList<View> getAllChildren(View v) {

        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<View>();

        ViewGroup viewGroup = (ViewGroup) v;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {

            View child = viewGroup.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            viewArrayList.addAll(getAllChildren(child));

            result.addAll(viewArrayList);
        }

        return result;

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