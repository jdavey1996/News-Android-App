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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;

import java.util.List;
import java.util.Locale;

public class Fragment1 extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{


    SwipeRefreshLayout sw;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    GoogleApiAvailability availabilityInstance = GoogleApiAvailability.getInstance();
    String currentLocation = null;
    LocationSettingsRequest locationSettingsRequest;

    @Override
    public void onStart() {
        super.onStart();
        startGoogleApi();
    }

    @Override
    public void onPause() {
        super.onPause();
        //If the googleApiClient is connected, stop location updates and disconnect (Save battery).
        if (googleApiClient.isConnected())
        {
            stopLocationUpdates();
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //If the googleApiClient is connected, stop location updates and disconnect (Save battery).
        if (googleApiClient.isConnected()) {
            stopLocationUpdates();
            googleApiClient.disconnect();
        }
    }


    public void startGoogleApi()
    {
        //Check if the googleApiClient has already been built. If so check run googlePlayServicesAvailability method to check availability.
        if (googleApiClient!= null)
        {
            googlePlayServicesAvailability();
        }
        //If not, build the googleApiClient, if successful (returned true), then run googlePlayServicesAvailability method to check availability.
        else {
            if (buildGoogleApiClient() == true) {
                googlePlayServicesAvailability();
            }
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

                    Log.i("Location permissions","Granted");

                    startGoogleApi();
                }
                else
                {
                    Log.i("Location permissions","Not granted");
                    //If permission is not granted, error message is displayed.
                    Toast.makeText(getContext(), "Location permissions are disabled. Please enable to view by location.", Toast.LENGTH_SHORT).show();;
                }
        }
    }


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
                            //Reload data.
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

    //Build google api client for location services api. Return true if successful.
    protected synchronized boolean buildGoogleApiClient() {
        try {
            googleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            return true;
        }catch (Exception e)
        {
            return false;
        }
    }

    public void googlePlayServicesAvailability()
    {
        //Gets google play services availability.
        int resultCode = availabilityInstance.isGooglePlayServicesAvailable(getContext());

        if (resultCode == ConnectionResult.SUCCESS)
        {
            //If the result code is success, check if the api client is connecting or connected. If it isn't then connect.
            if (!googleApiClient.isConnecting()== true && !googleApiClient.isConnected() == true) {
                googleApiClient.connect();
            }
        }
        else
        {
            //If the result code isn't success, display resolvable error dialog if it is a resolvable case.
            if (availabilityInstance.isUserResolvableError(resultCode)){
                availabilityInstance.getErrorDialog(getActivity(), resultCode, 1).show();
            }
        }
    }

    //Defines details of location data to be requested.
    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(50);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void startLocationUpdates()
    {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
        else {
            //Requesting permission to access device location.
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    public void stopLocationUpdates()
    {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }



    //Callback and listener methods:
    @Override
    public void onLocationChanged(Location location) {
       // Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
        getCity(location);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getContext(), "Failed to connect", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getContext(), "Suspended connection", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(getContext(), "Connected", Toast.LENGTH_SHORT).show();
        createLocationRequest();
        startLocationUpdates();
    }


    public void getCity(Location location)
    {
        try {
            Geocoder geo = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
            List<Address> data;
            String city = "null";
            data = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (data.size() > 0) {
                city = data.get(0).getLocality();
                if (city.equals(currentLocation))
                {
                    //No location change.
                    Toast.makeText(getContext(), "No location change", Toast.LENGTH_SHORT).show();
                }
                else if (city.equals("null"))
                {
                    //No city data.
                    Toast.makeText(getContext(), "No city data", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //Location has changed. Load data again.
                    currentLocation = city;
                    Toast.makeText(getContext(), "Location changed", Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch (Exception e)
        {
        }
    }


}