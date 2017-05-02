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

public class MainActivity extends AppCompatActivity  implements PlanetAdapter.OnItemClickListener{


    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;
    private ActionBarDrawerToggle mDrawerToggle;
   private Toolbar mToolbar;


    private Button startServiceButton = null;
    private ToggleButton confButton = null;

    private final String TAG = "MainActivity";


    private SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        //左上角图标可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (RecyclerView) findViewById(R.id.left_drawer);

        mDrawerList.setHasFixedSize(true);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new PlanetAdapter(mPlanetTitles, this));
        // enable ActionBar app icon to behave as action to toggle nav drawer




        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
//                this,                  /* host Activity */
//                mDrawerLayout,         /* DrawerLayout object */
//                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
//                R.string.drawer_open,  /* "open drawer" description for accessibility */
//                R.string.drawer_close  /* "close drawer" description for accessibility */

       // mDrawerToggle.setHomeAsUpIndicator(getDrawable(R.drawable.ic_drawer));
        //mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_drawer);
        mDrawerToggle.setDrawerArrowDrawable(new DrawerArrowDrawable(this));


        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }
            @Override
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        if (savedInstanceState == null) {
            selectItem(0);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.action_websearch:
                // create intent to perform web search for this planet
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, getSupportActionBar().getTitle());
                // catch event that there's no activity to handle intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    /* The click listener for RecyclerView in the navigation drawer */
    @Override
    public void onClick(View view, int position) {
        selectItem(position);
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = PlanetFragment.newInstance(position);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();

        // update selected item title, then close the drawer
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }



    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
    public static class PlanetFragment extends Fragment {
        public static final String ARG_PLANET_NUMBER = "planet_number";

        public PlanetFragment() {
            // Empty constructor required for fragment subclasses
        }

        public static Fragment newInstance(int position) {
            Fragment fragment = new PlanetFragment();
            Bundle args = new Bundle();
            args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_planet, container, false);
            int i = getArguments().getInt(ARG_PLANET_NUMBER);
            String planet = getResources().getStringArray(R.array.planets_array)[i];

            int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
                    "drawable", getActivity().getPackageName());
            ImageView iv = ((ImageView) rootView.findViewById(R.id.image));
            iv.setImageResource(imageId);

            getActivity().setTitle(planet);
            return rootView;
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