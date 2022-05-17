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
    public static Ext getExt() {
        if (globalext == null){
            try {
                return new Ext("Зайцев","3MA8|ZJQ{0");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return globalext;
    }
    public static Ext globalext;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.ext.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        data = preferences.getString("dairyData", "");
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