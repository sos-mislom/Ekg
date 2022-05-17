package com.example.ext.ui.home;

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

import com.example.ext.R;
import com.example.ext.databinding.FragmentHomeBinding;
import com.example.ext.ui.dialogs.NoteInfoDialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {
    private Typeface typefaceRoboto;
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private Handler HandlerCheckAllAccess = new Handler();

    @SuppressLint("ResourceAsColor")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        typefaceRoboto = Typeface.createFromAsset(getContext().getAssets(), "Roboto-Regular.ttf");
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        HandlerCheckAllAccess.post(CheckAllAccess);
        return root;

    }

    private final Runnable CheckAllAccess = new Runnable() {
        @Override
        public void run() {
            if (homeViewModel.getMapOfHW().getValue() != null && homeViewModel.getMapOfNotes().getValue() != null){
                setUI();
                HandlerCheckAllAccess.removeCallbacks(CheckAllAccess);
            } else {
                HandlerCheckAllAccess.postDelayed(this, 1000);
            }
        }
    };

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    private void setUI(){
        TableLayout tableLayout;
        TableLayout tblOtherContent;
        TextView last_notes_date_of_update;
        try {
            last_notes_date_of_update = binding.lastNotesDateOfUpdate;
            tableLayout = binding.tblLastNotes;
            tblOtherContent = binding.tblOtherContent;
        }catch (NullPointerException e){
            last_notes_date_of_update = new TextView(getContext());
            tableLayout = new TableLayout(getContext());
            tblOtherContent = new TableLayout(getContext());
        }

        Map<String, ArrayList<String>> next_day_map = homeViewModel.getMapOfHW().getValue();
        Map<String, ArrayList> curr_day_map = homeViewModel.getMapOfDiary().getValue();
        Map<String, ArrayList<String>> last_notes_map =  homeViewModel.getMapOfNotes().getValue();

        TableRow tableRow = new TableRow(getContext());
        TableRow.LayoutParams trLayoutParams = new TableRow.LayoutParams();
        trLayoutParams.setMargins(20,10,10,30);
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(getContext());
        horizontalScrollView.setHorizontalScrollBarEnabled(false);

        last_notes_date_of_update.setText("Недавно");

        for (String key : last_notes_map.keySet()) {
            TextView mark_tv = new TextView(getContext());
            mark_tv.setText(last_notes_map.get(key).get(2));
            mark_tv.setTextSize(40);
            mark_tv.setTextColor(Color.WHITE);
            mark_tv.setGravity(Gravity.CENTER);
            mark_tv.setMinWidth(200);
            mark_tv.setHeight(200);
            mark_tv.setTypeface(typefaceRoboto);
            mark_tv.setElevation(5f);
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
            tableRow.addView(mark_tv, trLayoutParams);
        }
        horizontalScrollView.addView(tableRow);
        tableLayout.addView(horizontalScrollView);

        ScrollView scrollView = new ScrollView(getContext());

        TableLayout.LayoutParams tlLayoutParams = new TableLayout.LayoutParams();
        tlLayoutParams.setMargins(20,20,20,20);

        tblOtherContent.addView(scrollView);
        TableLayout tbl_in_scr = new TableLayout(getContext());
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.addView(tbl_in_scr, tlLayoutParams);

        TextView diary_tv = new TextView(getContext());
        diary_tv.setText(R.string.diary);
        diary_tv.setTypeface(typefaceRoboto);
        diary_tv.setTextColor(ContextCompat.getColor(getContext(), R.color.abc_search_url_text_pressed));
        diary_tv.setTextSize(24);
        diary_tv.setPadding(25,0,0,45);
        tbl_in_scr.addView(diary_tv);

        TableLayout diary = new TableLayout(getContext());
        diary.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.less_rounded_corners) );
        tbl_in_scr.addView(diary);
        TextView now = new TextView(getContext());
        now.setText("сегодня");
        now.setTextColor(ContextCompat.getColor(getContext(), R.color.avatar_color_blue));
        now.setPadding(30, 10, 0, 10);
        now.setTextSize(22);
        diary.addView(now);
        diary.setElevation(5);


        TextView after = new TextView(getContext());
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
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", new Locale("ru"));
        Date d = new Date();
        for (String key : curr_day_map.keySet()) {
            TextView textView = new TextView(getContext());
            for (ArrayList arr : intervals){
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    String dayOfTheWeek = sdf.format(d).substring(0, 1).toUpperCase() + sdf.format(d).substring(1);
                    if (!key.equals(dayOfTheWeek)){
                        after.setText("Завтра " + curr_day_map.get(key).size() + getWordDeclension(curr_day_map.get(key).size()));
                    } else {
                        Date date_begin;
                        try {
                            date_begin = formatter.parse(arr.get(0).toString());
                            Date date_end = formatter.parse(arr.get(1).toString());
                            Date date_current = formatter.parse(currentTime);
                            if ((date_current.after(date_begin) || date_current.equals(date_begin))
                                    && (date_current.before(date_end)) || date_current.equals(date_end)) {
                                if (intervals.indexOf(arr) <= curr_day_map.get(key).size()){
                                    textView.setText(curr_day_map.get(key).get(intervals.indexOf(arr)).toString() +" в " + intervals.get(curr_day_map.get(key).size()).get(1));
                                }else{
                                    textView.setText("Уроки закончились в " + intervals.get(curr_day_map.get(key).size()).get(1));
                                }

                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            textView.setTextSize(26);
            textView.setPadding(20, 0, 0, 0);
            textView.setTextColor(Color.BLACK);
            diary.addView(textView);
        }


        after.setTextSize(16);
        after.setPadding(30, 10, 0, 20);
        after.setTextColor(ContextCompat.getColor(getContext(), R.color.secondary_text));
        diary.addView(after);


        TextView home_work_tv = new TextView(getContext());

        home_work_tv.setTextSize(24);
        home_work_tv.setTypeface(typefaceRoboto);
        home_work_tv.setTextColor(ContextCompat.getColor(getContext(), R.color.abc_search_url_text_pressed));
        home_work_tv.setPadding(25,50,0,45);
        tbl_in_scr.addView(home_work_tv);

        TableLayout home_work = new TableLayout(getContext());

        home_work.setDividerDrawable(getResources().getDrawable( R.drawable.divider ));
        home_work.setShowDividers(TableLayout.SHOW_DIVIDER_MIDDLE);
        home_work.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.less_rounded_corners) );
        tbl_in_scr.addView(home_work);
        home_work_tv.setText("На завтра задано " + next_day_map.keySet().size() + getWordDeclension(next_day_map.keySet().size()));
        for (String key : next_day_map.keySet()) {
            TableRow tableRow1 = new TableRow(getContext());
            tableRow1.setGravity(Gravity.CENTER_VERTICAL);
            TextView subj_tv = new TextView(getContext());

            String subj = next_day_map.get(key).get(0);
            String subj_name_str;
            if (subj.length() > 20) { subj_name_str = subj.substring(0, 19)+"...";}else  subj_name_str = subj;
            subj_tv.setTextSize(29);
            subj_tv.setText(subj_name_str);
            subj_tv.setPadding(25, 10, 0, 10);
            subj_tv.setTextColor(Color.BLACK);
            ImageView imageViewArrow = new ImageView(getContext());
            View space = new View(getContext());

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
            space.setMinimumWidth(550);
            tableRow1.addView(space);
            tableRow1.addView(imageViewArrow);

            home_work.addView(tableRow1);
            home_work.addView(hw_tv);
        }
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