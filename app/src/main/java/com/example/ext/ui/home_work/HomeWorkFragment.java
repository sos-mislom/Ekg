package com.example.ext.ui.home_work;

import static com.example.ext.ConfigApiResponses.HOME_WORK;
import static com.example.ext.ui.home_work.HomeWorkViewModel.IfmMapHWNotNull;
import static com.example.ext.ui.home_work.HomeWorkViewModel.begin_dt;
import static com.example.ext.ui.home_work.HomeWorkViewModel.getStartHomeWorkAsync;
import static com.example.ext.ui.note.NoteFragment.clearTableView;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ext.R;
import com.example.ext.databinding.FragmentHomeWorkBinding;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HomeWorkFragment extends Fragment {
    private Handler HandlerCheckAllAccess = new Handler();
    private FragmentHomeWorkBinding binding;
    private HomeWorkViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeWorkBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        if (HOME_WORK == null){
            notificationsViewModel =
                    new ViewModelProvider(this).get(HomeWorkViewModel.class);
            HandlerCheckAllAccess.post(CheckAllAccess);
            return root;
        } else {
            setUI(HOME_WORK);
        }
        return root;
    }

    private final Runnable CheckAllAccess = new Runnable() {
        @Override
        public void run() {
            if (IfmMapHWNotNull()){
                try {
                    HandlerCheckAllAccess.removeCallbacks(CheckAllAccess);
                    setUI(HOME_WORK);
                } catch (NullPointerException e){
                    HandlerCheckAllAccess.postDelayed(this, 1000);
                }
            } else {
                HandlerCheckAllAccess.postDelayed(this, 2500);
            }
        }
    };

    public void setUI(Map<String, ArrayList> result){
        int j;
        TableLayout row_of_subj;
        TableRow row_of_date;
        TextView textView1;
        TextView textView2;
        TableLayout row_of_day;

        TableLayout tblayoutl = binding.tblHomeWorkContent;
        clearTableView(tblayoutl);

        TableLayout.LayoutParams tlLayoutParams = new TableLayout.LayoutParams();
        tlLayoutParams.setMargins(25,10,20,25);
        tblayoutl.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.less_rounded_corners) );

        List<String> keys = new ArrayList<>(result.keySet());
        List<LocalDate> keys_format_date = new ArrayList<>();
        for (int i = 0; i < keys.size(); i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                keys_format_date.add(LocalDate.parse(keys.get(i), DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            }
        }
        Collections.sort(keys_format_date);
        keys = new ArrayList<String>();
        for (int i = 0; i < keys_format_date.size(); i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                keys.add(keys_format_date.get(i).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            }
        }

        ImageView imageViewWeekNext = new ImageView(getContext());
        imageViewWeekNext.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.week_next));

        imageViewWeekNext.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                getStartHomeWorkAsync(begin_dt.plusWeeks(1));
                HandlerCheckAllAccess.post(CheckAllAccess);

            }
        });

        ImageView imageViewWeekPrev = new ImageView(getContext());
        imageViewWeekPrev.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.week_prev));
        imageViewWeekPrev.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                getStartHomeWorkAsync(begin_dt.minusWeeks(1));
                HandlerCheckAllAccess.post(CheckAllAccess);
            }
        });
        TableRow tableRow = new TableRow(getContext());
        imageViewWeekPrev.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 100, 40f));
        tableRow.addView(imageViewWeekPrev);
        tableRow.addView(imageViewWeekNext);
        tblayoutl.addView(tableRow, tlLayoutParams);


        for (String date: keys) {
            row_of_date = new TableRow(getContext());
            TextView textView = new TextView(getContext());
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.ads_details_header_background));
            textView.setPadding(4, 0, 0, 0);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy");
            Date dt1 = null;
            try {
                dt1 = format1.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            @SuppressLint("SimpleDateFormat") DateFormat format2 = new SimpleDateFormat("EEEE");
            assert dt1 != null;
            String finalDay = format2.format(dt1);
            textView.setText(finalDay.substring(0, 1).toUpperCase() + finalDay.substring(1) + " " + date);
            row_of_date.setPadding(10,0,0,0);
            textView.setTextSize(18);
            row_of_date.addView(textView);
            row_of_date.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.avatar_color_blue));
            tblayoutl.addView(row_of_date, tlLayoutParams);
            j = 1;
            row_of_day = new TableLayout(getContext());
            ArrayList<ArrayList<String>> array = result.get(date);
            for (int i = 0; i < array.size(); i++) {
                row_of_subj = new TableLayout(getContext());
                row_of_subj.setPadding(20,0,20,5);
                textView1 = new TextView(getContext());
                textView1.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Roboto-Regular.ttf"));
                textView1.setText(j + ". " + array.get(i).get(0));
                textView1.setPadding(25, 5, 0, 5);
                textView1.setTextColor(ContextCompat.getColor(getContext(), R.color.abc_search_url_text_pressed));
                textView1.setTextSize(29);
                textView1.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f));

                textView2 = new TextView(getContext());
                textView2.setVisibility(View.GONE);
                textView2.setTextColor(ContextCompat.getColor(getContext(), R.color.blue_gray));
                textView2.setText(array.get(i).get(2));
                textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                textView2.setPadding(40, 5, 40, 10);
                textView2.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f));
                TextView finalTextView = textView2;
                textView1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (finalTextView.getVisibility() == View.VISIBLE) {
                            finalTextView.setVisibility(View.GONE);
                        } else finalTextView.setVisibility(View.VISIBLE);
                    }
                });
                row_of_subj.addView(textView1);
                row_of_subj.addView(textView2);
                row_of_subj.setDividerDrawable(ContextCompat.getDrawable(getContext(),R.drawable.line_devider));
                row_of_subj.setShowDividers(LinearLayout.SHOW_DIVIDER_END);
                row_of_day.addView(row_of_subj);
                row_of_day.setElevation(5);
                row_of_date.setElevation(5);
                row_of_day.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.my_border_tv_blue));
                j++;
            }
            tblayoutl.addView(row_of_day, tlLayoutParams);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}