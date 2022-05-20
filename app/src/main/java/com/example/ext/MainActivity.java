package com.example.ext;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.ext.api.Ext;
import com.example.ext.databinding.ActivityMainBinding;

import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    public static String data;
    public static Ext globalext;
    public static MainActivity ma;
    public static String username;
    public static String password;

    public static Ext getExt() {
        if (globalext == null){
            try {
                if (LoginActivity.username.length() > 0 && LoginActivity.password.length() > 0){
                    globalext = new Ext(LoginActivity.username,LoginActivity.password);
                } else{
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ma);
                    password = preferences.getString("password", "");
                    username = preferences.getString("username", "");
                    globalext =  new Ext(username, password);
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return globalext;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        data = preferences.getString("dairyData", "");
        super.onCreate(savedInstanceState);
        com.example.ext.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home_work,
                R.id.navigation_messages,
                R.id.navigation_home,
                R.id.navigation_note,
                R.id.navigation_dairy
                ).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }
}