package com.example.ext.ui.note;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.ext.R;
import com.example.ext.ui.ObjectOfSubject;
import com.example.ext.ui.dialogs.NoteInfoDialogFragment;
import com.example.ext.ui.dialogs.SubjInfoDialogFragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

public class NoteFragment extends Fragment {
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes_of_subj, container, false);
        if(getArguments() != null) {

            int count = 0;
            double nominator = 0.0;
            double denominator = 0.0;
            double weight;

            ObjectOfSubject obj = getArguments().getParcelable("key");
            Map<String, ArrayList> data = obj.paramToo;
            String subj = obj.paramOne;

            Typeface typefaceRoboto = Typeface.createFromAsset(getContext().getAssets(), "Roboto-Regular.ttf");
            ConstraintLayout tableLayout = view.findViewById(R.id.subjectNotes);
            TextView average_note = view.findViewById(R.id.SubjAverage);
            TextView subj_name = view.findViewById(R.id.SubjName);
            TableRow row_of_notes = view.findViewById(R.id.rowOfNotes);

            String subj_name_str;
            if (subj.length() > 26) {
                subj_name_str = subj.substring(0, 23) + "...";
            } else subj_name_str = subj;

            subj_name.setText(subj_name_str);
            subj_name.setTypeface(typefaceRoboto);

            ArrayList<ArrayList<String>> array = data.get(subj);

            int start = 0;
            if (array.size() >= 8) {
                start = array.size() - 7;
            }

            for (int j = 0; j < array.size(); j++) {
                TextView mark_tv = new TextView(getContext());
                mark_tv.setTextSize(26);
                mark_tv.setTextColor(Color.WHITE);
                mark_tv.setGravity(Gravity.CENTER);
                mark_tv.setMinWidth(85);
                mark_tv.setHeight(85);
                mark_tv.setTypeface(typefaceRoboto);
                mark_tv.setElevation(5f);

                TableRow.LayoutParams trRowParams = new TableRow.LayoutParams();
                trRowParams.setMargins(20, 10, 20, 30);

                Drawable back_gradient;
                switch (array.get(j).get(2)) {
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

                LayerDrawable finalDrawable = new LayerDrawable(new Drawable[]{back_gradient});
                if (!array.get(j).get(1).equals("null")) {
                    Drawable dotIcon = ContextCompat.getDrawable(getContext(), R.drawable.oval);
                    finalDrawable = new LayerDrawable(new Drawable[]{back_gradient, dotIcon});
                    finalDrawable.setLayerInset(0, 0, 0, 0, 0);
                    finalDrawable.setLayerInset(1, 65, 0, 0, 65);
                }
                mark_tv.setBackgroundDrawable(finalDrawable);

                weight = Double.parseDouble(array.get(j).get(3));
                if (array.get(j).get(2).matches("[0-9]")) {
                    count++;
                    nominator += Integer.parseInt(array.get(j).get(2)) * weight;
                    denominator += weight;
                }
                mark_tv.setText(array.get(j).get(2));

                int finalJ = j;
                mark_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NoteInfoDialogFragment newFragment = new NoteInfoDialogFragment(array.get(finalJ));
                        newFragment.show(getChildFragmentManager().beginTransaction(), "info");
                    }
                });
                if (j >= start) {
                    row_of_notes.addView(mark_tv, trRowParams);
                }

            }

            if (count > 2) {
                String formattedDouble = new DecimalFormat("#0.00").format(nominator / denominator);
                average_note.setText(formattedDouble);
            } else {
                average_note.setText("0.00");
            }

            average_note.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.my_border_tv));
            average_note.setTextSize(20);
            average_note.setPadding(10, 10, 10, 10);
            average_note.setTextColor(ContextCompat.getColor(getContext(), R.color.camo_green));


            tableLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SubjInfoDialogFragment newFragment = new SubjInfoDialogFragment(data.get(subj), subj);
                    newFragment.show(getChildFragmentManager().beginTransaction(), "info");
                }
            });


        }
        return view;
    }
}
