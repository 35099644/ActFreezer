package com.netlab.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.netlab.actfreezer.R;
import com.netlab.servicelogger.ServiceLogger;
import com.netlab.util.GlobalSettings;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        System.out.println(item.getTitle());
        return false;
    }

    private void listInstalledPkg()
    {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> pkgList = pm.getInstalledApplications(0);

        /**
         * List package info for all installed pkgs
         */
        String[] pkg_array = new String[pkgList.size()];
        int[] uid_array = new int[pkgList.size()];
        int index = 0;
        for(ApplicationInfo info : pkgList)
        {
            pkg_array[index] =  info.packageName;
            uid_array[index] =  info.uid;
            index ++;
        }

        /**
         * print them to console
         */
        for(String pkg : pkg_array)
        {
            System.out.println(",\""+pkg+"\"");
        }
        for(int uid: uid_array)
        {
            System.out.println(",\""+uid+"\"");
        }
    }



}







//        startServiceButton = (Button) findViewById(R.id.startServiceButton);
//
//        startServiceButton.setOnClickListener(new View.OnClickListener() {
//                                                  @Override
//                                                  public void onClick(View view) {
//                                                      startService(new Intent(MainActivity.this, ServiceLogger.class));
//                                                  }
//                                              }
//        );


//        sharedPreferences = getSharedPreferences("actfreezer", Context.MODE_WORLD_READABLE);
//
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putBoolean("actfreezer", GlobalSettings.getConf());
//        editor.commit();

//        confButton = (ToggleButton) findViewById(R.id.ConfToggleButton);
//        //System.err.println(GlobalSettings.getConf());
//        confButton.setChecked(GlobalSettings.getConf());
//        confButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
//                if (checked) {
//                    Log.d(TAG, "" + checked);
//                    GlobalSettings.setConf(checked);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putBoolean("actfreezer", checked);
//                    editor.commit();
//
//                    Intent intent = new Intent();
//                    intent.setAction("ActfreeezerData");
//                    intent.putExtra("configuration", ""+checked);
//                    sendBroadcast(intent);
//                } else {
//                    Log.d(TAG, "" + checked);
//                    GlobalSettings.setConf(checked);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("actfreezer", ""+checked);
//                    editor.commit();
//
//                    Intent intent = new Intent();
//                    intent.setAction("ActfreeezerData");
//                    intent.putExtra("configuration", ""+ checked);
//
//                    sendBroadcast(intent);
//                }
//            }
//        });
//startService(new Intent(this, ServiceLogger.class));