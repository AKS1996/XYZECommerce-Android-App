package com.xyzecommerce;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;


import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Map;
import java.util.Set;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Button mLoginButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         sharedPreferences=getSharedPreferences("hello", Context.MODE_PRIVATE);

        LoginActivity.facebookloggedIn=sharedPreferences.getBoolean("loggedIn",false);

        setContentView(R.layout.activity_main);
        mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mLoginButton=(Button)findViewById(R.id.login_activity_button);
        navigationView.setNavigationItemSelectedListener(this);
        //toggle
        mDrawerToggle=new ActionBarDrawerToggle(this,mDrawerLayout,mToolbar,R.string.open_drawer_fallback,R.string.close_drawer_fallback)

        {
            // Called when drawer has settled in completely closed state
            @Override
            public void onDrawerClosed(View view)
            {
                super.onDrawerClosed(view);
            }
            //Called when drawer has settled in completely opened state
            @Override
            public void onDrawerOpened(View view)
            {
                super.onDrawerOpened(view);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(LoginActivity.facebookloggedIn){
                     intent=new Intent(getBaseContext(),DetailsActivity.class);
                }else{
                    intent=new Intent(getBaseContext(),LoginActivity.class);
                }
                startActivity(intent);
            }
        });
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();

    }
    @Override
    public void onConfigurationChanged(Configuration configuration )
    {
        super.onConfigurationChanged(configuration);
        mDrawerToggle.onConfigurationChanged(configuration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu;
        // this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_apply_now:break;
            case R.id.nav_offers:break;
            case R.id.nav_near_me:
                Intent mapIntent=new Intent(this,MapsActivity.class);
                startActivity(mapIntent);

                break;
            case R.id.nav_support:break;
            case R.id.nav_faq:break;
            case R.id.nav_feedback:break;
            case R.id.nav_downloads:
                startActivity((new Intent(MainActivity.this,generic_download.class)).putExtra("message","Common Documents"));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("loggedIn",LoginActivity.facebookloggedIn);

        editor.apply();
    }
}
