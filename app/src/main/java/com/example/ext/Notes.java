package com.example.ext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import kotlin.Pair;

public class Notes extends AppCompatActivity {
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
        setContentView(R.layout.activity_notes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        new Notes.asyncEXT().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                try {
                    Ext ext = new Ext("Зайцев","3MA8|ZJQ{0");
                    //Ext ext = new Ext("Зайцева","<Cb0@4F9Sx");
                    //Ext ext = new Ext("Кудряшов","Ob7]NDz79+");
                    Pair<LocalDate, LocalDate> intervals = ext.GET_INTERVAL();
                    Log.e("pair", intervals.toString());
                    JSONArray journalData = ext.GET_STUDENT_JOURNAL_DATA();
                    Map<String, ArrayList> notes = new TreeMap();

                    for (int k = 0; k < journalData.length(); k++) {
                        String Date = Ext.GET_DATE(journalData.getJSONArray(k).getJSONArray(3));
                        String Subj = Ext.sbj_names.get(journalData.getJSONArray(k).getInt(4));
                        if (notes.get(Subj) == null){
                            notes.put(Subj, new ArrayList<>());
                        }
                        else{
                            LocalDate date = LocalDate.parse(Date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                            if (date.isAfter(intervals.component1()) && date.isBefore(intervals.component2())){
                                ArrayList<String> content = new ArrayList<>();
                                content.add(Date);
                                content.add(journalData.getJSONArray(k).getString(8));
                                content.add(journalData.getJSONArray(k).getString(2));
                                Objects.requireNonNull(notes.get(Subj)).add(content);
                            }
                        }
                    }
                    Log.e("notes", String.valueOf(notes));
                    return notes;
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

        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("ResourceAsColor")
        @Override
        protected void onPostExecute(Map<String, ArrayList> result) {
            super.onPostExecute(result);
            tblayoutl = (TableLayout) findViewById(R.id.tblayout);
            for (String subj: result.keySet()) {
                TableLayout tbl_of_subj = new TableLayout(Notes.this);
                TableRow row_of_subj_name = new TableRow(Notes.this);
                TableRow tbl_of_notes = new TableRow(Notes.this);

                TextView subj_name = new TextView(Notes.this);
                subj_name.setText(subj);
                subj_name.setTextSize(20);
                //subj_name.setTextColor(getResources().getColor(R.color.black));
                row_of_subj_name.addView(subj_name);

                ArrayList<ArrayList<String>> array = result.get(subj);
                for (int j = 0; j < array.size(); j++) {
                    TableRow row_of_note = new TableRow(Notes.this);
                    TextView note = new TextView(Notes.this);

                    TableRow.LayoutParams trLayoutParams = new TableRow.LayoutParams();
                    trLayoutParams.setMargins(7, 7, 7, 7);

                    note.setLayoutParams(trLayoutParams);
                    note.setTextSize(17);
                    note.setGravity(1);
                    note.setPadding(10, 10, 10, 10);

                    ShapeAppearanceModel shapeAppearanceModel = new ShapeAppearanceModel()
                            .toBuilder()
                            .setAllCorners(CornerFamily.ROUNDED, 18)
                            .build();
                    MaterialShapeDrawable shapeDrawable = new MaterialShapeDrawable(shapeAppearanceModel);
                    ViewCompat.setBackground(note, shapeDrawable);
                    shapeDrawable.setFillColor(ContextCompat.getColorStateList(Notes.this, R.color.white));
                    shapeDrawable.setStroke(2.0f, ContextCompat.getColor(Notes.this, R.color.white));

                    ShapeAppearanceModel shapeAppearanceModel2 = new ShapeAppearanceModel()
                            .toBuilder()
                            .setAllCorners(CornerFamily.ROUNDED, 20)
                            .build();
                    MaterialShapeDrawable shapeDrawable2 = new MaterialShapeDrawable(shapeAppearanceModel2);
                    ViewCompat.setBackground(row_of_note, shapeDrawable2);
                    TableRow.LayoutParams trRowParams = new TableRow.LayoutParams();
                    trRowParams.setMargins(4, 4, 4, 4);
                    row_of_note.setLayoutParams(trRowParams);
                    switch (array.get(j).get(2)) {
                        case ("4"):
                            shapeDrawable2.setFillColor(ContextCompat.getColorStateList(Notes.this, R.color.four));
                            shapeDrawable2.setStroke(2.0f, ContextCompat.getColor(Notes.this, R.color.four));
                            break;
                        case ("5"):
                            shapeDrawable2.setFillColor(ContextCompat.getColorStateList(Notes.this, R.color.five));
                            shapeDrawable2.setStroke(2.0f, ContextCompat.getColor(Notes.this, R.color.five));
                            break;
                        case ("3"):
                            shapeDrawable2.setFillColor(ContextCompat.getColorStateList(Notes.this, R.color.tree));
                            shapeDrawable2.setStroke(2.0f, ContextCompat.getColor(Notes.this, R.color.tree));
                            break;
                        case ("2"):
                            shapeDrawable2.setFillColor(ContextCompat.getColorStateList(Notes.this, R.color.two));
                            shapeDrawable2.setStroke(2.0f, ContextCompat.getColor(Notes.this, R.color.two));
                            break;
                        default:
                            shapeDrawable2.setFillColor(ContextCompat.getColorStateList(Notes.this, R.color.text));
                            shapeDrawable2.setStroke(2.0f, ContextCompat.getColor(Notes.this, R.color.text));
                            break;
                    }
                    note.setText(array.get(j).get(2) + " ");
                    row_of_note.addView(note);
                    tbl_of_notes.addView(row_of_note);
                }
                TextView average_note = new TextView(Notes.this);
                average_note.setText("5.00");
                TableRow.LayoutParams trLayoutParams = new TableRow.LayoutParams();
                trLayoutParams.setMargins(7, 7, 7, 7);
                average_note.setLayoutParams(trLayoutParams);
                average_note.setTextSize(17);
                average_note.setGravity(Gravity.CENTER);
                average_note.setPadding(10, 10, 10, 10);
                average_note.setBackgroundColor(Color.WHITE);
                tbl_of_notes.addView(average_note);

                // x

                tbl_of_subj.addView(tbl_of_notes);
                tblayoutl.addView(row_of_subj_name);
                tblayoutl.addView(tbl_of_subj);
            }
        }
    }

}
