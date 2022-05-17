package com.example.ext.ui.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.example.ext.MainActivity;
import com.example.ext.R;
import com.example.ext.api.Ext;

import java.security.NoSuchAlgorithmException;

public class ReloadDialogFragment extends DialogFragment{
    @SuppressLint("ResourceType")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder placeInfo = new AlertDialog.Builder(getActivity());
        placeInfo.setTitle("Сервер шалит");
        placeInfo.setIcon(R.drawable.ic_book);
        placeInfo.setMessage("НУЖНО ПЕРЕЗАГРУЗИТЬ");
        placeInfo.setNegativeButton("ПЕРЕЗАГРУЗИТЬ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (MainActivity.getExt() == null){
                    try {
                        MainActivity.globalext = new Ext("Зайцев", "3MA8|ZJQ{0");
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
            }
            }
        });
        return placeInfo.create();
    }
}
