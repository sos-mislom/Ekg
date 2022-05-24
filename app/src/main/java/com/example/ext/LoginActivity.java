package com.example.ext;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ext.api.Ext;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {
    EditText usernameFromLay, passwordFromLay;
    public static String username;
    public static String password;
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
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameFromLay.getText().toString();
                password = passwordFromLay.getText().toString();
                new CheckLogInfo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
    }

    private class CheckLogInfo extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            Ext ext;
            String confirm = null;
            try {
                ext = new Ext(username, password);
                confirm = ext.AUTH();
            } catch (NoSuchAlgorithmException | IOException | NullPointerException e) {
                e.printStackTrace();
            }
            return confirm;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null){
                if (result.equals("ok")){
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                            .putString("password", password)
                            .putString("username", username)
                            .apply();
                    logTview.setText("Успех");
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }logTview.setText("Ошибка загрузки данных с сервера. Трай эгейн");
        }
    }
}