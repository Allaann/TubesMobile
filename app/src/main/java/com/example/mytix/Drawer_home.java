package com.example.mytix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mytix.adapter.AlertDialogManager;
import com.example.mytix.session.SessionManager;
import com.google.android.material.navigation.NavigationView;

import com.smarteist.autoimageslider.SliderLayout;


public class Drawer_home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    SliderLayout sliderLayout;

    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_home);




        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawerOpen,R.string.drawerClose);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();

    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.setting:
                Toast.makeText(Drawer_home.this, "Setting Selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.contact:
                Toast.makeText(Drawer_home.this, "Contact Us Selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rate:
                Toast.makeText(Drawer_home.this, "Rate Us Selected", Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }

    public void btn_pswt(View view) {
        startActivity(new Intent(getApplicationContext(), Pesawat_page.class));
    }

    public void btn_krt(View view) {
        startActivity(new Intent(getApplicationContext(), Kereta_page.class));
    }

    public void btn_movie(View view) {
        Toast.makeText(getApplicationContext(), "Mohon maaf, sistem sedang dalam pengembangan.", Toast.LENGTH_LONG).show();
    }

    public void btn_holiday(View view) {
        startActivity(new Intent(getApplicationContext(), Holiday_page.class));
    }

    public void btn_profile(View view) {
        startActivity(new Intent(getApplicationContext(), Profile_page.class));
    }

    public void btn_about(View view) {
        startActivity(new Intent(getApplicationContext(), About.class));
    }
}
