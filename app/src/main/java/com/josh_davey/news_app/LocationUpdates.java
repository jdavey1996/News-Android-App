package com.josh_davey.news_app;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Locale;


public class LocationUpdates implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{

    Context ctx;
    Activity activity;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    GoogleApiAvailability availabilityInstance = GoogleApiAvailability.getInstance();
    String currentLocation = null;
    Looper looper;

    public LocationUpdates(Context ctx, Activity activity, Looper looper)
    {
        this.ctx = ctx;
        this.activity = activity;
        this.looper = looper;
    }

    public void initiateLocationServices() {
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //If permission is granted, begin location.
            startGoogleApi();
        } else {
            //Requesting permission to access device location (Only if not granted in SDK 23. Automatically granted via manifest in SDK 22 and below).
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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

    //Build google api client for location services api. Return true if successful.
    protected synchronized boolean buildGoogleApiClient() {
        try {
            googleApiClient = new GoogleApiClient.Builder(ctx)
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
        int resultCode = availabilityInstance.isGooglePlayServicesAvailable(ctx);

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
                availabilityInstance.getErrorDialog(activity, resultCode, 1).show();
            }
        }
    }

    //Defines details of location data to be requested.
    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(0);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void startLocationUpdates()
    {
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this, looper);
        }
        else {
            //Requesting permission to access device location.
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    public void stopLocationUpdates()
    {
        //If the googleApiClient is connected, stop location updates and disconnect (Save battery).
        if (googleApiClient != null && googleApiClient.isConnected())
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    //Callback and listener methods:
    @Override
    public void onLocationChanged(Location location) {
        // Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
        getCity(location);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(activity, "Unable to connect to location services, please try again later.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnected(Bundle bundle) {
        createLocationRequest();
        startLocationUpdates();
    }

    public void getCity(Location location)
    {
        try {
            Geocoder geo = new Geocoder(ctx, Locale.getDefault());
            List<Address> data;
            String city = "null";
            data = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (data.size() > 0) {
                city = data.get(0).getLocality();
                if (city.equals(currentLocation))
                {
                    //No location change.
                    Toast.makeText(activity, "No location change", Toast.LENGTH_SHORT).show();
                }
                else if (city.equals("null"))
                {
                    //No city data.
                    Toast.makeText(activity, "No city data", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //Location has changed. Load data again.
                    currentLocation = city;
                    Toast.makeText(activity, currentLocation, Toast.LENGTH_SHORT).show();
                    //loadData(currentLocation);
                }
            }
        }
        catch (Exception e)
        {
        }
    }


    public boolean isLocationEnabled() {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(ctx.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public boolean isAirplaneModeOn() {
        return Settings.System.getInt(ctx.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }
}
