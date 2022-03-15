package com.example.ext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class Messages extends AppCompatActivity {
    NavigationView navigationView;
    TableLayout tblayoutl;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
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
        private Map<String, ArrayList> GetContentForMessages() {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDate begin_dt = LocalDate.now().with(DayOfWeek.MONDAY);
                LocalDate end_dt = begin_dt.plusDays(5);
                try {
                    Ext ext = Activity_main.getExt();
                    JSONArray messageData = ext.GET_MESSAGES();
                    Map<String, ArrayList> messages = new TreeMap();

                    for (int k = 0; k < messageData.length(); k++) {
                        String Date = ext.GET_DATE(messageData.getJSONArray(k).getJSONArray(4));
                        String teacher = ext.teachers.get(messageData.getJSONArray(k).getInt(1));
                            if (messages.get(teacher) == null){
                                messages.put(teacher, new ArrayList<>());
                            }
                            ArrayList<String> content = new ArrayList<>();
                            content.add(Date);                                                             // 0 date
                            content.add(messageData.getJSONArray(k).getString(3));                  // 1 message
                            content.add(ext.sbj_names.get(messageData.getJSONArray(k).getInt(6))); // 2 subject name

                            Objects.requireNonNull(messages.get(teacher)).add(content);
                    }
                    return messages;
                } catch (JSONException e) {
                    e.printStackTrace(); }
                return null;
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Map<String, ArrayList> doInBackground(Void... params) {
            return GetContentForMessages();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("ResourceAsColor")
        @Override
        protected void onPostExecute(Map<String, ArrayList> result) {
            super.onPostExecute(result);
            tblayoutl = (TableLayout) findViewById(R.id.tblayout);
            for (String teacher: result.keySet()) {
                TableRow row_of_msg_teacher = new TableRow(Messages.this);
                TableLayout tbl_of_msg = new TableLayout(Messages.this);

                ArrayList<ArrayList<String>> array = result.get(teacher);
                TextView tt = new TextView(Messages.this);
                tt.setText(teacher);
                tt.setTextSize(20);
                tt.setPadding(5, 0, 0, 0);
                row_of_msg_teacher.addView(tt);
                tbl_of_msg.addView(row_of_msg_teacher);

                for (int j = 0; j < array.size(); j++) {
                    TableRow row_of_msg = new TableRow(Messages.this);
                    TextView msg = new TextView(Messages.this);
                    msg.setText(array.get(j).get(1) + " " + array.get(j).get(0));
                    msg.setPadding(8, 0, 0, 0);
                    array.get(j).get(2);
                    row_of_msg.addView(msg);
                    tbl_of_msg.addView(row_of_msg);
                }
                tblayoutl.addView(tbl_of_msg);
            }
        }

    }
}
