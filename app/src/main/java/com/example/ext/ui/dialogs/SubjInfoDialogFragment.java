package com.example.ext.ui.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.ext.R;

import java.util.ArrayList;

public class SubjInfoDialogFragment extends DialogFragment{
    ArrayList<ArrayList<String>> array;
    String name;

    public SubjInfoDialogFragment(ArrayList<ArrayList<String>> arrayList, String str_subj) {
        this.array = arrayList;
        this.name = str_subj;
    }

    @SuppressLint("ResourceType")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder placeInfo = new AlertDialog.Builder(getActivity());
        placeInfo.setTitle(name);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_fragment, null);

        TableLayout tableLayout = view.findViewById(R.id.contentDialogFragment);
        TableRow row_of_notes = new TableRow(getContext());
        for (int j = 0; j < array.size(); j++) {
            TextView mark_tv = new TextView(getContext());
            mark_tv.setTextSize(26);
            mark_tv.setTextColor(Color.WHITE);
            mark_tv.setGravity(Gravity.CENTER);
            mark_tv.setMinWidth(70);
            mark_tv.setHeight(70);
            mark_tv.setElevation(5f);

            TableRow.LayoutParams trRowParams = new TableRow.LayoutParams();
            trRowParams.setMargins(30, 10, 20, 30);
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

            LayerDrawable finalDrawable = new LayerDrawable(new Drawable[] {back_gradient});
            if (!array.get(j).get(1).equals("null")){
                Drawable dotIcon = ContextCompat.getDrawable(getContext(), R.drawable.oval);
                finalDrawable = new LayerDrawable(new Drawable[] {back_gradient, dotIcon});
                finalDrawable.setLayerInset(0, 0, 0, 0, 0);
                finalDrawable.setLayerInset(1,65,0,0,65);
            }
            mark_tv.setBackgroundDrawable(finalDrawable);
            mark_tv.setText(array.get(j).get(2));

            int finalJ = j;
            mark_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NoteInfoDialogFragment newFragment = new NoteInfoDialogFragment(array.get(finalJ));
                    newFragment.show(getChildFragmentManager().beginTransaction(), "info");
                }
            });
            row_of_notes.addView(mark_tv, trRowParams);
        }
        tableLayout.addView(row_of_notes);
        placeInfo.setView(view);
        placeInfo.setNegativeButton("закрыть", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return placeInfo.create();
    }
}
