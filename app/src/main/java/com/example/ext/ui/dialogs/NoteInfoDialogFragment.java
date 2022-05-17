package com.example.ext.ui.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.ext.R;

import java.util.ArrayList;

public class NoteInfoDialogFragment extends DialogFragment{
    ArrayList<String> data;

    public NoteInfoDialogFragment(ArrayList<String> arrayList) {
        this.data = arrayList;
    }
    private String chekIfNotNull(String response){
        if (response.equals("null")){
            return "Отсутствует";
        }return response;
    }
    @SuppressLint("ResourceType")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder placeInfo = new AlertDialog.Builder(getActivity());
        switch (data.get(2)) {
            case ("5"):
                placeInfo.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.average_mark_background_gradient_good));
                break;
            case ("4"):
                placeInfo.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.average_mark_background_gradient_ok));
                break;
            case ("3"):
                placeInfo.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.average_mark_background_gradient_normal));
                break;
            case ("2"):
                placeInfo.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.average_mark_background_gradient_bad));
                break;
            default:
                placeInfo.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.average_mark_background_gradient_default));
                break;
        }
        placeInfo.setTitle(data.get(4));

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_fragment_no_scroll, null);

        TableLayout tableLayout = view.findViewById(R.id.contentDialogFragment);
        tableLayout.setPadding(40,30,40,30);

        TextView tx_date = new TextView(getContext());
        tx_date.setTextSize(22);
        tx_date.setText("Дата: " + data.get(0)+"\n");
        tx_date.setTypeface(null, Typeface.ITALIC);
        tableLayout.addView(tx_date);

        TextView tx_comment = new TextView(getContext());
        tx_comment.setTextSize(20);
        tx_comment.setText("Комментарий: " + chekIfNotNull(data.get(1))+"\n");
        tx_comment.setTypeface(null, Typeface.BOLD);
        tableLayout.addView(tx_comment);

        TextView tx_theme = new TextView(getContext());
        tx_theme.setTextSize(20);
        tx_theme.setText("Тема: " +chekIfNotNull(data.get(5))+"");
        tableLayout.addView(tx_theme);

        TextView tx_home_work = new TextView(getContext());
        tx_home_work.setTextSize(20);
        tx_home_work.setText("Д/з: " +chekIfNotNull(data.get(6))+"\n");
        tableLayout.addView(tx_home_work);

        TextView tx_weight = new TextView(getContext());
        tx_weight.setTextSize(18);
        tx_weight.setText("Вес: " +Double.parseDouble(data.get(3))+"");
        tx_weight.setTypeface(null, Typeface.BOLD_ITALIC);
        tableLayout.addView(tx_weight);
        placeInfo.setNegativeButton("закрыть", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        placeInfo.setView(view);
        return placeInfo.create();
    }
}
