package com.josh_davey.news_app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class Fragment1 extends Fragment{
    public Fragment1() {
        // Required empty public constructor
    }

    SwipeRefreshLayout sw;
    LocationManager mLocationManager;

    @Override
    public void onStart() {
        super.onStart();
        /*Everytime the app is started (including closed and reopened), it checks if permissions for location are granted.
          If so it requests location updates and runs the load data method.
          If not, it requests permissions from the user.*/
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager = (LocationManager) getContext().getSystemService(getContext().LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
            loadData();
        } else {
            //Requesting permission to access device location.
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //When the app is paused (eg, returning to home screen without closing), location updates are removed to save battery.
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }

    //Runs when the user responds to permission requests.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode)
        {
            //Tasks to execute depending on the response from a request to access location data.
            case 1:
                //If the user grants the location permission, request location updates and load data.
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mLocationManager = (LocationManager) getContext().getSystemService(getContext().LOCATION_SERVICE);
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
                    loadData();
                    Log.i("Location permissions","Granted");
                }
                else
                {
                    Log.i("Location permissions","Not granted");
                    //If permission is not granted, error message is displayed.
                    Toast.makeText(getContext(), "Location permissions are disabled. Please enable to view by location.", Toast.LENGTH_SHORT).show();;
                }
        }
    }

    //Listener for location updates.
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment1, container, false);

        //Initialises swipe refresh layout 1 for fragment1.
        sw = (SwipeRefreshLayout)view.findViewById(R.id.refreshLayout1);

        //Reloads data on swipe down.
        sw.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        //If permission is granted, load data. Else request permission.
                        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            loadData();
                        }
                        else
                        {
                            //Cancel refresh animation.
                            sw.setRefreshing(false);
                            //Requesting permission to access device location.
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                        }
                    }
                }
        );
        return view;
    }

    public void loadData()
    {
        try {

            //Gets the last known location.
            Location loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            //Runs the last known location through the getCity method to return a city string.
            String latestCity = getCity(loc);
            //If the city returned is null, error.
            //If not, execute the getArticles asynctask, passing the city string as an argument to get the correct data.
            if (latestCity == null) {
                sw.setRefreshing(false);
                Toast.makeText(getContext(), "Unable to acquire your location, check your settings and try again later.", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(getContext(), latestCity, Toast.LENGTH_SHORT).show();
                GetArticles getData = new GetArticles(getContext(), getActivity(), this);
                getData.execute(latestCity);
            }
        }
        catch (SecurityException e)
        {
        }
    }

    //Uses a location, running it through a geocoder to get the information behind the lat and long values.
    //This then gets the city from that information and returns it.
    public String getCity(Location location)
    {
        try {
            Geocoder geo = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
            List<Address> data;
            String city = "null";
            try {
                data = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (data.size() > 0) {
                    city = data.get(0).getLocality();
                }
            } catch (Exception e) {
            }
            return city;
        }
        catch (Exception e)
        {
            return null;
        }
    }
}