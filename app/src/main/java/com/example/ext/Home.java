package com.example.ext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.security.NoSuchAlgorithmException;

public class Home extends AppCompatActivity {
    TableLayout tablelayout;
    TableLayout row_of_subj;
    TableRow row_of_date;
    String str_to_tb;
    TextView UCH_YEAR_TX;
    StringBuilder UCH_YEAR = new StringBuilder();
    TextView textView1;
    TextView textView2;
    TableLayout row_of_day;
    NavigationView navigationView;
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        int j;
        int days_of_weak = 2;


        tablelayout = (TableLayout) findViewById(R.id.tablelayout);
        String[] sourceArray = {"French ", "Russich ", "English "};
        String homework = "lalallllalalalalalallalalalal";
        for (int i = 0; i < days_of_weak; i++) {
            row_of_date = new TableRow(this);
            UCH_YEAR_TX = new TextView(this);
            UCH_YEAR_TX.setText(UCH_YEAR);
            row_of_date.addView(UCH_YEAR_TX);
            row_of_date.setBackgroundColor(R.color.teal_200);

            tablelayout.addView(row_of_date);
            j = 1;
            row_of_day = new TableLayout(this);
            for(String subject: sourceArray) {
                row_of_subj = new TableLayout(this);
                str_to_tb = j + " " + subject;
                textView1 = new TextView(this);
                textView1.setText(str_to_tb);
                textView1.setTypeface(null, Typeface.BOLD);
                textView1.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
                textView1.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f));

                textView2 = new TextView(this);
                textView2.setVisibility(View.GONE);
                textView2.setText(homework);
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
            tablelayout.addView(row_of_day);
        }
    }
 @SuppressLint("StaticFieldLeak")
    private class asyncEXT extends AsyncTask<Void, Void, Void> {
        private void UCH_YEAR() {
            try {
                Ext ext = new Ext("Зайцев","3MA8|ZJQ{0");
                UCH_YEAR.append(ext.UCH_YEAR());
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
        }
    }
    @SuppressLint("NonConstantResourceId")
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_main:{
                Intent mainIntent = new Intent(Home.this, Home.class);
                startActivity(mainIntent);
                break;
            }
            case R.id.nav_notes: {
                Intent mainIntent = new Intent(Home.this, Notes.class);
                startActivity(mainIntent);
                break;
            }
            case R.id.nav_diary: {
                Intent mainIntent = new Intent(Home.this, Diary.class);
                startActivity(mainIntent);
                break;
            }
            case R.id.nav_messages:{
                Intent mainIntent = new Intent(Home.this, Messages.class);
                startActivity(mainIntent);
                break;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
