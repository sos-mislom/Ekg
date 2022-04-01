package com.example.ext;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Diary extends Activity {
    private NavigationView navigationView;
    private TableLayout tblayoutl;
    private String nameOfLesson;
    private String dairyData;
    private Map<String, ArrayList<String>> jsondairy = new HashMap<>();

    public static JSONObject serialize(Map<String, ArrayList<String>> map) throws JSONException {
        JSONObject json = new JSONObject();
        for(Map.Entry<String, ArrayList<String> > entry : map.entrySet()) {
            json.put(entry.getKey(), new JSONArray(entry.getValue()));
        }
        return json;
    }
    private ArrayList<View> getAllChildren(View v) {

        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<View>();

        ViewGroup viewGroup = (ViewGroup) v;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {

            View child = viewGroup.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            viewArrayList.addAll(getAllChildren(child));

            result.addAll(viewArrayList);
        }
        return result;
    }
    public static Map<String, ArrayList<String>> deserialize(JSONObject json) throws JSONException {
        Map<String, ArrayList<String> > map = new HashMap<>();
        for (Iterator<String> it = json.keys(); it.hasNext(); ) {
            String key = it.next();
            JSONArray value = json.getJSONArray(key);
            ArrayList<String> list = new ArrayList<>();

            for(int i = 0; i < value.length(); i++) {
                list.add(value.getString(i));
            }
            map.put(key, list);
        }
        return map;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"ResourceAsColor", "SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.setDrawerListener(toggle);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Drawable p = getResources().getDrawable(R.drawable.plus);
        Bitmap bitmap = ((BitmapDrawable) p).getBitmap();
        Drawable plus = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 20, 20, true));

        Drawable t = getResources().getDrawable(R.drawable.trash);
        Bitmap bitmap1 = ((BitmapDrawable) t).getBitmap();
        Drawable trash = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap1, 20, 20, true));

        Drawable c = getResources().getDrawable(R.drawable.checkmark);
        Bitmap bitmap2 = ((BitmapDrawable) c).getBitmap();
        Drawable checkmark = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap2, 20, 20, true));


        //new asyncEXT().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Diary.this);
        dairyData = preferences.getString("dairyData", "");
        //preferences.edit().remove("dairyData").commit();
        if (dairyData.length() > 0) {
            try {
                jsondairy = deserialize(ProcJSON.decode(dairyData));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else{
            jsondairy.put("Понедельник", new ArrayList<>());
            jsondairy.put("Вторник", new ArrayList<>());
            jsondairy.put("Среда", new ArrayList<>());
            jsondairy.put("Четверг", new ArrayList<>());
            jsondairy.put("Пятница", new ArrayList<>());
            jsondairy.put("Суббота", new ArrayList<>());
            jsondairy.put("Воскресенье", new ArrayList<>());
        }

        ArrayList<String> days = new ArrayList<>();
        days.add("Понедельник");days.add("Вторник");days.add("Среда");days.add("Четверг");
        days.add("Пятница");days.add("Суббота");days.add("Воскресенье");
        tblayoutl = (TableLayout) findViewById(R.id.tblayout);

        for (int i = 0; i < jsondairy.size(); i++) {
            TableLayout tbl = new TableLayout(Diary.this);
            TableLayout tblForNewLesson = new TableLayout(Diary.this);
            TableRow tx_row = new TableRow(Diary.this);
            TextView tx = new TextView(Diary.this);
            //tx.setText(days.get(i) + " " + jsondairy.get(days.get(i)));
            tx.setText(days.get(i));
            tx.setPadding(5,5,0,0);
            tx.setTextSize(20);
            tx.setTextColor(Color.WHITE);
            tx_row.setBackgroundColor(ContextCompat.getColor(Diary.this, R.color.divider_color2));
            tx_row.addView(tx);
            tbl.addView(tx_row);
            ImageButton btn = new ImageButton(Diary.this);
            btn.setId(i);
            btn.setImageDrawable(plus);
            tx_row.addView(btn);
            btn.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("WrongConstant")
                public void onClick(View view) {
                    int last = jsondairy.get(days.get(btn.getId())).size();
                    if (last <= 8){
                        TableRow new_lesson = new TableRow(Diary.this);
                        ImageButton btn2 = new ImageButton(Diary.this);
                        ImageButton btn1 = new ImageButton(Diary.this);
                        nameOfLesson = last + ". ОКНО";

                        EditText ll = new EditText(Diary.this);
                        ll.setText(nameOfLesson);

                        jsondairy.get(days.get(btn.getId())).add("" + ll.getText());
                        ll.setId(last);
                        new_lesson.addView(ll);

                        ll.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                btn2.setVisibility(View.VISIBLE);
                                btn1.setVisibility(View.GONE);
                            }
                            @Override
                            public void afterTextChanged(Editable s) { }
                        });

                        //tx.setText(days.get(btn.getId()) + " " + jsondairy.get(days.get(btn.getId())));

                        btn1.setId(last);
                        btn1.setImageDrawable(trash);
                        new_lesson.addView(btn1);
                        new_lesson.setId(last);
                        btn1.setOnClickListener(new View.OnClickListener() {
                            @SuppressLint("ResourceType")
                            public void onClick(View view) {
                                ((ViewGroup) new_lesson.getParent()).removeView(new_lesson);
                                jsondairy.get(days.get(btn.getId())).remove(btn1.getId());
                                if (btn1.getId() != jsondairy.get(days.get(btn.getId())).size()) {
                                    for (View i : getAllChildren(tblForNewLesson)) {
                                        if (i.getId() > 0) {
                                            if (i instanceof ImageButton) {
                                                i.setId(i.getId()-1);
                                            }
                                            if (i instanceof EditText) {
                                                i.setId(i.getId()-1);
                                                int lenght = ((EditText) i).getText().length();
                                                ((EditText) i).setText(i.getId() +"." +((EditText) i).getText().subSequence(2, lenght));
                                            }
                                        }
                                    }
                                }
                                //tx.setText(days.get(btn.getId()) + " " + jsondairy.get(days.get(btn.getId())));
                            }
                        });

                        btn2.setId(last);
                        btn2.setImageDrawable(checkmark);
                        new_lesson.addView(btn2);
                        new_lesson.setId(last);
                        btn2.setVisibility(View.GONE);
                        btn2.setOnClickListener(new View.OnClickListener() {
                            @SuppressLint("ResourceType")
                            public void onClick(View view) {
                                jsondairy.get(days.get(btn.getId())).set(btn2.getId(), "" + ll.getText());
                                nameOfLesson = String.valueOf(ll.getText());
                                btn2.setVisibility(View.GONE);
                                btn1.setVisibility(View.VISIBLE);
                                //tx.setText(days.get(btn.getId()) + " " + jsondairy.get(days.get(btn.getId())));
                            }
                        });
                        tblForNewLesson.addView(new_lesson);
                    } else{
                        Toast.makeText(view.getContext(),
                                "Toooo many lessons", Toast.LENGTH_SHORT)
                                .show();
                    }

                }
            });

            for (int l = 0; l < jsondairy.get(days.get(i)).size(); l++) {
                TableRow new_lesson = new TableRow(Diary.this);
                ImageButton btn2 = new ImageButton(Diary.this);
                ImageButton btn1 = new ImageButton(Diary.this);

                String subject = jsondairy.get(days.get(btn.getId())).get(l);
                EditText ll = new EditText(Diary.this);
                ll.setText(subject);
                new_lesson.addView(ll);
                ll.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        btn2.setVisibility(View.VISIBLE);
                        btn1.setVisibility(View.GONE);
                    }
                    @Override
                    public void afterTextChanged(Editable s) { }
                });

                //tx.setText(days.get(btn.getId()) + " " + jsondairy.get(days.get(btn.getId())));
                btn1.setId(l);
                btn1.setImageDrawable(trash);
                new_lesson.addView(btn1);
                new_lesson.setId(l);
                btn1.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ResourceType")
                    public void onClick(View view) {
                        ((ViewGroup) new_lesson.getParent()).removeView(new_lesson);
                        jsondairy.get(days.get(btn.getId())).remove(subject);
                        if (btn1.getId() != jsondairy.get(days.get(btn.getId())).size()) {
                            for (View i : getAllChildren(tblForNewLesson)) {
                                if (i.getId() > 0) {
                                    if (i instanceof ImageButton) {
                                        i.setId(i.getId()-1);
                                    }
                                }
                            }
                        }
                        //tx.setText(days.get(btn.getId()) + " " + jsondairy.get(days.get(btn.getId())));
                    }
                });


                btn2.setId(l);
                btn2.setImageDrawable(checkmark);
                new_lesson.addView(btn2);
                new_lesson.setId(l);
                btn2.setVisibility(View.GONE);
                btn2.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        jsondairy.get(days.get(btn.getId())).set(btn2.getId(), "" + ll.getText());
                        btn2.setVisibility(View.GONE);
                        btn1.setVisibility(View.VISIBLE);
                        //tx.setText(days.get(btn.getId()) + " " + jsondairy.get(days.get(btn.getId())));
                    }
                });
                tblForNewLesson.addView(new_lesson);
            }
            tbl.addView(tblForNewLesson);
            tblayoutl.addView(tbl);
        }
        ImageButton btn3 = new ImageButton(this);
        btn3.setImageDrawable(checkmark);
        btn3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                            .putString("dairyData", serialize(jsondairy).toString())
                            .apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        tblayoutl.addView(btn3);
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
}
