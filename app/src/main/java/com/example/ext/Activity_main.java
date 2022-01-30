package com.example.ext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;

import java.security.NoSuchAlgorithmException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

public class Activity_main extends AppCompatActivity {
    NavigationView navigationView;
    TableLayout tblayoutl;
    int j;
    TableLayout row_of_subj;
    TableRow row_of_date;
    TextView textView1;
    TextView textView2;
    TableLayout row_of_day;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        new asyncEXT().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    @SuppressLint("NonConstantResourceId")
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_main:{
                Intent mainIntent = new Intent(this, Activity_main.class);
                startActivity(mainIntent);
                break;
            }
            case R.id.nav_notes: {
                Intent mainIntent = new Intent(this, Notes.class);
                startActivity(mainIntent);
                break;
            }
            case R.id.nav_diary: {
                Intent mainIntent = new Intent(this, Diary.class);
                startActivity(mainIntent);
                break;
            }
            case R.id.nav_messages:{
                Intent mainIntent = new Intent(this, Messages.class);
                startActivity(mainIntent);
                break;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class asyncEXT extends AsyncTask<Void, Void, Map<String, ArrayList>> {
        @RequiresApi(api = Build.VERSION_CODES.N)
        private Map<String, ArrayList> GetContentForActivityMain() {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDate begin_dt = LocalDate.now().with(DayOfWeek.MONDAY);
                LocalDate end_dt = begin_dt.plusDays(5);
                try {
                    Ext ext = new Ext("Зайцев","3MA8|ZJQ{0");
                    JSONArray dairyData = ext.GET_STUDENT_DAIRY(begin_dt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), end_dt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    JSONArray studentGroups = ext.GET_STUDENT_GROUPS();
                    Map<String, ArrayList> whereIsLesson = new Hashtable();
                    boolean curInGroup;
                    for (int k = 0; k < dairyData.length(); k++) {
                        curInGroup = true;
                        String Date = dairyData.getJSONArray(k).getJSONArray(0).get(2)+ "." + dairyData.getJSONArray(k).getJSONArray(0).get(1)+1 + "." + dairyData.getJSONArray(k).getJSONArray(0).get(0);
                        for (int n = 0; n < studentGroups.length(); n++) {
                            if (studentGroups.getJSONArray(n).get(1).equals(dairyData.getJSONArray(k).get(2)) && studentGroups.getJSONArray(n).get(2).equals(dairyData.getJSONArray(k).get(6))){
                                curInGroup = false;
                                break;
                            }
                        }
                        if (curInGroup || dairyData.getJSONArray(k).getInt(10) == 1){
                            String mark = dairyData.getJSONArray(k).getString(5); // after i need to add IsMarkRed and IsMarkHaveComm !!!!!!!!
                            if (dairyData.getJSONArray(k).get(7).toString().equals("null")){
                                if (whereIsLesson.get(Date) == null){
                                    whereIsLesson.put(Date, new ArrayList<>());
                                }
                                ArrayList<String> content = new ArrayList<>();
                                content.add(ext.sbj_names.get(dairyData.getJSONArray(k).getInt(2))); // 0 subject name
                                content.add(ext.teachers.get(dairyData.getJSONArray(k).getInt(9))); // 1 teacher's name
                                String hm = "";
                                if (!dairyData.getJSONArray(k).getString(3).equals("")){hm+=dairyData.getJSONArray(k).getString(3);}
                                if (!dairyData.getJSONArray(k).getString(4).equals("")){hm+=" / " +dairyData.getJSONArray(k).getString(4);}
                                content.add(hm); // 2 home work
                                content.add(mark); // 3 mark
                                Objects.requireNonNull(whereIsLesson.get(Date)).add(content);
                            }
                        }

                    }
                    return whereIsLesson;
                } catch (NoSuchAlgorithmException | JSONException e) {
                    e.printStackTrace(); }
                return null;
            }
            return null;
        }
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Map<String, ArrayList> doInBackground(Void... params) {
            return GetContentForActivityMain();
        }

        @SuppressLint("ResourceAsColor")
        @Override
        protected void onPostExecute(Map<String, ArrayList> result) {
            super.onPostExecute(result);
            tblayoutl = (TableLayout) findViewById(R.id.tblayout);
            for (String date: result.keySet()) {
                row_of_date = new TableRow(Activity_main.this);
                TextView textView = new TextView(Activity_main.this);
                textView.setText(date);
                row_of_date.addView(textView);
                row_of_date.setBackgroundColor(R.color.teal_200);
                tblayoutl.addView(row_of_date);
                j = 1;
                row_of_day = new TableLayout(Activity_main.this);
                ArrayList<ArrayList<String>> array = result.get(date);
                for (int i = 0; i < array.size(); i++) {
                    row_of_subj = new TableLayout(Activity_main.this);
                    textView1 = new TextView(Activity_main.this);
                    textView1.setText(j + " " + array.get(i).get(0).replace('$', '"'));
                    textView1.setTypeface(null, Typeface.BOLD);
                    textView1.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
                    textView1.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f));

                    textView2 = new TextView(Activity_main.this);
                    textView2.setVisibility(View.GONE);
                    textView2.setText(array.get(i).get(2));
                    textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);
                    textView2.setPadding(10, 0, 10, 0);
                    textView2.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f));

                    TextView finalTextView = textView2;
                    textView1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (finalTextView.getVisibility() == View.VISIBLE){
                                finalTextView.setVisibility(View.GONE);
                            } else finalTextView.setVisibility(View.VISIBLE);
                        }
                    });
                    row_of_subj.addView(textView1);
                    row_of_subj.addView(textView2);
                    row_of_day.addView(row_of_subj);
                    j++;
                }
                tblayoutl.addView(row_of_day);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

}