package com.example.ext;

import static com.example.ext.helper.PreferencesUtil.curt_break;
import static com.example.ext.helper.PreferencesUtil.long_break;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.ext.databinding.ActivityMainBinding;
import com.example.ext.helper.PreferencesUtil;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    public static PreferencesUtil preferencesUtil;

    protected void onCreate(Bundle savedInstanceState) {
        preferencesUtil = new PreferencesUtil(this);
        preferencesUtil.checkAppVersionCodeInSite();
        PreferencesUtil.createListOfIntervals(
                new ArrayList<Integer>(PreferencesUtil.TimeList),
                new ArrayList<Integer>(Collections.singletonList(0)),
                Integer.parseInt(long_break),
                Integer.parseInt(curt_break));

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