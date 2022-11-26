package com.example.ext.ui.dialogs;


import static com.example.ext.helper.PreferencesUtil.TimeList;
import static com.example.ext.helper.PreferencesUtil.curt_break;
import static com.example.ext.helper.PreferencesUtil.long_break;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.example.ext.LoginActivity;
import com.example.ext.R;

public class SettingsDialogFragment extends DialogFragment {
    private TextView LessonBegin;
    private TimePicker LessonBeginTimePicker;
    private TextView LongBreak;
    private TimePicker LongBreakTimePicker;
    private TextView CurtBreak;
    private TimePicker CurtBreakTimePicker;


    @SuppressLint({"ResourceType", "SetTextI18n"})
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder placeInfo = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_fragment_settingds, null);
        ConstraintLayout cll = view.findViewById(R.id.constraintLayout2);
        LessonBegin = view.findViewById(R.id.last_notes);
        LessonBeginTimePicker = new TimePicker(getContext(), null, R.style.TimePicker);
        LessonBeginTimePicker.setVisibility(View.GONE);

        cll.addView(LessonBeginTimePicker);

        Button btn_exit = view.findViewById(R.id.exit);
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
                        .putString("password", "")
                        .putString("username", "")
                        .apply();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        LessonBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LessonBeginTimePicker.getVisibility() == View.VISIBLE){
                    LessonBeginTimePicker.setVisibility(View.GONE);
                } else LessonBeginTimePicker.setVisibility(View.VISIBLE);
            }
        });
        LessonBegin.setText(
                "Начало уроков: " +
                TimeList.get(0) +
                ":" +
                TimeList.get(1));

        LessonBeginTimePicker.getLayoutMode();
        LessonBeginTimePicker.setHour(TimeList.get(0));
        LessonBeginTimePicker.setMinute(TimeList.get(1));
        LessonBeginTimePicker.setIs24HourView(true);
        LessonBeginTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
//                Toast.makeText(getContext(), "onTimeChanged",
//                        Toast.LENGTH_SHORT).show();
                String str_ = LessonBeginTimePicker.getHour() + ":" +LessonBeginTimePicker.getMinute();

                LessonBegin.setText(new StringBuilder()
                        .append("Начало уроков: ")
                        .append(str_));
                PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
                        .putString("lessons_start", str_)
                        .apply();
            }
        });

        ConstraintLayout cll2 = view.findViewById(R.id.constraintLayout4);

        LongBreak = view.findViewById(R.id.long_of_classic);
        LongBreak.setText("Длина перемены: " + long_break);
        LongBreakTimePicker = new TimePicker(getContext(), null, R.style.TimePicker);
        LongBreakTimePicker.setVisibility(View.GONE);

        cll2.addView(LongBreakTimePicker);

        LongBreak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LongBreakTimePicker.getVisibility() == View.VISIBLE){
                    LongBreakTimePicker.setVisibility(View.GONE);
                } else LongBreakTimePicker.setVisibility(View.VISIBLE);
            }
        });
        LongBreakTimePicker.setHour(0);
        LongBreakTimePicker.setMinute(Integer.parseInt(long_break));
        LongBreakTimePicker.setIs24HourView(true);
        LongBreakTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
//                Toast.makeText(getContext(), "onTimeChanged",
//                        Toast.LENGTH_SHORT).show();
                String str_ = LongBreakTimePicker.getHour() + ":" + LongBreakTimePicker.getMinute();

                LongBreak.setText(new StringBuilder()
                        .append("Длина перемены: ")
                        .append(str_));
                PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
                        .putString("long_break", LongBreakTimePicker.getMinute()+"")
                        .apply();
            }
        });

        ConstraintLayout cll3 = view.findViewById(R.id.constraintLayout);
        CurtBreak = view.findViewById(R.id.long_of_curt);
        CurtBreakTimePicker = new TimePicker(getContext(), null, R.style.TimePicker);
        CurtBreakTimePicker.setVisibility(View.GONE);

        cll3.addView(CurtBreakTimePicker);

        CurtBreak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CurtBreakTimePicker.getVisibility() == View.VISIBLE){
                    CurtBreakTimePicker.setVisibility(View.GONE);
                } else CurtBreakTimePicker.setVisibility(View.VISIBLE);
            }
        });


        CurtBreakTimePicker.setHour(0);
        CurtBreak.setText("Длина кор. перемены: " + curt_break);
        CurtBreakTimePicker.setMinute(Integer.parseInt(curt_break));
        CurtBreakTimePicker.setIs24HourView(true);
        CurtBreakTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
//                Toast.makeText(getContext(), "onTimeChanged",
//                        Toast.LENGTH_SHORT).show();
                String str_ = CurtBreakTimePicker.getHour() + ":" + CurtBreakTimePicker.getMinute();
                CurtBreak.setText(new StringBuilder()
                        .append("Длина короткой перемены: ")
                        .append(str_));
                PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
                        .putString("curt_break", CurtBreakTimePicker.getMinute()+"")
                        .apply();
            }
        });

        placeInfo.setTitle("НАСТРОЙКИ");
        placeInfo.setIcon(R.drawable.wing);
        placeInfo.setView(view);
        placeInfo.setNegativeButton("", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return placeInfo.create();
    }

}
