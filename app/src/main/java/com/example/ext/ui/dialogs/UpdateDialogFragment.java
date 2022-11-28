package com.example.ext.ui.dialogs;

import static androidx.core.content.FileProvider.getUriForFile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.ext.BuildConfig;
import com.example.ext.MainActivity;
import com.example.ext.R;
import com.example.ext.helper.PreferencesUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class UpdateDialogFragment extends DialogFragment {
    private ProgressDialog pDialog;
    private Context activityContext;
    public UpdateDialogFragment(Context ActivityContext){
        activityContext = ActivityContext;
    }

    @SuppressLint({"ResourceType", "SetTextI18n"})
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder placeInfo = new AlertDialog.Builder(getActivity());
        if (PreferencesUtil.aboutNewVersion.get(1) == "null"){
            PreferencesUtil.aboutNewVersion.set(1, "Без комментариев");
        }
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_fragment_for_update, null);
        TextView tvPatchNotes = view.findViewById(R.id.tvPatchNotes);

        tvPatchNotes.setText(PreferencesUtil.aboutNewVersion.get(1));

        placeInfo.setTitle("ОБНОВЛЕНИЕ ДО "+ PreferencesUtil.aboutNewVersion.get(3));
        placeInfo.setView(view);
        placeInfo.setIcon(R.drawable.ic_notifications_black_24dp);
        placeInfo.setNegativeButton("Не обновлять", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        String url = PreferencesUtil.aboutNewVersion.get(2);
        seTpDialog();
        placeInfo.setPositiveButton("Обновить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new DownloadFileFromURL().execute(url);
                dialog.dismiss();
            }
        });

        return placeInfo.create();
    }

    private void seTpDialog(){
        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Загрузка. Ожидайте сильвупле...");
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(true);
    }


    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                //URL url = new URL("https://cdn.discordapp.com/attachments/610043461122523138/1046794890807365832/app-debug.apk");
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                int lenghtOfFile = connection.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);
                File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                OutputStream output = new FileOutputStream(downloads  + "//ext.apk");

                byte[] data = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.toString());
            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String file_url) {

            File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file1 = new File (downloads + "//ext.apk");

            Uri uri = getUriForFile(activityContext, BuildConfig.APPLICATION_ID, file1);

            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activityContext.startActivity(intent);

            MainActivity.preferencesUtil.createAppVersionCode(PreferencesUtil.aboutNewVersion.get(3));
            pDialog.dismiss();

        }
    }
}
