package com.example.ext;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Activity_log extends AppCompatActivity {
    EditText usernameFromLay, passwordFromLay;
    private String username;
    private String password;
    TextView logTview, logInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameFromLay = (EditText) findViewById(R.id.login);
        passwordFromLay = (EditText) findViewById(R.id.password);
        logInBtn = (Button) findViewById(R.id.enter);
        logTview = (TextView) findViewById(R.id.auth);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        password = preferences.getString("password", "");
        username = preferences.getString("username", "");

        if (password.length() > 0 || username.length() > 0){
            Intent intent = new Intent(this, Activity_main.class);
            startActivity(intent);
            finish();
        }

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameFromLay.getText().toString();
                password = passwordFromLay.getText().toString();
                new ChecLogInfo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
    }


    private class ChecLogInfo extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            Ext ext = null;
            String result = "";
            try {
                ext = new Ext(username, password);
                JSONArray confirm = ext.AUTH();
                result = confirm.getString(0);

            } catch (NoSuchAlgorithmException | IOException | JSONException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("ok")){
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                        .putString("password", password)
                        .putString("username", username)
                        .apply();
                logTview.setText("Успех");
                Intent intent = new Intent(Activity_log.this, Activity_main.class);
                startActivity(intent);
                finish();
            }
            else if (result.equals("Неправильный логин и/или пароль.")){logTview.setText("Неправильный логин и/или пароль.");
            }
            else if (result.equals("Ошибка авторизации.")){
                logTview.setText("Ошибка авторизации.");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

}