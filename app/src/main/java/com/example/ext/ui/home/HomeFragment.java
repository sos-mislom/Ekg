package com.example.ext.ui.home;

import static com.example.ext.ui.diary.DiaryViewModel.deserialize;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {
    private Typeface typefaceRoboto;
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private final Handler HandlerCheckAllAccess = new Handler();
    private boolean isHomeWorkSet;

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
        setUI();
        return root;

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

    }

    @SuppressLint("ResourceAsColor")
    private void setTableOfHomeWork(){
        if (isHomeWorkSet){
            Map<String, ArrayList<String>> next_day_map = homeViewModel.getMapOfHW().getValue();
            TextView home_work_tv = binding.HomeWorkTV;
            TableLayout home_work = binding.tblHomeWork;
            home_work_tv.setText("???? ???????????? ???????????? " + next_day_map.keySet().size() + getWordDeclension(next_day_map.keySet().size()));
            for (String key : next_day_map.keySet()) {
                TableRow tableRow1 = new TableRow(getContext());
                tableRow1.setGravity(Gravity.CENTER_VERTICAL);
                String subj = next_day_map.get(key).get(0);
                String subj_name_str;
                if (subj.length() > 20) { subj_name_str = subj.substring(0, 19)+"...";}else  subj_name_str = subj;
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
                hw_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);
                hw_tv.setPadding(40, 10, 40, 15);
                hw_tv.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f));
                tableRow1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (hw_tv.getVisibility() == View.VISIBLE){
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
            isHomeWorkSet = false;
        }
    }

    @SuppressLint("SetTextI18n")
    private void setTableOfDiary(){
        String dairyData;
        Map<String, ArrayList<String>> jsondairy = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", new Locale("ru"));
        Date d = new Date();
        Map<String, ArrayList> curr_day_map = new HashMap<>();
        String dayOfTheWeek = sdf.format(d).substring(0, 1).toUpperCase() + sdf.format(d).substring(1);
        dairyData = MainActivity.data;
        if (dairyData != null && dairyData.length() > 0) {
            try {
                jsondairy = deserialize(JSON.decode(dairyData));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else{
            jsondairy.put("??????????????????????", new ArrayList<>());
            jsondairy.put("??????????????", new ArrayList<>());
            jsondairy.put("??????????", new ArrayList<>());
            jsondairy.put("??????????????", new ArrayList<>());
            jsondairy.put("??????????????", new ArrayList<>());
            jsondairy.put("??????????????", new ArrayList<>());
            jsondairy.put("??????????????????????", new ArrayList<>());
        }
        curr_day_map.put(dayOfTheWeek, jsondairy.get(dayOfTheWeek));
        ArrayList<String> days = new ArrayList<>();
        days.add("??????????????????????");days.add("??????????????");days.add("??????????");days.add("??????????????");
        days.add("??????????????");days.add("??????????????");days.add("??????????????????????");
        String nextDayOfWeek;
        if (dayOfTheWeek.equals("??????????????????????")){
            nextDayOfWeek = days.get(0);
        } else{
            nextDayOfWeek = days.get(days.indexOf(dayOfTheWeek)+1);
        }
        curr_day_map.put(nextDayOfWeek, jsondairy.get(nextDayOfWeek));
        ArrayList<ArrayList<String>> intervals = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        intervals.add(new ArrayList<String>(Arrays.asList("8:20", "9:20")));
        intervals.add(new ArrayList<String>(Arrays.asList("9:20", "10:20")));
        intervals.add(new ArrayList<String>(Arrays.asList("10:20", "11:20")));
        intervals.add(new ArrayList<String>(Arrays.asList("11:20", "12:20")));
        intervals.add(new ArrayList<String>(Arrays.asList("12:20", "13:20")));
        intervals.add(new ArrayList<String>(Arrays.asList("13:20", "14:20")));
        intervals.add(new ArrayList<String>(Arrays.asList("14:20", "15:20")));
        intervals.add(new ArrayList<String>(Arrays.asList("15:20", "16:20")));

        TextView currentTV = binding.currentTV;
        TextView after = binding.afterTV;
        for (String key : curr_day_map.keySet()) {
            for (ArrayList arr : intervals){
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    if (!key.equals(dayOfTheWeek)){
                        after.setText("???????????? " + curr_day_map.get(key).size() + getWordDeclension(curr_day_map.get(key).size()));
                    } else {
                        Date date_begin;
                        try {
                            date_begin = formatter.parse(arr.get(0).toString());
                            Date date_end = formatter.parse(arr.get(1).toString());
                            Date date_current = formatter.parse(currentTime);
                            currentTV.setText("?????????? ??????????????????????");
                            if ((date_current.after(date_begin) || date_current.equals(date_begin))
                                    && (date_current.before(date_end)) || date_current.equals(date_end)) {
                                if (intervals.indexOf(arr) < curr_day_map.get(key).size()){
                                    currentTV.setText(curr_day_map.get(key).get(intervals.indexOf(arr)-1).toString() +" ?? " + intervals.get(curr_day_map.get(key).size()).get(1));
                                }
                            } else if (date_current.after(date_end)){
                                if (curr_day_map.get(key).size() == 0){
                                    currentTV.setText("?????????? ?????????????????????? ?? " +  intervals.get(curr_day_map.get(key).size()).get(1));
                                }else currentTV.setText("?????????? ?????????????????????? ?? " +  intervals.get(curr_day_map.get(key).size()-1).get(1));
                            }
                        } catch (ParseException | IndexOutOfBoundsException e) {
                            currentTV.setText("?????????? ??????????????????????");
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void setUI(){
        if (binding != null) {

            HorizontalScrollView horizontalScrollView = binding.scrollViewHorizontalLastNotes;
            horizontalScrollView.setHorizontalScrollBarEnabled(false);
            ScrollView scrollView = binding.scrollViewHome;
            scrollView.setVerticalScrollBarEnabled(false);
            setTableOfDiary();
        }
    }
    private String getWordDeclension(Integer n){
        String[] wordType = new String[]{" ????????", " ??????????", " ????????????"};
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