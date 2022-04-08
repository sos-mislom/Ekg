package com.example.ext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class SettingsActivity extends AppCompatActivity {
    NavigationView navigationView;

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
    }
    @SuppressLint("NonConstantResourceId")
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_main:{
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
                break;
            }
            case R.id.nav_notes: {
                Intent mainIntent = new Intent(this, NotesActivity.class);
                startActivity(mainIntent);
                break;
            }
            case R.id.nav_diary: {
                Intent mainIntent = new Intent(this, DiaryActivity.class);
                startActivity(mainIntent);
                break;
            }
            case R.id.nav_messages:{
                Intent mainIntent = new Intent(this, MessagesActivity.class);
                startActivity(mainIntent);
                break;
            }
            case R.id.nav_settings:{
                Intent mainIntent = new Intent(this, SettingsActivity.class);
                startActivity(mainIntent);
                break;
            }
            case R.id.nav_exit:{
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                        .putString("password", "")
                        .putString("username", "")
                        .apply();
                Intent mainIntent = new Intent(this, LoginActivity.class);
                startActivity(mainIntent);
                finish();
                break;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}