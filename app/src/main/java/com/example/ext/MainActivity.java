package com.example.ext;

import static com.example.ext.ConfigApiResponses.lenOfBreak;
import static com.example.ext.ConfigApiResponses.lenOfShortBreak;
import static com.example.ext.ConfigApiResponses.listOfIntervals;
import static com.example.ext.ConfigApiResponses.positionOfShortBreak;
import static com.example.ext.ConfigApiResponses.startTime;

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

import com.example.ext.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    public static String data;
    public static String username;
    public static String password;
    public static ArrayList<Integer> TimeList;
    public static String lessons_start;
    public static String long_break;
    public static String curt_break;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        password = preferences.getString("password", "");
        username = preferences.getString("username", "");
        data = preferences.getString("dairyData", "");

        lessons_start = preferences.getString("lessons_start", "");
        long_break = preferences.getString("long_break", "");
        curt_break = preferences.getString("curt_break", "");

        if (lessons_start.length() == 0){
            lessons_start = "8:10"; }
        if (long_break.length() == 0){
            long_break = "20"; }
        if (curt_break.length() == 0){
            curt_break = "10"; }

        TimeList = new ArrayList<Integer>();
        for (String x : lessons_start.split(":")){
            TimeList.add(Integer.parseInt(x));
        }
        createListOfIntervals(
                new ArrayList<Integer>(TimeList),
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




    private void createListOfIntervals(ArrayList<Integer> mstartTime,
                                       ArrayList<Integer> mpositionOfShortBreak,
                                       int mlenOfBreak,
                                       int mlenOfShortBreak){
        startTime = mstartTime;
        positionOfShortBreak = mpositionOfShortBreak;
        lenOfBreak = mlenOfBreak;
        lenOfShortBreak = mlenOfShortBreak;
        createListOfIntervals();
    }

    private void createListOfIntervals(){
        listOfIntervals = new ArrayList<ArrayList<String>>();

        listOfIntervals.add(new ArrayList<String>(Arrays.asList(
                formatTime(startTime),
                formatTime(new ArrayList<Integer>(Arrays.asList(
                        startTime.get(0),
                        startTime.get(1)+40)))
        )));

        for (int i = 0; i < 8; i++) {
            if (positionOfShortBreak.contains(i)){
                listOfIntervals.add(minusTime(startTime, lenOfShortBreak));
            } else {

                listOfIntervals.add(minusTime(startTime, lenOfBreak));
            }
        }
    }

    private ArrayList<String> minusTime(ArrayList<Integer> listOfTime, int lenOfBreak){
        listOfTime = rebaseTime(new ArrayList<Integer>(
                Arrays.asList(listOfTime.get(0),
                        listOfTime.get(1)+40+lenOfBreak)));
        startTime = listOfTime;

        return new ArrayList<String>(Arrays.asList(formatTime(listOfTime),
                formatTime(rebaseTime(new ArrayList<Integer>(
                        Arrays.asList(listOfTime.get(0),
                                listOfTime.get(1) + 40))))));
    }

    private ArrayList<Integer> rebaseTime(ArrayList<Integer> listOfTime){
        if (listOfTime.get(1) >= 60){
            return new ArrayList<Integer>(
                    Arrays.asList(
                            listOfTime.get(0) + (listOfTime.get(1) / 60),
                            listOfTime.get(1) - 60 * (listOfTime.get(1) / 60)));
        }
        return listOfTime;
    }

    private String formatTime(ArrayList<Integer> listOfTime){
        if (listOfTime.get(0) < 2){
            return String.format(
                    "%s0:%s",
                    listOfTime.get(0).toString(),
                    listOfTime.get(1).toString());
        } else if (listOfTime.get(1) < 2){
            return String.format(
                    "%s:%s0",
                    listOfTime.get(0).toString(),
                    listOfTime.get(1).toString());
        }
        return String.format(
                "%s:%s",
                listOfTime.get(0).toString(),
                listOfTime.get(1).toString());
    }
}