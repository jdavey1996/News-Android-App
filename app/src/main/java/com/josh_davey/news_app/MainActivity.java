package com.josh_davey.news_app;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewDebug;
import android.widget.FrameLayout;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    LocationUpdates loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Listens for when the actionbar back button is pressed and closes fragment4.
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFrag4(null);
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Local"));
        tabLayout.addTab(tabLayout.newTab().setText("Most Viewed"));
        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(2);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void closeFrag4(View view)
    {
        //Makes the frame invisible containing fragment 4.
        FrameLayout frame = (FrameLayout)findViewById(R.id.framefrag);
        frame.setVisibility(View.GONE);

        //Makes the tabbed layout visible again to allow navigation along tabs.
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.setVisibility(View.VISIBLE);

        //Makes the action bar close button invisible when closing fragment 4.
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }


    @Override
    protected void onStart() {
        super.onStart();
        //Handler thread to manage location updates continuously. **Required due to frame skipping if timeout occurred in slow connection downloading data via async.
        HandlerThread handlerThread = new HandlerThread("locationThread");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();

        //Initialise location updates class using the looper.
        loc = new LocationUpdates(this,this,looper);

        //Start location updates
        loc.initiateLocationServices();

        //If location permissions are already enabled, begin downloading data. If they're not, load later when ANY response is given to location permissions request.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            downloadData();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        //Stop location updates
        loc.stopLocationUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Stop location updates
        loc.stopLocationUpdates();
    }

    //Runs when the user responds to permission requests.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode)
        {
            //Tasks to execute depending on the response from a request to access location data.
            case 1:
                //If the user grants the location permission, start google api.
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //Startlocation updates
                    loc.initiateLocationServices();
                }
                else
                {
                    //If permission is not granted, error message is displayed.
                    Toast.makeText(this, "Location permissions are disabled. Please enable to view by location.", Toast.LENGTH_SHORT).show();;
                }

                //Download all data once response is given.
                downloadData();
        }
    }

    public void downloadData()
    {
        //Get current location from shared preferences. If not stored, value is null.
        SharedPreferences prefs = getSharedPreferences("News-App-Location", MODE_PRIVATE);
        String currentLocation = prefs.getString("currentLocation", null);

        //Load data.
        GetArticles getData = new GetArticles(this, this);
        getData.execute("loadall", currentLocation);
    }

    @Override
    public void onBackPressed() {
        //If frame layout containing fragment 4 is visible, back pressed runs the closeFrag4 method, making it invisible again.
        FrameLayout frame = (FrameLayout)findViewById(R.id.framefrag);
        if(frame.getVisibility() == View.VISIBLE)
        {
            closeFrag4(null);
        }
        //Else back pressed runs as normal.
        else
        {
            super.onBackPressed();
        }
    }
}
