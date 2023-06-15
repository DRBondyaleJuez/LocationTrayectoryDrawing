package controller.locationController;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class LocationController implements LocationControllerObservable {

    private static final int INTERVAL_MILLIS = 100;
    private static final int REQUEST_CHECK_CODE = 1001;
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

        //LOCATION ACTIVE SETTINGS REQUEST  - https://www.youtube.com/watch?v=dPqivAUK8ps
        LocationSettingsRequest locationSettingRequest = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .build();

        SettingsClient settingsCLient = LocationServices.getSettingsClient(activity);
        settingsCLient.checkLocationSettings(locationSettingRequest).addOnCompleteListener(
                new OnCompleteListener<LocationSettingsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                        if(task.isSuccessful()){

                        } else {
                            if(task.getException() instanceof ResolvableApiException){
                                try {
                                    ResolvableApiException resolvableApiException = (ResolvableApiException) task.getException();
                                    resolvableApiException.startResolutionForResult(activity,REQUEST_CHECK_CODE); //When the GPS is not active the previous exception detects this and a message to activate it is displayed
                                } catch (IntentSender.SendIntentException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                }
        );

        //PERMISSION CHECK
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

    //https://www.youtube.com/watch?v=dPqivAUK8ps
    private LocationRequest createLocationRequest() {
        return (new LocationRequest.Builder(
                INTERVAL_MILLIS
        ).setGranularity(Granularity.GRANULARITY_FINE)
         .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
         .build());
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
            observer.calculateAndSetDirection(lat,lon);
        }
    }

    @Override
    public void addObservers(LocationControllerObserver observer) {
        observers.add(observer);
    }
}
