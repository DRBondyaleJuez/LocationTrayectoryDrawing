package controller.locationController;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;

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

/**
 * Provides the objects in charged of communicating the phones GPS to retrieve the location information.
 * It implements an observable interface to facilitate communication with observers such as the MainController
 */
public class LocationController implements LocationControllerObservable {

    private static final int INTERVAL_MILLIS = 100;
    private static final int REQUEST_CHECK_CODE = 1001;
    private final Activity activity;
    private final LocationRequest locationRequest;
    private final LocationCallback locationCallback;
    private final FusedLocationProviderClient fusedLocationProviderClient;

    private ArrayList<LocationControllerObserver> observers;


    /**
     * This is the constructor.
     * <p>
     *     A series of process are began. LocationRequest and LocationCallback are initialized to serve as parameters
     *     of the getFusedLocationProviderClient which is also initialized.
     * </p>
     * @param activity Activity object associated with the app starting
     */
    public LocationController(Activity activity) {
        this.activity = activity;
        locationRequest = createLocationRequest();
        locationCallback = createLocationCallback();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        observers = new ArrayList<>();
    }

    /**
     * Update the GPS this means getting the GPS ready for the requests is going to respond so it is up to date.
     * <p>
     *     Apart from the update it also is responsible of requesting the user to turn on the GPS if
     *     it is not on. It also verifies the permission to access location information is provided and if not
     *     it requests it
     * </p>
     */
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

    /**
     * Starts the looping request of location to the GPS
     */
    public void startContinuousLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    /**
     * Stops the looping request of location to the GPS
     */
    public void stopContinuousLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    // --------------------------------------------------------------------------------------------------------
    // METHODS REQUIRED TO GET GPS LOCATION PARAMETERS BASED ON https://www.youtube.com/watch?v=dPqivAUK8ps

    /**
     * Create location request for the fusedLocationClient.
     * <p>
     *     This request has a series of parameter regarding the quality and frequency of the request
     * </p>
     * @return LocationRequest
     */
        private LocationRequest createLocationRequest() {
        return (new LocationRequest.Builder(
                INTERVAL_MILLIS
        ).setGranularity(Granularity.GRANULARITY_FINE)
         .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
         //.setMinUpdateDistanceMeters(100)
                //.setMinUpdateIntervalMillis(100)
                //.setMaxUpdateDelayMillis(500)
                //.setIntervalMillis(100) //There is probably a time limit to how fast the GPS updates location https://stackoverflow.com/questions/64117840/how-to-make-locationmanager-get-very-frequent-location-updates
         .build());
    }

    /**
     * Create location callback for the fusedLocationClient.
     * <p>
     *     This method calls the updateLocationInfo method which communicates with the observers
     * </p>
     * @return LocationCallback
     */
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

    /**
     * Action trigger only called during the first update GPS not in loop request.
     * It communicates with observers if successful
     * @return OnSuccessListener
     */
    private OnSuccessListener<Location> setOnSuccessListener() {
        return new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                updateLocationInfo(location);
            }
        };
    }
//--------------------------------------------------------------------------------------------------

    /**
     * Triggered action to submit updated information regarding location to the observers
     * @param location
     */
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

    //Implementations of the abstract methods in the LocationControllerObservable interface
    @Override
    public void addObservers(LocationControllerObserver observer) {
        observers.add(observer);
    }
}
