package com.josh_davey.news_app;

import android.Manifest;
import android.content.Context;
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

        loadData();
    }

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
                        loadData();
                    }
                }
        );
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            locationManager= (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            //Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Boolean isGPSEnabled;
            Boolean isNetworkEnabled;

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Log.i("GPS Status", String.valueOf(isGPSEnabled));
            Log.i("Network Status", String.valueOf(isNetworkEnabled));
        }
    }

    public boolean checkPermissionGranted()
    {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean checkLocationProviderEnabled()
    {
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)== true || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void loadData()
    {
        GetAllArticles getData = new GetAllArticles(getContext(),getActivity(),this);
        getData.execute("lincoln");
    }
}

