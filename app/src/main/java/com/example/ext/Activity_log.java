package com.example.ext;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_log extends AppCompatActivity {
    EditText usernameFromLay;
    EditText passwordFromLay;

    private TextView logTview;
    private TextView logInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameFromLay = (EditText) findViewById(R.id.login);
        passwordFromLay = (EditText) findViewById(R.id.password);
        logInBtn = (Button) findViewById(R.id.enter);


        logTview = (TextView) findViewById(R.id.auth);

/*        asyncEXT task = new asyncEXT();
        task.execute();*/

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameFromLay.getText().toString();
                String password = passwordFromLay.getText().toString();
                int Code_Of_Check_auth = AUTHCheck.checkLogin(username, password);
                if (Code_Of_Check_auth == 200){ logTview.setText("Успех");
                    Intent i = new Intent(Activity_log.this, Activity_main.class);
                    i.putExtra("result", username+" "+password);
                    startActivity(i);
                }
                else if (Code_Of_Check_auth == 103){logTview.setText("Неверный логин");
                }
                else if (Code_Of_Check_auth == 104){
                    logTview.setText("Неверный пароль");
                }
            }
        });
    }



    public static class AUTHCheck {
        private static final int MIN_PASS_LENGHT = 0;
        private static final int MIN_LOG_LENGHT = 0;

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