package com.example.ext;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    EditText usernameFromLay;
    EditText passwordFromLay;
    private StringBuilder strTree = new StringBuilder();
    private TextView logTview;
    private TextView logInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameFromLay = (EditText) findViewById(R.id.login);
        passwordFromLay = (EditText) findViewById(R.id.password);
        logInBtn = (Button) findViewById(R.id.login_button);


        //logTview = (TextView) findViewById(R.id.tvLog);
        asyncEXT task = new asyncEXT();
        task.execute();
        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameFromLay.getText().toString();
                String password = passwordFromLay.getText().toString();
                int Code_Of_Check_auth = AUTHCheck.checkLogin(username, password);
                if (Code_Of_Check_auth == 200){ logTview.setText("success");
                }
                else if (Code_Of_Check_auth == 103){logTview.setText("incorrect login");
                }
                else if (Code_Of_Check_auth == 104){
                    logTview.setText("incorrect password");
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class asyncEXT extends AsyncTask<Void, Void, Void> {
        private void UCH_YEAR() {
            try {
                Ext ext = new Ext("Зайцев","3MA8|ZJQ{0");
                strTree.append(ext.UCH_YEAR());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace(); }
            }

        @Override
        protected Void doInBackground(Void... params) {
            UCH_YEAR();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            logTview.setText(strTree);
            System.out.println(strTree);
        }
    }

    public static class AUTHCheck {
        private static final int MIN_PASS_LENGHT = 10;
        private static final int MIN_LOG_LENGHT = 5;

        public static int checkPassword(String password, String login){
            if(password.length() >= MIN_PASS_LENGHT){
               if(login.length() >= MIN_LOG_LENGHT){
                   return 200;
                }
                return 103;
            }
            return 104;
        }

        public static int checkLogin(String login, String password){
            return checkPassword(password, login);
        }
    }
}