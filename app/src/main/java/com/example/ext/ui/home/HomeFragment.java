package com.example.ext.ui.home;

import static com.example.ext.ConfigApiResponses.listOfIntervals;
import static com.example.ext.ui.diary.DiaryViewModel.deserialize;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ext.MainActivity;
import com.example.ext.R;
import com.example.ext.api.JSON;
import com.example.ext.databinding.FragmentHomeBinding;
import com.example.ext.ui.dialogs.NoteInfoDialogFragment;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {
    private Typeface typefaceRoboto;
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private final Handler HandlerCheckAllAccess = new Handler();
    private boolean isHomeWorkSet;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        typefaceRoboto = Typeface.createFromAsset(getContext().getAssets(), "Roboto-Regular.ttf");
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        HandlerCheckAllAccess.post(CheckAllAccess);
        isHomeWorkSet = true;
        try {
            setUI();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return root;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setUI() throws ParseException {
        if (binding != null) {
            HorizontalScrollView horizontalScrollView = binding.scrollViewHorizontalLastNotes;
            horizontalScrollView.setHorizontalScrollBarEnabled(false);
            ScrollView scrollView = binding.scrollViewHome;
            scrollView.setVerticalScrollBarEnabled(false);
            setTableOfDiary();
        }
    }

    private final Runnable CheckAllAccess = new Runnable() {
        @Override
        public void run() {
            if (homeViewModel.getMapOfHW().getValue() != null && binding != null){
                setTableOfHomeWork();
            } else{ HandlerCheckAllAccess.postDelayed(this, 1000); }

            if (homeViewModel.getMapOfNotes().getValue() != null && binding != null){
                setTableOfNotes();
            } else{ HandlerCheckAllAccess.postDelayed(this, 1000); }
            if (homeViewModel.getMapOfNotes().getValue() != null && homeViewModel.getMapOfHW().getValue() != null){
                HandlerCheckAllAccess.removeCallbacks(CheckAllAccess);
            }
        }
    };

    private void setTableOfNotes(){
        TableRow.LayoutParams trLayoutParams = new TableRow.LayoutParams();
        trLayoutParams.setMargins(20,10,10,30);
        TableRow tableRowOfMraks = binding.tableRowOfMraks;
        Map<String, ArrayList<String>> last_notes_map =  homeViewModel.getMapOfNotes().getValue();
        for (String key : last_notes_map.keySet()) {
            TextView mark_tv = new TextView(getContext());
            mark_tv.setTextSize(40);
            mark_tv.setTextColor(Color.WHITE);
            mark_tv.setGravity(Gravity.CENTER);
            mark_tv.setMinWidth(200);
            mark_tv.setHeight(200);
            mark_tv.setTypeface(typefaceRoboto);
            mark_tv.setElevation(5f);
            mark_tv.setText(last_notes_map.get(key).get(2));
            Drawable back_gradient;
            switch (last_notes_map.get(key).get(2)) {
                case ("4"):
                    back_gradient = ContextCompat.getDrawable(getContext(), R.drawable.average_mark_background_gradient_ok);
                    break;
                case ("3"):
                    back_gradient = ContextCompat.getDrawable(getContext(), R.drawable.average_mark_background_gradient_normal);
                    break;
                case ("5"):
                    back_gradient = ContextCompat.getDrawable(getContext(), R.drawable.average_mark_background_gradient_good);
                    break;
                case ("2"):
                    back_gradient = ContextCompat.getDrawable(getContext(), R.drawable.average_mark_background_gradient_bad);
                    break;
                default:
                    back_gradient = ContextCompat.getDrawable(getContext(), R.drawable.average_mark_background_gradient_default);
                    break;
            }

            LayerDrawable finalDrawable = new LayerDrawable(new Drawable[] {back_gradient});
            if (!last_notes_map.get(key).get(1).equals("null")){
                Drawable dotIcon = ContextCompat.getDrawable(getContext(), R.drawable.oval);
                finalDrawable = new LayerDrawable(new Drawable[] {back_gradient, dotIcon});
                finalDrawable.setLayerInset(0, 0, 0, 0, 0);
                finalDrawable.setLayerInset(1,65,0,0,65);
            }
            mark_tv.setBackgroundDrawable(finalDrawable);
            mark_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NoteInfoDialogFragment newFragment = new NoteInfoDialogFragment(last_notes_map.get(key));
                    newFragment.show(getChildFragmentManager().beginTransaction(), "info");
                }
            });
            tableRowOfMraks.addView(mark_tv, trLayoutParams);
        }
        if (tableRowOfMraks.getChildCount() < 2){
            binding.lastNotesDateOfUpdate.setText("Давно не получали");
        }
    }

    @SuppressLint("ResourceAsColor")
    private void setTableOfHomeWork(){
        if (isHomeWorkSet){
            Map<String, ArrayList<String>> next_day_map = homeViewModel.getMapOfHW().getValue();
            TextView home_work_tv = binding.HomeWorkTV;
            TableLayout home_work = binding.tblHomeWork;
            if (next_day_map.keySet().size() > 0) {
                home_work_tv.setText(
                        "На завтра " +
                        getWordDeclension(next_day_map.keySet().size(), new String[]{"задан ", "задано "})
                        + next_day_map.keySet().size()
                        + getWordDeclension(next_day_map.keySet().size())
                );

                for (String key : next_day_map.keySet()) {
                    TableRow tableRow1 = new TableRow(getContext());
                    tableRow1.setGravity(Gravity.CENTER_VERTICAL);
                    String subj = next_day_map.get(key).get(0);
                    String subj_name_str;
                    if (subj.length() > 20) {
                        subj_name_str = subj.substring(0, 19) + "...";
                    } else subj_name_str = subj;
                    TextView subj_tv = new TextView(getContext());
                    subj_tv.setTextSize(29);
                    subj_tv.setPadding(25, 10, 0, 10);
                    subj_tv.setTextColor(Color.BLACK);
                    subj_tv.setText(subj_name_str);
                    ImageView imageViewArrow = new ImageView(getContext());

                    imageViewArrow.setImageResource(R.drawable.ic_arrow_drop_down);

                    TextView hw_tv = new TextView(getContext());
                    hw_tv.setVisibility(View.GONE);
                    hw_tv.setTextColor(R.color.blue_gray);
                    hw_tv.setText(next_day_map.get(key).get(2));
                    hw_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                    hw_tv.setPadding(40, 10, 40, 15);
                    hw_tv.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f));
                    tableRow1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (hw_tv.getVisibility() == View.VISIBLE) {
                                hw_tv.setVisibility(View.GONE);
                                imageViewArrow.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_arrow_drop_down));
                            } else {
                                imageViewArrow.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_arrow_drop_up));
                                hw_tv.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    tableRow1.addView(subj_tv);
                    tableRow1.addView(imageViewArrow);

                    home_work.addView(tableRow1);
                    home_work.addView(hw_tv);
                }
            }
            isHomeWorkSet = false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    private void setTableOfDiary() throws ParseException {
        String dairyData;
        Map<String, ArrayList<String>> jsondairy = new LinkedHashMap<>();

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        ArrayList<String> days = new ArrayList<>();

        days.add("Понедельник");days.add("Вторник");
        days.add("Среда");days.add("Четверг");
        days.add("Пятница");days.add("Суббота");
        days.add("Воскресенье");

        int now = LocalDate.now().getDayOfWeek().getValue();
        int tomorrow = LocalDate.now().getDayOfWeek().plus(1).getValue();
        if (now > 0) now -= 1;
        if (tomorrow > 0) tomorrow -= 1;

        Map<String, ArrayList> curr_day_map = new HashMap<>();

        dairyData = MainActivity.data;

        if (dairyData.length() > 0) {
            try {
                jsondairy = deserialize(JSON.decode(dairyData));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else{
            jsondairy.put("Понедельник", new ArrayList<>());
            jsondairy.put("Вторник", new ArrayList<>());
            jsondairy.put("Среда", new ArrayList<>());
            jsondairy.put("Четверг", new ArrayList<>());
            jsondairy.put("Пятница", new ArrayList<>());
            jsondairy.put("Суббота", new ArrayList<>());
            jsondairy.put("Воскресенье", new ArrayList<>());
        }

        String dayOfTheWeek = days.get(now);

        String nextDayOfWeek = days.get(tomorrow);
        curr_day_map.put(dayOfTheWeek, jsondairy.get(dayOfTheWeek));
        curr_day_map.put(nextDayOfWeek, jsondairy.get(nextDayOfWeek));

        TextView currentTV = binding.currentTV;
        TextView after = binding.afterTV;

        if (jsondairy.get(nextDayOfWeek) != null){
            after.setText("Завтра " + jsondairy.get(nextDayOfWeek).size()
               + getWordDeclension(jsondairy.get(nextDayOfWeek).size()));
        } else {
            after.setText("Завтра уроков не намечается");
        }
        Date date_classes_begin = formatter.parse(listOfIntervals.get(0).get(0));
        Date date_classes_end = formatter.parse(listOfIntervals.get(curr_day_map.get(dayOfTheWeek).size()-1).get(1));

        for (int i = 0; i < listOfIntervals.size(); i++) {
            if (listOfIntervals.get(i) != null){
                try {
                    ArrayList<String> arr = listOfIntervals.get(i);

                    Date date_begin = formatter.parse(arr.get(0));
                    Date date_end = formatter.parse(arr.get(1));

                    Date date_begin_next = formatter.parse(listOfIntervals.get(i+1).get(0));

                    Date date_current = formatter.parse(currentTime);

                    if (date_current.after(date_classes_begin) && date_current.before(date_classes_end)) {
                        if (date_current.after(date_begin) && date_current.before(date_end)){
                            currentTV.setText(curr_day_map.get(dayOfTheWeek).get(i) +
                                    " в " +
                                    listOfIntervals.get(i).get(1));
                        } else if (date_current.after(date_end) && date_current.after(date_begin_next)){
                            currentTV.setText("(П)" +
                                    curr_day_map.get(dayOfTheWeek).get(i+1) +
                                    " в " +
                                    listOfIntervals.get(i+1).get(0));
                        }
                    } else if (date_current.after(date_classes_end) && date_current.after(date_classes_begin)){
                            currentTV.setText("Уроки закончились в " +
                                    listOfIntervals.get(curr_day_map.get(dayOfTheWeek).size()-1).get(1));
                    } else if (date_current.before(date_classes_begin)){
                        currentTV.setText("Уроки начнутся в " +
                                listOfIntervals.get(0).get(0));
                    }

                } catch (ParseException | IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private String getWordDeclension(Integer n, String[] wordType){
        if (n >= 2 ) return wordType[1];
        else return wordType[0];
    }

    private String getWordDeclension(Integer n){
        String[] wordType = new String[]{" урок", " урока", " уроков"};
        int result = n % 100;
        if (result >=10 && result <= 20) {
            return wordType[2]; }
        result = n % 10;
        if (result == 0 || result > 4) {
            return wordType[2];
        }
        if (result > 1) {
            return wordType[1];
        } if (result == 1) {
            return wordType[0];
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}