package com.josh_davey.news_app;

import android.Manifest;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Requesting permission to access device location.
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager = (LocationManager) getContext().getSystemService(getContext().LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        }

    }


    @Override
    public void onStart() {
        super.onStart();
        loadData();
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

        sw = (SwipeRefreshLayout)view.findViewById(R.id.refreshLayout1);

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




    public void loadData()
    {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Location loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            String latestCity = getCity(loc);
            if (latestCity.equals("null")) {
                Log.i("Loc", "err");
                Toast.makeText(getContext(), "ERROR", Toast.LENGTH_SHORT).show();
                sw.setRefreshing(false);
            }
            else
            {
                Log.i("lastest locc", latestCity);
                Toast.makeText(getContext(), latestCity, Toast.LENGTH_SHORT).show();
                GetArticles getData = new GetArticles(getContext(),getActivity(),this);
                getData.execute(latestCity);
            }
        }
    }


    public String getCity(Location location)
    {
        Geocoder geo = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
        List<Address> data;
        String city = "null";
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


