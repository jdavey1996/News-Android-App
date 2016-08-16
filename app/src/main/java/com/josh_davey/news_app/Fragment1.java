package com.josh_davey.news_app;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Fragment1 extends Fragment{
    public Fragment1() {
        // Required empty public constructor
    }
Context ct = this.getContext();
    //Variables
    LocationManager locationManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Requesting permission to access device location.
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
    }

    @Override
    public void onStart() {
        super.onStart();

            final LocationManager mLocationManager = (LocationManager) getContext().getSystemService(getContext().LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);

            Button testb = (Button)getView().findViewById(R.id.TESTbtn);

            testb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        Location loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        String latestCity = getCity(loc);
                        if (latestCity.equals(null))
                        {
                            Log.i("Loc","err");
                        }
                        else
                        Log.i("lastest locc", latestCity);
                    }
                }
            });
        }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment1, container, false);

        final SwipeRefreshLayout sw = (SwipeRefreshLayout)view.findViewById(R.id.refreshLayout1);

        //Reloads data on swipe down.
        sw.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        //loadData();
                    }
                }
        );
        return view;
    }




    public void loadData(String location)
    {
        GetAllArticles getData = new GetAllArticles(getContext(),getActivity(),this);
        getData.execute(location);
    }


    public String getCity(Location location)
    {
        Geocoder geo = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
        List<Address> data;
        String city = null;
        try {
            data = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (data.size() > 0) {
                city = data.get(0).getLocality();
            }
        }
        catch (Exception e)
        {

        }
        return city;
    }
}


