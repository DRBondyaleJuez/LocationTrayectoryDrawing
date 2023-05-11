package controller;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class LocationController implements LocationControllerObservable {

    private static final int INTERVAL_MILLIS = 10000;
    private final Activity activity;
    private final LocationRequest locationRequest;
    private final LocationCallback locationCallback;
    private final FusedLocationProviderClient fusedLocationProviderClient;

    private ArrayList<LocationControllerObserver> observers;


    public LocationController(Activity activity) {
        this.activity = activity;
        locationRequest = createLocationRequest();
        locationCallback = createLocationCallback();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        observers = new ArrayList<>();
    }

    public void updateGPS() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
                if(locationTask != null) {
                    locationTask.addOnSuccessListener(activity, setOnSuccessListener());
                }

            } else {
                //Permission not granted yet.
                ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, 100);
            }
        }
    }

    public void startContinuousLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    public void stopContinuousLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private LocationRequest createLocationRequest() {
        return (new LocationRequest.Builder(
                INTERVAL_MILLIS
        ).build());
    }

    private LocationCallback createLocationCallback() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location location = locationResult.getLastLocation();
                if (location == null) {
                    // TODO: Log!
                } else {
                    updateLocationInfo(location);
                }
            }
        };
    }

    private OnSuccessListener<Location> setOnSuccessListener() {
        return new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                //TODO: Send location values to controller or store them or something
                updateLocationInfo(location);
            }
        };
    }

    private void updateLocationInfo(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        location.getAltitude();
        Double alt = null;
        if (location.hasAltitude()) {
            alt = location.getAltitude();
        }
        Float speed = null;
        if (location.hasSpeed()) {
            speed = location.getSpeed();
        }

        //CALLING OBSERVERS
        for (LocationControllerObserver observer : observers) {
            observer.setLocationParameters(lat, lon, alt, speed);
        }
    }

    @Override
    public void addObservers(LocationControllerObserver observer) {
        observers.add(observer);
    }
}
